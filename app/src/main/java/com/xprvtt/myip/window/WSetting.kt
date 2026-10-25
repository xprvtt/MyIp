package com.xprvtt.myip.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xprvtt.myip.R
import com.xprvtt.myip.SettingsDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.xprvtt.myip.InfoProfile

//--------------------------------------------------------------------------------------------------

@Composable
fun SettingsScreen( settingsDataStore: SettingsDataStore, modifier: Modifier = Modifier.fillMaxSize(), onBack: () -> Unit, onOpenHelp: () -> Unit, onOpenAddProfile: (InfoProfile) -> Unit, onOpenEditProfile: (String) -> Unit) {
    Box(modifier = modifier) {
        val profiles by settingsDataStore.profiles.collectAsState(initial = emptyList())
        val selectedProfileId by settingsDataStore.selectedProfileId.collectAsState(initial = null)
        val scope = rememberCoroutineScope()
        val uriHandler = LocalUriHandler.current

        // Заголовок и прокручиваемый список профилей
        Column( modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp) ) {

            Text( text = "Setting", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 16.dp), color = Color.White )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 135.dp),
                contentPadding = PaddingValues(top = 8.dp,bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = profiles,key = { it.id }) {
                    profile -> Card( modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp,end = 4.dp,top = 4.dp,bottom = 4.dp).clickable{
                                scope.launch{
                                    settingsDataStore.selectProfile(profile.id)
                                }
                            }
                        ) {
                            Text(text = profile.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = Color.White)

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                IconButton(
                                    onClick = {
                                        onOpenEditProfile(profile.id)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_edit),
                                        contentDescription = "Edit profile",
                                        tint = Color.Unspecified
                                    )
                                }

                                if (!profile.isDefaultProfile()) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                settingsDataStore.deleteProfile(profile.id)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_delete),
                                            contentDescription = "",
                                            tint = Color.Unspecified
                                        )
                                    }
                                }

                                RadioButton(
                                    selected = profile.id == selectedProfileId,
                                    onClick = {
                                        scope.launch {
                                            settingsDataStore.selectProfile(profile.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- GitHub ---
        Button(
            modifier = Modifier.align(Alignment.BottomStart).offset(y = (-15).dp, x = 15.dp).width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                uriHandler.openUri("https://github.com/xprvtt")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_github),
                contentDescription = "GitHub",
                tint = Color.Unspecified
            )
        }

        // --- Help ---
        var isPressedButtonHelp by remember { mutableStateOf(false) }
        val scopeButtonsHelp = rememberCoroutineScope()

        Button(
            enabled = !isPressedButtonHelp,
            modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-15).dp).width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                if (isPressedButtonHelp) return@Button

                isPressedButtonHelp = true

                scopeButtonsHelp.launch {
                    try {
                        onOpenHelp()
                    } finally {
                        isPressedButtonHelp = false
                    }
                }
            }
        ) {
            Text(text = "?", color = Color.White)
        }

        // --- Back ---
        var isPressedButtonBack by remember { mutableStateOf(false) }
        val scopeButtonsBack = rememberCoroutineScope()

        Button(
            enabled = !isPressedButtonBack,
            modifier = Modifier.align(Alignment.BottomEnd).offset(y = (-15).dp, x = (-15).dp).width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                if (isPressedButtonBack) return@Button

                isPressedButtonBack = true
                scopeButtonsBack.launch {
                    try {
                        onBack()
                        delay(2000)
                    } finally {
                        isPressedButtonBack = false
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Назад",
                tint = Color.Unspecified
            )
        }

        // --- add profile ---
        var isPressedButtonAdd by remember { mutableStateOf(false) }
        val scopeButtonsAdd = rememberCoroutineScope()

        Button(
            enabled = !isPressedButtonAdd,
            modifier = Modifier.align(Alignment.BottomEnd).offset(y = (-65).dp, x = (-15).dp).width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                if (isPressedButtonAdd) return@Button

                isPressedButtonAdd = true

                scopeButtonsAdd.launch {
                    try {
                        onOpenAddProfile(InfoProfile())
                    } finally {
                        isPressedButtonAdd = false
                    }
                }
            }
        ) {
            Text(text = "+", color = Color.White)
        }
    }
}

//--------------------------------------------------------------------------------------------------
