package com.xprvtt.myip

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.BroadcastReceiver
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//--------------------------------------------------------------------------------------------------

fun showIpNotification(context: Context, message: String) {
    val channelId = "ip_notification_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId,"IP notifications",NotificationManager.IMPORTANCE_HIGH)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    val repeatIntent = Intent(context, RepeatIpReceiver::class.java).setAction("REPEAT_IP_REQUEST")
    val repeatPendingIntent = PendingIntent.getBroadcast(context,1002, repeatIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_qs_check_ip)
        .setContentTitle("My IP")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(0, "Repeat", repeatPendingIntent)
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT < 33 || androidx.core.content.ContextCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED ) {
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}

//--------------------------------------------------------------------------------------------------

class RepeatIpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "REPEAT_IP_REQUEST") return

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val settingsDataStore = SettingsDataStore(appContext)
                val profiles = settingsDataStore.profiles.first()
                val selectedId = settingsDataStore.selectedProfileId.first()
                val profile = profiles.find { it.id == selectedId } ?: profiles.firstOrNull() ?: throw IllegalStateException("Profiles not found")

                showIpNotification(appContext, "Waiting...")

                val answer = getIpInfo(profile)

                showIpNotification(appContext, answer)

            }
            catch (e: Exception) {
                Log.e("RepeatIpReceiver", "IP request failed", e)
                showIpNotification(appContext, "Error getting IP")
            }
            finally {
                pendingResult.finish()
            }
        }
    }
}

//--------------------------------------------------------------------------------------------------
