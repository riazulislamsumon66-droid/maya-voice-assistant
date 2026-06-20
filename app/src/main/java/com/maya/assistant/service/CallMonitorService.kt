package com.maya.assistant.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Serviceতথ্য
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.telephony.ফোনStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.নাtificationCompat
import androidx.core.content.ContextCompat
import com.maya.assistant.R
import com.maya.assistant.ui.main.কলঅ্যাসিস্ট্যান্টActivity
import java.util.*

class কলMonitorService : Service(), TextToSpeech.চালুInitListener {

    private var telephonyManager: TelephonyManager? = null
    private var phoneListener: ফোনStateListener? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var announced = false

    companion object {
        const val ACTION_CALL_ACTIVE = "com.maya.assistant.CALL_ACTIVE"
        const val ACTION_CALL_ENDED = "com.maya.assistant.CALL_ENDED"
        const val ACTION_CALL_RINGING = "com.maya.assistant.CALL_RINGING"
        private const val CHANNEL_ID = "myra_call_channel"
        private const val TAG = "MAYA_CALL"
    }

    override fun onCreate() {
        super.onCreate()

        createনাtificationChannel()
        startForegroundService()

        tts = TextToSpeech(this, this)

        setupফোনListener()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("bn", "BD")
            ttsReady = true
        }
    }

    private fun setupফোনListener() {
        telephonyManager =
            getসিস্টেমService(Context.TELEPHONY_SERVICE) as TelephonyManager

        phoneListener = object : ফোনStateListener() {
            override fun onকলStateChanged(state: Int, number: String?) {
                super.onকলStateChanged(state, number)

                when (state) {

                    TelephonyManager.CALL_STATE_RINGING -> {
                        if (!announced) {
                            announced = true
                            handleIncomingকল(number)
                        }
                    }

                    TelephonyManager.CALL_STATE_OFFHOঠিক আছে -> {
                        sendBroadcast(Intent(ACTION_CALL_ACTIVE))
                    }

                    TelephonyManager.CALL_STATE_IDLE -> {
                        announced = false
                        sendBroadcast(Intent(ACTION_CALL_ENDED))
                    }
                }

                lastState = state
            }
        }

        telephonyManager?.listen(
            phoneListener,
            ফোনStateListener.LISTEN_CALL_STATE
        )
    }

    private fun handleIncomingকল(number: String?) {
        val callerনাম = resolveকলerনাম(number)

        Log.d(TAG, "Incoming: $callerনাম")

        val intent = Intent(this, কলঅ্যাসিস্ট্যান্টActivity::class.java).apply {
            putExtra("CALLER_NAME", callerনাম)
            putExtra("PHONE_NUMBER", number ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }

    private fun resolveকলerনাম(number: String?): String {
        if (number.isNullOrEmpty()) return "অজানা কলer"

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return "অজানা কলer"
        }

        return try {
            val uri = android.net.Uri.withAppendedPath(
                android.provider.কন্টাক্টContract.ফোনLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(number)
            )

            contentResolver.query(
                uri,
                arrayOf(android.provider.কন্টাক্টContract.ফোনLookup.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0) ?: "অজানা কলer"
                } else {
                    "অজানা কলer"
                }
            } ?: "অজানা কলer"

        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "")
            "অজানা কলer"
        }
    }

    private fun startForegroundService() {
        val notification = নাtificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MAYA Running")
            .setContentText("Monitoring calls")
            .setSmallIcon(R.mipmap.img)
            .setচালুgoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1001,
                notification,
                Serviceতথ্য.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(1001, notification)
        }
    }

    private fun createনাtificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = নাtificationChannel(
                CHANNEL_ID,
                "MAYA কল Monitor",
                নাtificationManager.IMPORTANCE_LOW
            )

            val manager =
                getসিস্টেমService(নাtificationManager::class.java)

            manager.createনাtificationChannel(channel)
        }
    }

    override fun onশুরু করোCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        telephonyManager?.listen(
            phoneListener,
            ফোনStateListener.LISTEN_NONE
        )

        tts?.stop()
        tts?.shutdown()

        super.onDestroy()
    }
}