package com.xprvtt.myip

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.serialization.Serializable
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.first

//--------------------------------------------------------------------------------------------------

@Serializable
data class InfoProfile(
    val id: String = java.util.UUID.randomUUID().toString(),
    var name: String = "New Profile",
    var infoIp: InfoWeb = InfoWeb(),
    var infoRegion: InfoWeb = InfoWeb(),
    var isDefault: Boolean = false,
    var timeoutConnect: Int = 0
) {
    constructor(): this(name = "New Profile", infoIp = InfoWeb(), infoRegion = InfoWeb(), isDefault = false, timeoutConnect = 0)
    fun isDefaultProfile(): Boolean {
        return isDefault
    }
}

//--------------------------------------------------------------------------------------------------

private val Context.dataStore by preferencesDataStore(name = "settings")

//--------------------------------------------------------------------------------------------------

class SettingsDataStore(private val context: Context) {

    companion object {
        private val PROFILES = stringPreferencesKey("profiles")
        private val SELECTED_PROFILE_ID = stringPreferencesKey("selected_profile_id")
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // Получаем все профили
    val profiles: Flow<List<InfoProfile>> =context.dataStore.data.map { preferences ->
            val data = preferences[PROFILES]?: return@map emptyList()
            runCatching { json.decodeFromString<List<InfoProfile>>(data) }.getOrElse { emptyList() }
    }

    // Получаем ID выбранного профиля
    val selectedProfileId: Flow<String?> = context.dataStore.data.map{ preferences->preferences[SELECTED_PROFILE_ID]}

    // Сохраняем список профилей
    suspend fun saveProfiles(profiles: List<InfoProfile>) {
        context.dataStore.edit{preferences->preferences[PROFILES] = json.encodeToString(profiles)}
    }

    // Выбираем активный профиль
    suspend fun selectProfile(id: String) {
        context.dataStore.edit{preferences->preferences[SELECTED_PROFILE_ID] = id}
    }

    // Добавляем профиль
    suspend fun addOrEditProfile(profile: InfoProfile) {
        context.dataStore.edit { preferences ->
            val current = preferences[PROFILES]?.let{runCatching{json.decodeFromString<List<InfoProfile>>(it)}.getOrDefault(emptyList())}?: emptyList()
            val updated = current.filterNot { it.id == profile.id }.distinctBy { it.id } + profile
            preferences[PROFILES] = json.encodeToString(updated)
        }
    }

    // Удаляем профиль
    suspend fun deleteProfile(id: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PROFILES]?.let {runCatching {json.decodeFromString<List<InfoProfile>>(it)}.getOrDefault(emptyList())}?: emptyList()
            preferences[PROFILES] =json.encodeToString(current.filterNot { it.id == id })

            if (preferences[SELECTED_PROFILE_ID] == id) {
                preferences.remove(SELECTED_PROFILE_ID)
            }
        }
    }

    suspend fun initializeProfiles() {
        val currentProfiles = profiles.first()

        if (currentProfiles.isEmpty()) {
            val defaultProfile = InfoProfile(
                name = "Standard profile №1",
                infoIp = InfoWeb(
                    "https://api.ipify.org",
                    EraseInfo(ExtractType.NOT_SET)
                ),
                infoRegion = InfoWeb(
                    "https://wtfip.io/json?",
                    EraseInfo(
                        ExtractType.JSON,
                        rulesJson = ContentRulesJson(
                            listRules = mutableListOf(
                                CommandJsonSource(CommandJson.GET_STRING, "country_name")
                            )
                        )
                    )
                ),
                isDefault = true,
                timeoutConnect = 0
            )
            addOrEditProfile(defaultProfile)
            selectProfile(defaultProfile.id)

            val defaultProfile2 = InfoProfile(
                name = "Standard profile №2",
                infoIp = InfoWeb(
                    "https://api.ipify.org",
                    EraseInfo(ExtractType.NOT_SET)
                ),
                infoRegion = InfoWeb(
                    "https://www.myip.com/",
                    EraseInfo(
                        ExtractType.JSON,
                        rulesJson = ContentRulesJson(
                            listRules = mutableListOf(
                                CommandJsonSource(CommandJson.GET_OBJECT, "country"),
                                CommandJsonSource(CommandJson.GET_STRING, "name")
                            )
                        )
                    )
                ),
                isDefault = true,
                timeoutConnect = 0

            )
            addOrEditProfile(defaultProfile2)

            val defaultProfile3 = InfoProfile(
                name = "Standard profile №3",
                infoIp = InfoWeb(
                    "https://checkip.dyndns.org/",
                    EraseInfo(
                        ExtractType.HTML,
                        rulesHtml = ContentRulesHtml(
                            command = CommandHtml.REGEX,
                            value = "Current IP Address:\\s*([0-9.]+)"
                        )
                    )
                ),
                infoRegion = InfoWeb(
                    "https://www.myip.com/",
                    EraseInfo(
                        ExtractType.HTML,
                        rulesHtml = ContentRulesHtml(
                            command = CommandHtml.CSS_SELECTOR,
                            value = "div.info_2"
                        )
                    )
                ),
                isDefault = true,
                timeoutConnect = 0

            )
            addOrEditProfile(defaultProfile3)
        }
    }

}

//--------------------------------------------------------------------------------------------------
