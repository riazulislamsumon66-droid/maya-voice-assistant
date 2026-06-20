package com.maya.assistant.ui.settings

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.Componentনাম
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.কন্টাক্টContract
import android.provider.সেটিংস
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.maya.assistant.R
import com.maya.assistant.service.অ্যাক্সেসিবিলিটিসাহায্যerService
import com.maya.assistant.service.MayaDeviceঅ্যাডমিনReceiver
import com.maya.assistant.security.নিরাপত্তাসেটিংসActivity


class সেটিংসActivity : AppCompatActivity() {

    // ── Original Views ──────────────────────────────────────────
    private lateinit var apiKeyInput: এডিট করোText
    private lateinit var ttsApiKeyInput: এডিট করোText
    private lateinit var userনামInput: এডিট করোText
    private lateinit var primeনামInput: এডিট করোText
    private lateinit var primeNumberInput: এডিট করোText
    private lateinit var personalityGroup: RadioGroup
    private lateinit var voiceTypeGroup: RadioGroup
    private lateinit var liveModeSwitch: Switch
    private lateinit var saveBtn: Button
    private lateinit var accessibilityStatus: TextView
    private lateinit var adminStatusText: TextView
    private lateinit var pickContactBtn: ImageButton

    // ── NEW Views ─────────────────────────────────────────────
    private lateinit var callAnnounceSwitch: Switch
    private lateinit var callAnnounceStatusText: TextView
    private lateinit var grantPermissionsBtn: Button
    private lateinit var setডিফল্টঅ্যাসিস্ট্যান্টBtn: Button
    private lateinit var permissionsStatusText: TextView

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentনাম: Componentনাম

