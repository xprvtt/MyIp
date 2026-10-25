package com.xprvtt.myip.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xprvtt.myip.R
import com.xprvtt.myip.SettingsDataStore
import com.xprvtt.myip.getIpInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


//--------------------------------------------------------------------------------------------------

@Composable
fun MainScreen(modifier: Modifier = Modifier, settingsDataStore : SettingsDataStore, onOpenSettings: () -> Unit) {

    val profiles by settingsDataStore.profiles.collectAsState(initial = emptyList())
    val selectedProfileId by settingsDataStore.selectedProfileId.collectAsState(initial = null)
    val profile = profiles.find {it.id == selectedProfileId}

    var answer by remember { mutableStateOf("Your IP will appear here") }
    val scope = rememberCoroutineScope()


    Box(modifier = modifier) {

        // --- TITLE ---
        Text(text = "Check IP anytime",color = Color.White, fontSize = 26.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp))

        // --- BUTTON GET IP---
        var buttonGetIpText by remember { mutableStateOf("Get IP") }
        var isLoading by remember { mutableStateOf(false)}
        var isGetAnswer by remember { mutableStateOf(false) }

        Button(
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.Center).offset(y = 70.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                buttonGetIpText = "Waiting..."
                answer = ". . ."
                isLoading = true
                isGetAnswer = false

                scope.launch{
                    try {
                        val currentProfile = profile?: return@launch
                        answer = getIpInfo(currentProfile)
                    }
                    finally {
                        buttonGetIpText = "Get IP"
                        isLoading = false;
                        isGetAnswer = true;
                    }
                }
            }
        ) {
            Text(text = buttonGetIpText, color = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-130).dp)
                .fillMaxWidth(0.9f)
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .padding(8.dp)
        ){
            Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
                SelectionContainer {
                    Text(
                        text = answer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        color = Color.White
                    )
                }
            }

        }

        // --- BUTTON COPY ---
        if (isGetAnswer)
        {
            val clipboardManager = LocalClipboardManager.current
            val scopeButtonCope = rememberCoroutineScope()
            var isPressedButtonCopy by remember  {mutableStateOf(false)}

            Button(
                colors =  ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
                modifier = Modifier.align(Alignment.CenterEnd).offset(y = 70.dp, x = (-15).dp).width(65.dp),
                enabled = !isPressedButtonCopy,
                onClick = {
                    clipboardManager.setText(AnnotatedString(answer))
                    isPressedButtonCopy = true
                    scopeButtonCope.launch {
                        delay(500.milliseconds)
                        isPressedButtonCopy = false
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }

        // --- SETTING ---
        val scopeButtonSetting = rememberCoroutineScope()
        var isPressedButtonSetting by remember  {mutableStateOf(false)}

        Button(
            enabled = !(isLoading || isPressedButtonSetting),
            modifier = Modifier.align(Alignment.BottomEnd).offset(y = (-15).dp, x = (-15).dp).width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                isPressedButtonSetting = true
                scopeButtonSetting.launch {
                    try{
                        onOpenSettings()
                    }
                    finally {
                        isPressedButtonSetting = false
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "",
                tint = Color.Unspecified
            )
        }

    }
}

//--------------------------------------------------------------------------------------------------
