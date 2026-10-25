package com.xprvtt.myip

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException
import org.jsoup.Jsoup
//--------------------------------------------------------------------------------------------------

// Возвращает результат в виде 2 строк, 1 строка с ip, 2 строка с регионом ( отформатированного по MutableList)
suspend fun getIpInfo(profile: InfoProfile) : String {

    var result: String = "IP: "

    // IP
    try {
        result += sendRequestToWebsite(profile.infoIp.link, profile.infoIp.rulesCuttingForGetInfo, profile.timeoutConnect)
    }
    catch (e: Exception) {
        result += "IP Error: ${e.message ?: "Request failed"}\nURL: ${profile.infoIp.link} \n"
    }

    result += "\nRegion: "

    // IP region
    try {
        result += sendRequestToWebsite(profile.infoRegion.link, profile.infoRegion.rulesCuttingForGetInfo, profile.timeoutConnect)
    }
    catch (e: Exception) {
        result += "Region Error: ${e.message ?: "Request failed"}\nURL: ${profile.infoRegion.link} \n"
    }

    return result
}

//--------------------------------------------------------------------------------------------------

suspend fun sendRequestToWebsite(website: String, eraseRules: EraseInfo, timeout: Int): String = withContext(Dispatchers.IO, ) {

    var connection: HttpURLConnection? = null

    try {
        connection = URL(website).openConnection() as HttpURLConnection

        connection.connectTimeout = timeout
        connection.readTimeout = timeout
        connection.requestMethod = "GET"

        val statusCode = connection.responseCode
        val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use{ it.readText() } ?: "Not text in response"

        if (statusCode !in 200..299) {
            return@withContext "Error HTTP $statusCode: $response\nURL: $website"
        }

        if (eraseRules.isSet()) {
            substringContentIp(response, eraseRules)
        }
        else {
            response
        }

    }
    catch (e: CancellationException) {
        throw e
    }
    catch (e: Exception) {
        Log.e("IP_REQUEST", "Error request to $website", e)
        "Error: ${e.javaClass.simpleName}: ${e.message}\nURL: $website, timeout: $timeout"
    }
    finally {
        connection?.disconnect()
    }
}

//--------------------------------------------------------------------------------------------------

suspend fun substringContentIp(content: String, eraseContent: EraseInfo) : String {
    if (eraseContent.type == ExtractType.TEXT){
        return extractText(content, eraseContent)
    }
    else if (eraseContent.type == ExtractType.JSON){
        return extractJson(content, eraseContent)
    }
    else if (eraseContent.type == ExtractType.HTML){
        return extractHtml(content, eraseContent)
    }
    else if (eraseContent.type == ExtractType.XML){
        return extractXml(content, eraseContent)
    }

    return content
}

//--------------------------------------------------------------------------------------------------

suspend fun extractText(content:String, eraseContent: EraseInfo): String{
    val mode = eraseContent.rulesText.charToSave
    val isIp = eraseContent.rulesText.isIp
    val start = eraseContent.rulesText.startIndex.coerceIn(0, content.length)

    if (mode == -1) {
        return content.substring(start, content.length)
    }
    else if (mode == 0){
        if (isIp) {

            val regex = Regex("""(?<![\d.])(?:\d{1,3}\.){3}\d{1,3}(?![\d.])""")
            return regex.find(content, start)?.value ?: ""
        } else {
            val start = eraseContent.rulesText.startIndex.coerceIn(0, content.length)
            if (start == content.length) return ""

            val end = content.indexOf(' ', start).let { if (it == -1) content.length else it }
            return content.substring(start, end).trim('\"', '\'', ',', '.', ' ', '"')
        }
    }
    else if (mode > 0){
        return content.substring(start, eraseContent.rulesText.charToSave)
    }
    else
    {
        return content
    }
}

//--------------------------------------------------------------------------------------------------

fun extractJson(content: String, eraseContent: EraseInfo): String {
    val rules = eraseContent.rulesJson
    return try {
        var current: Any = JSONObject(content)

        for (rule in rules.listRules) {
            if (rule.command == CommandJson.NOT_SET){
                continue
            }
            current = when (rule.command) {
                CommandJson.GET_OBJECT->(current as JSONObject).getJSONObject(rule.key)
                CommandJson.GET_ARRAY->(current as JSONObject).getJSONArray(rule.key)
                CommandJson.GET_STRING->(current as JSONObject).getString(rule.key)
                CommandJson.GET_VALUE ->(current as JSONObject).get(rule.key)
                else -> return ""
            }
        }
        current.toString()
    } catch (e: Exception) {
        "Error in fun: extractJson ${e.message}"
    }
}

//--------------------------------------------------------------------------------------------------

fun extractHtml(content: String, eraseContent: EraseInfo) : String {
    return getStringInXmlOrHtml(content, eraseContent)
}

//--------------------------------------------------------------------------------------------------


fun extractXml(content: String, eraseContent: EraseInfo) : String {
    return getStringInXmlOrHtml(content, eraseContent)
}

//--------------------------------------------------------------------------------------------------

fun getStringInXmlOrHtml(content: String, eraseContent: EraseInfo) : String {

    val doc = Jsoup.parse(content)

    return when (eraseContent.rulesHtml.command) {
        CommandHtml.NOT_SET -> content

        CommandHtml.CSS_SELECTOR ->doc.selectFirst(eraseContent.rulesHtml.value)?.text() ?: "Does not contain (CSS_SELECTOR) with (${eraseContent.rulesHtml.value})"

        CommandHtml.ATTRIBUTE -> {
            val selector = eraseContent.rulesHtml.value
            val attribute = eraseContent.rulesHtml.attribute
            doc.selectFirst(selector)?.attr(attribute) ?: "Does not contain (ATTRIBUTE)  with (${eraseContent.rulesHtml.value})"
        }

        CommandHtml.REGEX -> {
            val regex = Regex(eraseContent.rulesHtml.value)
            val match = regex.find(doc.html())
            match?.groups?.get(1)?.value ?: match?.value ?: "Does not contain (REGEX)  with (${eraseContent.rulesHtml.value})"
        }

        CommandHtml.BODY_TEXT -> doc.body()?.text() ?: "Does not contain (BODY_TEXT)"

        CommandHtml.ALL_TEXT -> doc.text()
    }
}
//--------------------------------------------------------------------------------------------------
