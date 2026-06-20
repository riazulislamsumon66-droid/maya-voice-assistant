package com.maya.assistant.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maya.assistant.R
import com.maya.assistant.service.AccessibilityHelperService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Build
import org.json.JSONArray
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences

    // UI elements
    private lateinit var apiKeyInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var modelSpinner: Spinner
    private lateinit var voiceSpinner: Spinner
    private lateinit var personalityGf: RadioButton
    private lateinit var personalityPro: RadioButton
    private lateinit var personalityAssistant: RadioButton
    private lateinit var accessibilityStatus: TextView
    private lateinit var voiceAuthStatus: TextView
    private lateinit var enrollVoiceBtn: Button
    private lateinit var primeContactsRecycler: RecyclerView
    private lateinit var addPrimeContactBtn: Button
    private lateinit var saveButton: Button

    private lateinit var primeContactAdapter: PrimeContactAdapter
    private val primeContacts = mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)

        initViews()
        loadSettings()
        setupListeners()
    }

    private fun initViews() {
        apiKeyInput = findViewById(R.id.apiKeyInput)
        userNameInput = findViewById(R.id.userNameInput)
        modelSpinner = findViewById(R.id.modelSpinner)
        voiceSpinner = findViewById(R.id.voiceSpinner)
        personalityGf = findViewById(R.id.personalityGf)
        personalityPro = findViewById(R.id.personalityPro)
        personalityAssistant = findViewById(R.id.personalityAssistant)
        accessibilityStatus = findViewById(R.id.accessibilityStatus)
        voiceAuthStatus = findViewById(R.id.voiceAuthStatus)
        enrollVoiceBtn = findViewById(R.id.enrollVoiceBtn)
        primeContactsRecycler = findViewById(R.id.primeContactsRecycler)
        addPrimeContactBtn = findViewById(R.id.addPrimeContactBtn)
        saveButton = findViewById(R.id.saveButton)

        // Model spinner
        val models = arrayOf(
            "Native Audio (Human Voice) — DEFAULT",
            "Flash Live (Fast)",
            "Pro Audio Dialog"
        )
        modelSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, models)

        // Voice spinner
        val voices = arrayOf("Aoede (Female)", "Charon (Male)", "Kore (Female)", "Fenrir (Male)", "Puck (Male)", "Leda (Female)", "Orus (Male)", "Zephyr (Female)")
        voiceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, voices)

        // Prime contacts
        primeContactAdapter = PrimeContactAdapter(primeContacts) { index ->
            primeContacts.removeAt(index)
            primeContactAdapter.notifyDataSetChanged()
        }
        primeContactsRecycler.apply {
            layoutManager = LinearLayoutManager(this@SettingsActivity)
            adapter = primeContactAdapter
        }
    }

    private fun loadSettings() {
        apiKeyInput.setText(prefs.getString("api_key", ""))
        userNameInput.setText(prefs.getString("user_name", ""))

        // Model
        val model = prefs.getString("gemini_model", "models/gemini-2.5-flash-native-audio-preview-12-2025")
        modelSpinner.setSelection(when (model) {
            "models/gemini-2.0-flash-live-001" -> 1
            "models/gemini-2.5-flash-preview-native-audio-dialog" -> 2
            else -> 0
        })

        // Voice
        val voice = prefs.getString("gemini_voice", "Aoede")
        voiceSpinner.setSelection(when (voice) {
            "Charon" -> 1
            "Kore" -> 2
            "Fenrir" -> 3
            "Puck" -> 4
            "Leda" -> 5
            "Orus" -> 6
            "Zephyr" -> 7
            else -> 0
        })

        // Personality
        when (prefs.getString("personality_mode", "gf")) {
            "gf" -> personalityGf.isChecked = true
            "professional" -> personalityPro.isChecked = true
            "assistant" -> personalityAssistant.isChecked = true
        }

        // Accessibility
        updateAccessibilityStatus()

        // Prime contacts
        loadPrimeContacts()
    }

    private fun loadPrimeContacts() {
        val json = prefs.getString("prime_contacts_json", null)
        primeContacts.clear()
        if (json != null) {
            try {
                val arr = JSONArray(json)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    primeContacts.add(Pair(obj.getString("name"), obj.getString("number")))
                }
            } catch (e: Exception) {
                // Try legacy migration
                val oldName = prefs.getString("prime_name", null)
                val oldNumber = prefs.getString("prime_number", null)
                if (oldName != null && oldNumber != null) {
                    primeContacts.add(Pair(oldName, oldNumber))
                }
            }
        }
        primeContactAdapter.notifyDataSetChanged()
    }

    private fun setupListeners() {
        // Accessibility status click
        accessibilityStatus.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        // Add prime contact
        addPrimeContactBtn.setOnClickListener {
            showAddPrimeContactDialog()
        }

        // Voice enrollment
        enrollVoiceBtn.setOnClickListener {
            enrollVoice()
        }

        // Save
        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun enrollVoice() {
        enrollVoiceBtn.isEnabled = false
        enrollVoiceBtn.text = "🎙️ Recording... Speak now!"
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val coreService = com.maya.assistant.service.MayaCoreService.instance
                if (coreService != null) {
                    coreService.setVoiceEnrolled()
                }
                withContext(Dispatchers.Main) {
                    enrollVoiceBtn.text = "✅ Voice enrolled!"
                    voiceAuthStatus.text = "✅ Voice enrolled — only your voice will work for commands"
                    voiceAuthStatus.setTextColor(getColor(R.color.maya_success))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    enrollVoiceBtn.text = "❌ Failed — try again"
                    enrollVoiceBtn.isEnabled = true
                }
            }
        }
    }

    private fun showAddPrimeContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_prime_contact, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogNameInput)
        val numberInput = dialogView.findViewById<EditText>(R.id.dialogNumberInput)

        AlertDialog.Builder(this)
            .setTitle("Add Prime Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val number = numberInput.text.toString().trim()
                if (name.isNotEmpty() && number.isNotEmpty()) {
                    primeContacts.add(Pair(name, number))
                    primeContactAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveSettings() {
        val modelValue = when (modelSpinner.selectedItemPosition) {
            1 -> "models/gemini-2.0-flash-live-001"
            2 -> "models/gemini-2.5-flash-preview-native-audio-dialog"
            else -> "models/gemini-2.5-flash-native-audio-preview-12-2025"
        }

        val voiceValue = when (voiceSpinner.selectedItemPosition) {
            1 -> "Charon"
            2 -> "Kore"
            3 -> "Fenrir"
            4 -> "Puck"
            5 -> "Leda"
            6 -> "Orus"
            7 -> "Zephyr"
            else -> "Aoede"
        }

        val personalityValue = when {
            personalityGf.isChecked -> "gf"
            personalityPro.isChecked -> "professional"
            else -> "assistant"
        }

        // Save prime contacts as JSON
        val jsonArray = JSONArray()
        for ((name, number) in primeContacts) {
            jsonArray.put(JSONObject().apply {
                put("name", name)
                put("number", number)
            })
        }

        prefs.edit().apply {
            putString("api_key", apiKeyInput.text.toString().trim())
            putString("user_name", userNameInput.text.toString().trim())
            putString("gemini_model", modelValue)
            putString("gemini_voice", voiceValue)
            putString("personality_mode", personalityValue)
            putString("prime_contacts_json", jsonArray.toString())
            apply()
        }

        Toast.makeText(this, "Settings saved! Restart app to apply changes.", Toast.LENGTH_LONG).show()

        // Start MayaCoreService after saving settings
        try {
            val coreIntent = Intent(this, com.maya.assistant.service.MayaCoreService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(coreIntent)
            } else {
                startService(coreIntent)
            }
        } catch (e: Exception) {
            // Service may already be running
        }
    }

    private fun updateAccessibilityStatus() {
        if (AccessibilityHelperService.isEnabled()) {
            accessibilityStatus.text = "✅ Accessibility: ON"
            accessibilityStatus.setTextColor(getColor(R.color.maya_success))
        } else {
            accessibilityStatus.text = "❌ Accessibility: OFF — Tap to enable"
            accessibilityStatus.setTextColor(getColor(R.color.maya_red))
        }
    }

    override fun onResume() {
        super.onResume()
        updateAccessibilityStatus()
    }
}

// Prime Contact Adapter
class PrimeContactAdapter(
    private val contacts: List<Pair<String, String>>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<PrimeContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.primeItemName)
        val numberText: TextView = view.findViewById(R.id.primeItemNumber)
        val deleteBtn: ImageButton = view.findViewById(R.id.primeItemDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prime_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, number) = contacts[position]
        holder.nameText.text = name
        holder.numberText.text = number
        holder.deleteBtn.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = contacts.size
}
