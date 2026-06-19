# 🎙️ MAYA — Android AI Voice Assistant

**প্রজেক্ট তথ্য:**
- **নাম:** MAYA (আগে MYRA)
- **প্রকার:** Production-ready Android AI Voice Assistant App
- **ভাষা:** Kotlin + Android Studio
- **আর্কিটেকচার:** MVVM
- **প্যাকেজ:** `com.maya.assistant`
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34

**ভাষা সাপোর্ট:** বাংলা + English + Hindi (Hinglish/বাংলিশ)

**AI Engine:** Google Gemini Live API via WebSocket (BidiGenerateContent)

**তৈরি:** ২০২৫, Sumon Sir এর জন্য ❤️

---

## 📋 ফিচার সমূহ

### 🎤 ভয়েস ফিচার
- Gemini Live WebSocket এ real-time ভয়েস কথা
- PCM Native Audio (16kHz mic → 24kHz speaker)
- ৮টি ভয়েস অপশন (Aoede, Charon, Kore, Fenrir, Puck, Leda, Orus, Zephyr)
- Push-to-talk mic button
- Long press to interrupt/stop

### 💬 চ্যাট সিস্টেম
- Real-time chat UI
- User এবং MAYA এর আলাদা bubble
- Transcript deduplication

### 📞 কল ম্যানেজমেন্ট
- Incoming call detection
- কলার announce করা (বাংলা/English/Hindi)
- Voice দিয়ে call accept/reject

### ⭐ প্রাইম কন্ট্যাক্ট
- একাধিক প্রাইম কন্ট্যাক্ট সেভ
- Voice command দিয়ে call/message

### 🔧 ফোন কমান্ড
- App open/close
- Call, SMS, WhatsApp
- Volume control
- WiFi, Bluetooth, Flashlight
- Torch on/off

### 🎭 পার্সোনালিটি মোড
- **GF Mode** 💖 — বাংলিশ/হিন্দিশ, warm & caring
- **Professional Mode** 💼 — Formal English
- **Assistant Mode** 🤖 — Balanced bilingual

### 🫧 ওভারলে
- Floating orb overlay
- Double power press দিয়ে ওপেন
- Draggable

### ⚙️ সেটিংস
- API Key, Name, Model, Voice, Personality
- Prime contacts management
- Accessibility status

---

## 📁 ফাইল স্ট্রাকচার

```
app/src/main/java/com/maya/assistant/
├── ai/
│   ├── GeminiLiveClient.kt
│   ├── AudioEngine.kt
│   └── CommandParser.kt
├── model/
│   └── AppCommand.kt
├── service/
│   ├── AccessibilityHelperService.kt
│   ├── CallMonitorService.kt
│   ├── MyraOverlayService.kt
│   ├── PowerButtonReceiver.kt
│   └── BootReceiver.kt
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt
│   │   ├── OrbAnimationView.kt
│   │   └── UiComponents.kt
│   └── settings/
│       └── SettingsActivity.kt
└── viewmodel/
    └── MainViewModel.kt
```

---

## 🔄 প্রগ্রেস

- [ ] প্রজেক্ট সেটআপ
- [ ] GeminiLiveClient.kt
- [ ] AudioEngine.kt
- [ ] OrbAnimationView.kt
- [ ] WaveformView.kt
- [ ] CommandParser.kt (বাংলা + English + Hindi)
- [ ] MainViewModel.kt
- [ ] MainActivity.kt
- [ ] CallMonitorService.kt
- [ ] AccessibilityHelperService.kt
- [ ] MyraOverlayService.kt
- [ ] PowerButtonReceiver.kt
- [ ] SettingsActivity.kt
- [ ] সব XML layouts
- [ ] সব drawable XMLs
- [ ] AndroidManifest.xml
- [ ] build.gradle
