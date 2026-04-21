// Copyright Sierra

package ai.sierra.sdk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat

internal class VoiceSessionService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = buildNotification()
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                } else {
                    0
                }
            )
        } catch (e: Exception) {
            Log.e(VOICE_TAG, "Failed to start foreground service", e)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Voice Call",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Active voice conversation"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Voice call in progress")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "sierra_voice_channel"
        private const val NOTIFICATION_ID = 9201

        private fun isServiceDeclaredInManifest(context: Context): Boolean {
            return try {
                val componentName = android.content.ComponentName(context, VoiceSessionService::class.java)
                context.packageManager.getServiceInfo(componentName, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }

        fun start(context: Context) {
            if (!isServiceDeclaredInManifest(context)) {
                Log.e(
                    VOICE_TAG,
                    "VoiceSessionService is not declared in the merged manifest. " +
                        "Add the sierra-android-sdk-voice dependency to your app module."
                )
                return
            }
            try {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, VoiceSessionService::class.java)
                )
            } catch (e: Exception) {
                Log.e(
                    VOICE_TAG,
                    "Failed to start VoiceSessionService. " +
                        "Ensure the sierra-android-sdk-voice module is included and the " +
                        "manifest declares the service.",
                    e
                )
            }
        }

        fun stop(context: Context) {
            try {
                context.stopService(Intent(context, VoiceSessionService::class.java))
            } catch (e: Exception) {
                Log.e(VOICE_TAG, "Failed to stop VoiceSessionService", e)
            }
        }
    }
}
