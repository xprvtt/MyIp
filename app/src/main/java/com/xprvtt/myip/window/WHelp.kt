package com.xprvtt.myip.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xprvtt.myip.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//--------------------------------------------------------------------------------------------------

@Composable
fun HelpScreen(modifier: Modifier = Modifier.fillMaxSize(), onBack: () -> Unit) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            // --- TITLE ---
            Text( text = "Help", fontSize = 26.sp, modifier = Modifier.padding(top = 26.dp), color = Color.White )

            // --- HELP INFO ---
            Text(
//                text = "Because responses from sites can be of various types, the response is accepted in raw form.\n" +
//                        "\n" +
//                        "This could be an XML page, JSON, or other format. Analyze the response and find the part of the response you need.\n" +
//                        "\n" +
//                        "Parameters:\n" +
//                        "Index - the starting index of the information you need;\n" +
//                        "CharsToSave - the number of characters to retain from the response.\n" +
//                        "\n" +
//                        "Example, answer:\n"+
//                        "\n" +
//                        "<!DOCTYPE html>\n" +
//                        "<html lang=\"en\" class=\"dark\">\n" +
//                        "<head>\n" +
//                        "    <link rel=\"canonical\" href=\"\n" +
//                        "...\n" +
//                        "Ip: 1.1.1.1\n" +
//                        "...\n" +
//                        "\n" +
//                        "Index N, CharsToSave = 7\n" +
//                        "Result answer: \"1.1.1.1\"\n" +
//                        "It is better to use sites that send a minimal response.",
                text = "",
                modifier = Modifier.padding(top = 16.dp).offset(x = 10.dp),
                color = Color.White
            )
        }

        // --- BUTTON BACK ---
        var isPressedButtonBack by remember {mutableStateOf(false)}
        val scopeButtonsBack =  rememberCoroutineScope()

        Button(
            enabled = !isPressedButtonBack,
            modifier = Modifier.align(Alignment.BottomEnd).offset(y = (-15).dp, x = (-15).dp).width(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)),
            onClick = {
                if (isPressedButtonBack) return@Button

                isPressedButtonBack = true

                scopeButtonsBack.launch {
                    onBack()
                    delay(2000)
                    isPressedButtonBack = false
                }
            }
        ) {
            Icon( painter = painterResource(id = R.drawable.ic_back), contentDescription = "", tint = Color.Unspecified )
        }
    }
}

//--------------------------------------------------------------------------------------------------
