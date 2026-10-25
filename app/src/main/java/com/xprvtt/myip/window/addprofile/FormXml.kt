package com.xprvtt.myip.window.addprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xprvtt.myip.CommandXml
import com.xprvtt.myip.InfoWeb

@Composable
fun ExpandedInputXmlRules(infoWeb: InfoWeb, onInfoWeb: (InfoWeb) -> Unit) {
    var xmlCommand by remember { mutableStateOf(infoWeb.rulesCuttingForGetInfo.rulesXml) }

    Column() {
        // command
        var expandedDropMenu by remember { mutableStateOf(false) }
        OutlinedButton(
            onClick = { expandedDropMenu = true },
            modifier = Modifier.height(50.dp).offset(0.dp, 3.dp)
        ) {
            Text(xmlCommand.command.name)
        }

        DropdownMenu(
            expanded = expandedDropMenu,
            onDismissRequest = { expandedDropMenu = false }

        ) {
            CommandXml.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        xmlCommand.command = type
                        expandedDropMenu = false
                        onInfoWeb(
                            infoWeb.copy(
                                rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                                    rulesXml = infoWeb.rulesCuttingForGetInfo.rulesXml.copy(
                                        command = type
                                    )
                                )
                            )
                        )
                    }
                )
            }
        }


        //value
        OutlinedTextField(
            value = infoWeb.rulesCuttingForGetInfo.rulesXml.value,
            onValueChange = { newValue ->
                onInfoWeb(
                    infoWeb.copy(
                        rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                            rulesXml = infoWeb.rulesCuttingForGetInfo.rulesXml.copy(
                                value = newValue
                            )
                        )
                    )
                )
            },
            label = { Text("value:") },
            modifier = Modifier.height(60.dp),
            singleLine = true,
            shape = RoundedCornerShape(45.dp),
            textStyle = LocalTextStyle.current.copy(lineHeight = 15.sp)
        )

        //value
        OutlinedTextField(
            value = infoWeb.rulesCuttingForGetInfo.rulesXml.attribute,
            onValueChange = { newAttribute ->
                onInfoWeb(
                    infoWeb.copy(
                        rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                            rulesXml = infoWeb.rulesCuttingForGetInfo.rulesXml.copy(
                                attribute = newAttribute
                            )
                        )
                    )
                )
            },
            label = { Text("attribute:") },
            modifier = Modifier.height(60.dp),
            singleLine = true,
            shape = RoundedCornerShape(45.dp),
            textStyle = LocalTextStyle.current.copy(lineHeight = 15.sp)
        )
    }
}
