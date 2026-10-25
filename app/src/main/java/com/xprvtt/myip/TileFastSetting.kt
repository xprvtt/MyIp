package com.xprvtt.myip

import android.app.Activity
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle

//--------------------------------------------------------------------------------------------------

class CheckIpTileService : TileService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, CollapseShadeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            startActivityAndCollapse(pendingIntent)
        }
        else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }

        serviceScope.launch {
            val settingsDataStore = SettingsDataStore(applicationContext)

            val profiles = settingsDataStore.profiles.first()
            val selectedId = settingsDataStore.selectedProfileId.first()

            val profile = profiles.find { it.id == selectedId }?: profiles.firstOrNull()?: return@launch

            val answer = getIpInfo(profile)

            withContext(Dispatchers.Main) {
                showIpNotification(this@CheckIpTileService, answer)
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}

//--------------------------------------------------------------------------------------------------

class CollapseShadeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}

//--------------------------------------------------------------------------------------------------
