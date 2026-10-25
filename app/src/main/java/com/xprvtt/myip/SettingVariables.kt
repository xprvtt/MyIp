package com.xprvtt.myip

import kotlinx.serialization.Serializable

//--------------------------------------------------------------------------------------------------

@Serializable
enum class ExtractType {
    NOT_SET,
    TEXT,
    JSON,
    HTML,
    XML
}

//--------------------------------------------------------------------------------------------------

@Serializable
enum class CommandJson {
    NOT_SET,
    GET_STRING,
    GET_OBJECT,
    GET_ARRAY,
    GET_VALUE,
}

//--------------------------------------------------------------------------------------------------

@Serializable
enum class CommandHtml {
    NOT_SET,
    CSS_SELECTOR,
    ATTRIBUTE,
    REGEX,
    BODY_TEXT,
    ALL_TEXT
}


//--------------------------------------------------------------------------------------------------

@Serializable
enum class CommandXml {
    NOT_SET,
    CSS_SELECTOR,
    ATTRIBUTE,
    REGEX,
    BODY_TEXT,
    ALL_TEXT
}
//--------------------------------------------------------------------------------------------------

@Serializable
data class CommandJsonSource(
    var command: CommandJson = CommandJson.NOT_SET,
    var key: String = ""
){
    constructor(): this(command = CommandJson.NOT_SET, key = "")
}

//--------------------------------------------------------------------------------------------------

@Serializable
data class ContentRulesText(
    var startIndex: Int = 0,
    var charToSave: Int = 0,
    var isIp: Boolean = true
)

//--------------------------------------------------------------------------------------------------

@Serializable
data class ContentRulesJson(
    var listRules: MutableList<CommandJsonSource> = mutableListOf()

)

//--------------------------------------------------------------------------------------------------

@Serializable
data class ContentRulesHtml(
    var command: CommandHtml = CommandHtml.NOT_SET,
    var value: String = "",
    var attribute: String = ""
)

//--------------------------------------------------------------------------------------------------

@Serializable
data class ContentRulesXml(
    var command: CommandXml = CommandXml.NOT_SET,
    var value: String = "",
    var attribute: String = ""
)

//--------------------------------------------------------------------------------------------------
@Serializable
data class InfoWeb(
    var link: String = "",
    var rulesCuttingForGetInfo: EraseInfo = EraseInfo(),
){
    fun isSetRulesErase(): Boolean{
        return rulesCuttingForGetInfo.isSet()
    }
}

//--------------------------------------------------------------------------------------------------

@Serializable
data class EraseInfo(
    var type: ExtractType = ExtractType.NOT_SET,
    var rulesText: ContentRulesText = ContentRulesText(),
    var rulesJson: ContentRulesJson = ContentRulesJson(),
    var rulesHtml: ContentRulesHtml = ContentRulesHtml(),
    var rulesXml: ContentRulesXml = ContentRulesXml()
){
    fun isSet() : Boolean {
        return type != ExtractType.NOT_SET
    }
}

//--------------------------------------------------------------------------------------------------
