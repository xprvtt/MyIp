package com.xprvtt.myip.window.addprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xprvtt.myip.InfoWeb

//--------------------------------------------------------------------------------------------------

@Composable
fun ExpandedInputFormForTextRules(infoWeb: InfoWeb, nameValOne: String, nameValSecond: String, onInfoWeb: (InfoWeb) -> Unit) {

    var startIndexText by remember(infoWeb) {mutableStateOf(infoWeb.rulesCuttingForGetInfo.rulesText.startIndex.toString())}
    var charToSaveText by remember(infoWeb) {mutableStateOf(infoWeb.rulesCuttingForGetInfo.rulesText.charToSave.toString())}

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(0.dp, (15).dp)
    ) {
        OutlinedTextField(
            value = startIndexText,
            onValueChange = { text ->
                startIndexText = text
                text.toIntOrNull()?.let { newValue ->
                    onInfoWeb(
                        infoWeb.copy(
                            rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                                rulesText = infoWeb.rulesCuttingForGetInfo.rulesText.copy(
                                    startIndex = newValue
                                )
                            )
                        )
                    )
                }
            },
            label = { Text("$nameValOne: ") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        // ввод для char to save 0 - откл
        OutlinedTextField(
            value = charToSaveText,
            onValueChange = { text ->
                charToSaveText = text
                text.toIntOrNull()?.let { newValue ->
                    onInfoWeb(
                        infoWeb.copy(
                            rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                                rulesText = infoWeb.rulesCuttingForGetInfo.rulesText.copy(
                                    charToSave = newValue
                                )
                            )
                        )
                    )
                }
            },
            label = { Text("$nameValSecond: ") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
    }
}

//--------------------------------------------------------------------------------------------------