package com.xprvtt.myip.window.addprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xprvtt.myip.ExtractType
import com.xprvtt.myip.InfoProfile
import com.xprvtt.myip.InfoWeb
import com.xprvtt.myip.R
import com.xprvtt.myip.SettingsDataStore
import kotlinx.coroutines.launch

//--------------------------------------------------------------------------------------------------
@Composable
fun AddProfileScreen(settingsDataStore: SettingsDataStore, profile: InfoProfile = InfoProfile(), modifier: Modifier = Modifier.fillMaxSize(), onBack: () -> Unit) {

    var tempProfile by remember { mutableStateOf(profile.copy()) }

    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 26.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- TITLE ---
            Text(
                text = "Add profile",
                fontSize = 26.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White
            )
            HorizontalDivider()

            Column() {
                // --- NAME PROFILE ---
                OutlinedTextField(
                    value = tempProfile.name,
                    onValueChange = { newName ->
                        tempProfile = tempProfile.copy(name = newName)
                    },
                    label = { Text("Profile name:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                var timeoutText by remember { mutableStateOf(tempProfile.timeoutConnect.toString()) }

                // --- TIMEOUT ---
                OutlinedTextField(
                    value = timeoutText,
                    onValueChange = { text: String ->
                        timeoutText = text

                        text.toIntOrNull()?.let { newValue ->
                            tempProfile.timeoutConnect = newValue
                        }
                    },
                    label = { Text("Timeout (ms). 0 - no limit:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                HorizontalDivider(modifier = Modifier.padding(0.dp, 15.dp))

                // --- IP ---
                OutlinedTextField(
                    value = tempProfile.infoIp.link,
                    onValueChange = { newLink ->
                        tempProfile = tempProfile.copy(
                            infoIp = tempProfile.infoIp.copy(link = newLink)
                        )
                    },
                    label = { Text("Source IP URL:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                RadioButtonForInputRulesForm(
                    "Select Response Parsing Method",
                    tempProfile.infoIp.rulesCuttingForGetInfo.type
                ) { newType ->
                    tempProfile = tempProfile.copy(
                        infoIp = tempProfile.infoIp.copy(
                            rulesCuttingForGetInfo = tempProfile.infoIp.rulesCuttingForGetInfo.copy(type = newType)
                        )
                    )
                }
                InputFormSelector(
                    tempProfile.isDefaultProfile(),
                    tempProfile.infoIp.rulesCuttingForGetInfo.type,
                    tempProfile.infoIp,
                ) { newInfoWeb ->
                    tempProfile = tempProfile.copy(infoIp = newInfoWeb)
                }
                HorizontalDivider(modifier = Modifier.padding(0.dp, 15.dp))

                // --- Region ---
                OutlinedTextField(
                    value = tempProfile.infoRegion.link,
                    onValueChange = { newLink ->
                        tempProfile = tempProfile.copy(
                            infoRegion = tempProfile.infoRegion.copy(link = newLink)
                        )
                    },
                    label = { Text("Source Region URL:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                RadioButtonForInputRulesForm(
                    "Select Response Parsing Method",
                    tempProfile.infoRegion.rulesCuttingForGetInfo.type
                ) { newType ->
                    tempProfile = tempProfile.copy(
                        infoRegion = tempProfile.infoRegion.copy(
                            rulesCuttingForGetInfo = tempProfile.infoRegion.rulesCuttingForGetInfo.copy(type = newType)
                        )
                    )
                }
                InputFormSelector(
                    tempProfile.isDefaultProfile(),
                    tempProfile.infoRegion.rulesCuttingForGetInfo.type,
                    tempProfile.infoRegion,
                ) { newInfoWeb ->
                    tempProfile = tempProfile.copy(
                        infoRegion = newInfoWeb
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(0.dp, 15.dp))
            }
        }

        // --- BUTTON SAVE ---
        val scopeAddButton = rememberCoroutineScope()
        Button(
            enabled = !tempProfile.isDefaultProfile(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 15.dp, bottom = 65.dp)
                .width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                scopeAddButton.launch {
                    settingsDataStore.addOrEditProfile(tempProfile)
                    onBack()
                }
            }
        ) {
            Text("+", color = Color.White)
        }

        // --- BUTTON BACK ---
        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 15.dp, bottom = 15.dp)
                .width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Назад",
                tint = Color.Unspecified
            )
        }
    }
}

//--------------------------------------------------------------------------------------------------

@Composable
fun InputFormSelector(isDefault: Boolean, selectedTypeInputRules: ExtractType, infoWeb: InfoWeb, onInfoWeb: (InfoWeb) -> Unit){
    when (selectedTypeInputRules) {
        ExtractType.TEXT -> {
            ExpandedInputFormForTextRules(
                infoWeb,
                "Start index (int)",
                "Chars to Save. -1 = off,\n0 = Standart save",
                onInfoWeb = onInfoWeb
            )
        }
        ExtractType.JSON -> {
            ExpandedInputFormForJsonRules(
                isDefault,
                infoWeb,
                onInfoWeb = onInfoWeb
            )
        }
        ExtractType.HTML -> {
            ExpandedInputHtmlRules(
                infoWeb,
                onInfoWeb = onInfoWeb
            )
        }
        ExtractType.XML -> {
            ExpandedInputXmlRules(
                infoWeb,
                onInfoWeb = onInfoWeb
            )
        }
        else -> {
            //expandedInputTextRules(infoWeb, "Source IP", "Source Region")
        }
    }
}

//--------------------------------------------------------------------------------------------------

@Composable
fun RadioButtonForInputRulesForm(name: String, selectedTypeInputRules: ExtractType, onSelect: (ExtractType) -> Unit){

    Text(name, color = Color.White, modifier = Modifier
        .offset(0.dp, 10.dp)
        .padding(bottom = 11.dp))

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 5.dp) {
        for (type in ExtractType.entries){
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedTypeInputRules == type,
                    onClick = {
                        onSelect(type)
                    }
                )
                Text(type.name, color = Color.White)
            }
        }
    }
}
//--------------------------------------------------------------------------------------------------
