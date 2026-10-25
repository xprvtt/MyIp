package com.xprvtt.myip.window.addprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xprvtt.myip.CommandJsonSource
import com.xprvtt.myip.InfoWeb
import com.xprvtt.myip.CommandJson
import com.xprvtt.myip.R

//--------------------------------------------------------------------------------------------------

@Composable
fun ExpandedInputFormForJsonRules(isDefault: Boolean, infoWeb: InfoWeb, onInfoWeb: (InfoWeb) -> Unit ) {
    val currentRules = infoWeb.rulesCuttingForGetInfo.rulesJson.listRules

    Column( verticalArrangement = Arrangement.spacedBy(12.dp) ) {
        Text("Parse the JSON response ->")

        for (jCommand in currentRules) {
            Text("Next command ->")

            Row( horizontalArrangement = Arrangement.spacedBy(8.dp) ) {
                JsonCommandInputForm(
                    isDefault,
                    jsonCommand = jCommand,
                    onDelete = {
                        val currentRules = currentRules.filter{ it !== jCommand }.toMutableList()

                        onInfoWeb(
                            infoWeb.copy(
                                rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                                    rulesJson = infoWeb.rulesCuttingForGetInfo.rulesJson.copy(
                                        listRules = currentRules
                                    )
                                )
                            )
                        )
                    },
                    onSetCommand = { jsonCommand ->
                        infoWeb.copy(
                            rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                                rulesJson = infoWeb.rulesCuttingForGetInfo.rulesJson.copy(
                                    listRules = currentRules
                                )
                            )
                        )
                    }
                )
            }
        }
        // --- ADD NEW COMMAND ---
        Button(
            enabled = !isDefault,
            colors = ButtonDefaults.buttonColors( containerColor = Color(0xFF6650a4)),
            onClick = {
                val newRules = currentRules.toMutableList().apply{ add(CommandJsonSource()) }
                onInfoWeb(
                    infoWeb.copy(
                        rulesCuttingForGetInfo = infoWeb.rulesCuttingForGetInfo.copy(
                            rulesJson = infoWeb.rulesCuttingForGetInfo.rulesJson.copy(
                                    listRules = newRules
                            )
                        )
                    )
                )
            }
        ) {
            Text("Add new command")
        }
    }
}

@Composable
fun JsonCommandInputForm(isDefault: Boolean, jsonCommand: CommandJsonSource, onDelete: () -> Unit, onSetCommand: (CommandJsonSource) -> Unit) {
    Column() {
        // command
        var expandedDropMenu by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                OutlinedButton(
                    onClick = { expandedDropMenu = true },
                    modifier = Modifier.height(50.dp).offset(0.dp, 3.dp)
                ) {
                    Text(jsonCommand.command.name)
                }

                DropdownMenu(
                    expanded = expandedDropMenu,
                    onDismissRequest = { expandedDropMenu = false }

                ) {
                    CommandJson.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                jsonCommand.command = type
                                expandedDropMenu = false
                                onSetCommand(jsonCommand)
                            }
                        )
                    }
                }
            }

            //key
            OutlinedTextField(
                value = jsonCommand.key,
                onValueChange = {
                    jsonCommand.key = it
                    onSetCommand(jsonCommand)
                },
                label = { Text("Key:") },
                modifier = Modifier.weight(1f).height(60.dp),
                singleLine = true,
                shape = RoundedCornerShape(45.dp),
                textStyle = LocalTextStyle.current.copy(lineHeight = 15.sp)
            )

            // delete
            IconButton(
                enabled = !isDefault,
                onClick = onDelete
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

//--------------------------------------------------------------------------------------------------