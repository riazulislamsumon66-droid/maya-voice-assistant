package com.maya.assistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.IBinder
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.maya.assistant.R
import com.maya.assistant.ui.main.MainActivity

class CallMonitorService : Service() {

    companion object {
        private const val TAG = "CallMonitorService"
        private const val CHANNEL_ID = "maya_call_monitor"
        private const val NOTIFICATION_ID = 1002
    }

    private lateinit var telephonyManager: TelephonyManager
    private var phoneStateListener: PhoneStateListener? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupCallListener()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MAYA Call Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors incoming calls for MAYA"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MAYA")
            .setContentText("Call monitoring active")
            .setSmallIcon(R.drawable.ic_maya_notif)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun setupCallListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            @Deprecated("Deprecated in Java")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        val callerName = resolveCallerName(phoneNumber)
                        Log.d(TAG, "Incoming call from: $callerName ($phoneNumber)")
                        sendCallIntent(callerName)
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        sendBroadcast(Intent("com.myra.CALL_ENDED"))
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call in progress — do nothing
                    }
                }
            }
        }
        try {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up call listener: ${e.message}")
        }
    }

    private fun resolveCallerName(phoneNumber: String?): String {
        if (phoneNumber.isNullOrEmpty()) return "Unknown"

        val context = applicationContext
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
            ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?",
            arrayOf("%${phoneNumber.takeLast(7)}"),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(0) ?: "Unknown"
            }
        }
        return phoneNumber
    }

    private fun sendCallIntent(callerName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("INCOMING_CALL", true)
            putExtra("CALLER_NAME", callerName)
        }
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        phoneStateListener?.let {
            try {
                telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE)
            } catch (e: Exception) {}
        }
    }
}
