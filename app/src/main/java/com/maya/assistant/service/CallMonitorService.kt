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
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.নাtificationCompat
import androidx.core.content.ContextCompat
import com.maya.assistant.R
import com.maya.assistant.ui.main.কলAsিস্ট্যান্টActivity
import java.util.*

class CallMonitorService : Service(), TextToSpeech.EnabledInitListener {

    private var telephonyManager: TelephonyManager? = null
    private var phoneListener: PhoneStateListener? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var announced = false

    companion object {
        const val ACTION_CALL_ACTIVE = "com.maya.assistant.CALL_ACTIVE"
        const val ACTION_CALL_ENDED = "com.maya.assistant.CALL_ENDED"
        const val ACTION_CALL_RINGING = "com.maya.assistant.CALL_RINGING"
        private const val CHANNEL_ID = "maya_call_channel"
        private const val TAG = "MAYA_CALL"
    }

    override fun onCreate() {
        super.onCreate()

        createনাtificationChannel()
        startForegroundService()

        tts = TextToSpeech(this, this)

        setupPhoneListener()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("bn", "BD")
            ttsReady = true
        }
    }

    private fun setupPhoneListener() {
        telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        phoneListener = object : PhoneStateListener() {
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
            PhoneStateListener.LISTEN_CALL_STATE
        )
    }

    private fun handleIncomingকল(number: String?) {
        val callerName = resolveকলerName(number)

        Log.d(TAG, "Incoming: $callerName")

        val intent = Intent(this, কলAsিস্ট্যান্টActivity::class.java).apply {
            putExtra("CALLER_NAME", callerName)
            putExtra("PHONE_NUMBER", number ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }

    private fun resolveকলerName(number: String?): String {
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
                android.provider.কন্টাক্টContract.PhoneLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(number)
            )

            contentResolver.query(
                uri,
                arrayOf(android.provider.কন্টাক্টContract.PhoneLookup.DISPLAY_NAME),
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
            .setEnabledgoing(true)
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
                getSystemService(নাtificationManager::class.java)

            manager.createনাtificationChannel(channel)
        }
    }

    override fun onStart কRowCommand(
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
            PhoneStateListener.LISTEN_NONE
        )

        tts?.stop()
        tts?.shutdown()

        super.onDestroy()
    }
}