    private val contactPickerLauncher = registerForActivityResult(
        ActivityResultContracts.শুরু করোActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_ঠিক আছে) {
            val contactUri = result.data?.data ?: return@registerForActivityResult
            handleContactResult(contactUri)
        }
    }

    // প্রয়োজন permissions list
    private val allPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        devicePolicyManager = getসিস্টেমService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentনাম = Componentনাম(this, MayaDeviceঅ্যাডমিনReceiver::class.java)

        initViews()
        loadPreferences()
        setupListeners()
        updateStatus()
    }

    private fun initViews() {
        // Original views
        apiKeyInput = findViewById(R.id.apiKeyInput)
        ttsApiKeyInput = findViewById(R.id.ttsApiKeyInput)
        userনামInput = findViewById(R.id.userনামInput)
        primeনামInput = findViewById(R.id.primeনামInput)
        primeNumberInput = findViewById(R.id.primeNumberInput)
        personalityGroup = findViewById(R.id.personalityGroup)
        voiceTypeGroup = findViewById(R.id.voiceTypeGroup)
        liveModeSwitch = findViewById(R.id.liveModeSwitch)
        saveBtn = findViewById(R.id.saveBtn)
        accessibilityStatus = findViewById(R.id.accessibilityStatus)
        adminStatusText = findViewById(R.id.adminStatusText)
        pickContactBtn = findViewById(R.id.pickContactBtn)

        // NEW views — add these to your XML layout
        callAnnounceSwitch = findViewById(R.id.callAnnounceSwitch)
        callAnnounceStatusText = findViewById(R.id.callAnnounceStatusText)
        grantPermissionsBtn = findViewById(R.id.grantPermissionsBtn)
        setডিফল্টঅ্যাসিস্ট্যান্টBtn = findViewById(R.id.setডিফল্টঅ্যাসিস্ট্যান্টBtn)
        permissionsStatusText = findViewById(R.id.permissionsStatusText)
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        apiKeyInput.setText(prefs.getString("api_key", ""))
        ttsApiKeyInput.setText(prefs.getString("tts_api_key", ""))
        userনামInput.setText(prefs.getString("user_name", "Sir"))
        primeনামInput.setText(prefs.getString("prime_name", ""))
        primeNumberInput.setText(prefs.getString("prime_number", ""))

        when (prefs.getString("personality_mode", "gf")) {
            "gf" -> findViewById<RadioButton>(R.id.gfRadio).isChecked = true
            "professional" -> findViewById<RadioButton>(R.id.proRadio).isChecked = true
            else -> findViewById<RadioButton>(R.id.assistantRadio).isChecked = true
        }

        when (prefs.getString("voice_engine", "system")) {
            "elevenlabs" -> findViewById<RadioButton>(R.id.radioEleven).isChecked = true
            else -> findViewById<RadioButton>(R.id.radioসিস্টেম).isChecked = true
        }

        liveModeSwitch.isChecked = prefs.getBoolean("live_mode_enabled", false)

        // কল announce (default ON for better UX)
        val announceচালু = prefs.getBoolean("call_announce_enabled", true)
        callAnnounceSwitch.isChecked = announceচালু
        updateকলAnnounceStatus(announceচালু)
    }

    private fun setupListeners() {
        saveBtn.setচালুClickListener { savePreferences() }

        pickContactBtn.setচালুClickListener {
            val intent = Intent(Intent.ACTION_PICK, কন্টাক্টContract.CommonDataKinds.ফোন.CONTENT_URI)
            contactPickerLauncher.launch(intent)
        }

        // Original cards
        findViewById<View>(R.id.accessibilityCard).setচালুClickListener {
            startActivity(Intent(android.provider.সেটিংস.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<View>(R.id.deviceঅ্যাডমিনCard).setচালুClickListener {
            if (!devicePolicyManager.isঅ্যাডমিনসক্রিয়(componentনাম)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentনাম)
                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "MAYA needs admin to control system.")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "অ্যাডমিন is already active ✅", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<View>(R.id.securityসেটিংসCard).setচালুClickListener {
            startActivity(Intent(this, নিরাপত্তাসেটিংসActivity::class.java))
        }

        // NEW: কল announce switch
        callAnnounceSwitch.setচালুCheckedChangeListener { _, isChecked ->
            updateকলAnnounceStatus(isChecked)
        }

        // NEW: Grant permissions button
        grantPermissionsBtn.setচালুClickListener {
            checkAndRequestPermissions()
        }

        // NEW: Set default assistant button
        setডিফল্টঅ্যাসিস্ট্যান্টBtn.setচালুClickListener {
            try {
                startActivity(Intent(সেটিংস.ACTION_VOICE_INPUT_SETTINGS))
                Toast.makeText(this, "MAYA ko ডিফল্ট অ্যাসিস্ট্যান্ট chuno 👆", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "সেটিংস → Apps → ডিফল্ট Apps → অ্যাসিস্ট্যান্ট → MAYA", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * NEW: Check and request all required permissions
     */
    private fun checkAndRequestPermissions() {
        val missing = allPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isনাtEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Sab permissions already granted! ✅", Toast.LENGTH_SHORT).show()
            updatePermissionsStatus()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val granted = grantResults.count { it == PackageManager.PERMISSION_GRANTED }
            val total = permissions.size

            if (granted == total) {
                Toast.makeText(this, "Sab permissions mil gayi! ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "$granted/$total permissions mili ⚠️", Toast.LENGTH_LONG).show()

                val permanentlyঅস্বীকৃত = permissions.filterIndexed { index, _ ->
                    grantResults[index] == PackageManager.PERMISSION_DENIED &&
                    !ActivityCompat.shouldদেখাওRequestPermissionRationale(this, permissions[index])
                }

                if (permanentlyঅস্বীকৃত.isনাtEmpty()) {
                    AlertDialog.Builder(this)
                        .setTitle("Permissions প্রয়োজন ⚠️")
                        .setমেসেজ("কিছু permissions permanently deny ho gayi hain. সেটিংস se manually enable karo.\n\n" +
                                permanentlyঅস্বীকৃত.joinToString("\n") { "• ${it.split('.').last()}" })
                        .setPositiveButton("খোলো সেটিংস") { _, _ ->
                            try {
                                val intent = Intent(সেটিংস.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageনাম, null)
                                }
                                startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(this, "সেটিংস open nahi ho paya", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("বাতিল", null)
                        .show()
                }
            }
            updatePermissionsStatus()
        }
    }

    private fun updateকলAnnounceStatus(enabled: Boolean) {
        callAnnounceStatusText.text = if (enabled) {
            "📢 কল aane pe MAYA naam bolegi"
        } else {
            "🔇 কল announce band hai"
        }
        callAnnounceStatusText.setTextরঙ(
            if (enabled) 0xFF00E676.toInt() else 0xFFFF1744.toInt()
        )
    }

    private fun updatePermissionsStatus() {
        val missing = allPermissions.count {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        permissionsStatusText.text = when {
            missing == 0 -> "✅ সব Permissions মঞ্জুর ✅"
            missing <= 2 -> "⚠️ $missing permissions pending"
            else -> "❌ $missing permissions missing"
        }
        permissionsStatusText.setTextরঙ(when {
            missing == 0 -> 0xFF00E676.toInt()
            missing <= 2 -> 0xFFFFA726.toInt()
            else -> 0xFFFF1744.toInt()
        })
    }

    private fun handleContactResult(uri: android.net.Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor?.moveToFirst() == true) {
            val nameIndex = cursor.getColumnIndex(কন্টাক্টContract.CommonDataKinds.ফোন.DISPLAY_NAME)
            val numIndex = cursor.getColumnIndex(কন্টাক্টContract.CommonDataKinds.ফোন.NUMBER)
            primeনামInput.setText(cursor.getString(nameIndex))
            primeNumberInput.setText(cursor.getString(numIndex).replace(" ", ""))
        }
        cursor?.close()
    }

    private fun savePreferences() {
        val prefs = getSharedPreferences("maya_prefs", Context.MODE_PRIVATE).edit()

        prefs.putString("api_key", apiKeyInput.text.toString().trim())
        prefs.putString("tts_api_key", ttsApiKeyInput.text.toString().trim())
        prefs.putString("user_name", userনামInput.text.toString().trim())
        prefs.putString("prime_name", primeনামInput.text.toString().trim())
        prefs.putString("prime_number", primeNumberInput.text.toString().trim())

        val personality = when (personalityGroup.checkedRadioButtonId) {
            R.id.gfRadio -> "gf"
            R.id.proRadio -> "professional"
            else -> "assistant"
        }
        prefs.putString("personality_mode", personality)

        val voiceEngine = when (voiceTypeGroup.checkedRadioButtonId) {
            R.id.radioEleven -> "elevenlabs"
            else -> "system"
        }
        prefs.putString("voice_engine", voiceEngine)

        prefs.putBoolean("live_mode_enabled", liveModeSwitch.isChecked)
        prefs.putBoolean("call_announce_enabled", callAnnounceSwitch.isChecked)

        prefs.apply()
        Toast.makeText(this, "সেটিংস সেভ করোd! MAYA updated. ✅", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateStatus() {
        val enabled = অ্যাক্সেসিবিলিটিসাহায্যerService.isচালু(this)
        accessibilityStatus.text = if (enabled) "✅ Full Control চালু" else "❌ অ্যাক্সেসিবিলিটি বন্ধ"
        accessibilityStatus.setTextরঙ(if (enabled) 0xFF00E676.toInt() else 0xFFFF1744.toInt())

        val adminসক্রিয় = devicePolicyManager.isঅ্যাডমিনসক্রিয়(componentনাম)
        adminStatusText.text = if (adminসক্রিয়) "✅ অ্যাডমিন সক্রিয়" else "❌ অ্যাডমিন নিষ্ক্রিয়"
        adminStatusText.setTextরঙ(if (adminসক্রিয়) 0xFF00E676.toInt() else 0xFFFF1744.toInt())

        updatePermissionsStatus()
    }

    override fun onচালিয়ে যাও() {
        super.onচালিয়ে যাও()
        updateStatus()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 200
    }
}