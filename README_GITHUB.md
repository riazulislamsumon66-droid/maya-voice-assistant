# Maya Voice Assistant

Production-ready Android AI Voice Assistant App built with Kotlin + Gemini Live WebSocket.

## Features

- **AI Voice**: Gemini Live API via WebSocket (BidiGenerateContent)
- **Audio**: Native PCM — 16kHz mic input, 24kHz speaker output
- **Languages**: Bangla (বাংলা) + English + Hindi — all three supported
- **Voice Commands**: Open/close apps, calls, SMS, WhatsApp, volume, flashlight, WiFi, Bluetooth
- **Call Handling**: Incoming call detection + voice-controlled accept/reject
- **Prime Contacts**: Multiple priority contacts with quick voice dial
- **Overlay**: Floating orb (double power press to open)
- **Personalities**: GF Mode 💖 / Professional 💼 / Assistant 🤖

## Tech Stack

- Kotlin + Android Studio
- MVVM Architecture
- OkHttp WebSocket
- AudioRecord / AudioTrack (PCM native)
- Custom Canvas animations (Orb)

## Setup

1. Open in Android Studio
2. Add Gemini API Key in Settings
3. Run on device (Android 8.0+)

## Project Structure

```
app/src/main/java/com/maya/assistant/
├── ai/
│   ├── GeminiLiveClient.kt      ← WebSocket, keepalive, session renewal
│   ├── AudioEngine.kt           ← AudioRecord + AudioTrack + queue
│   └── CommandParser.kt         ← Voice text → AppCommand (Bangla+English+Hindi)
├── model/
│   └── AppCommand.kt
├── service/
│   ├── AccessibilityHelperService.kt
│   ├── CallMonitorService.kt
│   ├── MayaOverlayService.kt
│   ├── PowerButtonReceiver.kt
│   └── BootReceiver.kt
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt
│   │   ├── OrbAnimationView.kt
│   │   ├── WaveformView.kt
│   │   └── UiComponents.kt
│   └── settings/
│       └── SettingsActivity.kt
└── viewmodel/
    └── MainViewModel.kt
```

## Voice Commands (Examples)

| Bangla | English | Hindi |
|--------|---------|-------|
| "YouTube kholo" | "open YouTube" | "YouTube kholo" |
| "amma ke call karo" | "call mom" | "mummy ko call karo" |
| "whatsapp band karo" | "close WhatsApp" | "WhatsApp band karo" |
| "torch jalao" | "flashlight on" | "torch on karo" |
| "gan bajao" | "play music" | "gana bajao" |

## License

© 2025 — Sumon Sir
