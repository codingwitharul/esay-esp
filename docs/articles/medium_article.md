# The Painless Way to Connect ESP32 Devices to Wi-Fi on Android 📱⚡

**How to build seamless IoT provisioning flows using Jetpack Compose, Ktor, and MVI Architecture.**

If you’ve ever built an IoT product, you know the biggest hurdle isn't writing the firmware or building the cloud backend—it’s the **onboarding process**. Getting a screenless microcontroller (like an ESP32) connected to a user's home Wi-Fi network is notoriously frustrating. 

Users have to leave your app, go to their phone’s Wi-Fi settings, connect to a temporary hotspot, ignore the "Internet may not be available" warnings, go back to the app, enter their credentials, and pray it works.

As Android developers, we can do better. 

Today, I am open-sourcing **[EasyESP Connect](https://github.com/codingwitharul/esay-esp)**—a battle-tested Kotlin Multiplatform (KMM) Library, Android App, and companion ESP32 firmware that makes provisioning ESP32 devices incredibly simple, resilient, and fully automated.

---

### The Android IoT Routing Problem 🚧

Before diving into the solution, let’s talk about why Android makes IoT provisioning so hard. 

When your phone connects to an ESP32's temporary Wi-Fi hotspot, that hotspot doesn't have internet access. Modern Android versions (Android 9+) are smart—if they detect a Wi-Fi network has no internet, they will silently route all HTTP requests through the user's Cellular Data instead. 

This means when your app tries to send the Wi-Fi password to `http://192.168.4.1`, the request goes to the cellular tower, times out, and the setup fails. 

### The Solution: EasyESP Connect ✨

EasyESP Connect solves this (and many other edge cases) by taking full control of the Android Networking stack and providing a beautiful, state-driven Jetpack Compose UI. 

**Key Features:**
*   **Automated Device Discovery:** Passively scans for local ESP32 devices broadcasting setup SSIDs using Android's `WifiManager`.
*   **Programmatic Hotspot Binding:** Uses `WifiNetworkSpecifier` (Android 10+) to securely connect the phone to the ESP32 *without leaving the app*, and explicitly binds the app's process to the Wi-Fi network so HTTP requests don't leak to cellular data.
*   **Smart Fallbacks:** If the OS blocks the automatic connection, it gracefully degrades to a guided manual connection screen.
*   **ESP32 Dual Mode:** The firmware runs in `WIFI_AP_STA` mode, acting as a configuration portal while simultaneously scanning the environment for home routers.

---

### How to use it in your app 🛠️

I built EasyESP Connect to be as flexible as possible. Whether you want a plug-and-play UI or you want to build your own custom screens, integration takes minutes.

#### Method 1: The Drop-In Compose UI
If you are building a modern Jetpack Compose app, you can drop the pre-built `EasyEspSetupUI` directly into your screen. It handles permissions, Wi-Fi scanning, Lottie animations, and error states automatically.

```kotlin
import com.easyesp.shared.ui.EasyEspSetupUI
import com.easyesp.shared.mvi.EasyEspConfig

@Composable
fun ProvisioningScreen() {
    EasyEspSetupUI(
        config = EasyEspConfig(scanTimeoutMillis = 60_000L),
        onSuccess = { 
            println("ESP Device Provisioned Successfully! 🎉") 
        },
        onError = { error -> 
            println("Setup Failed: $error") 
        }
    )
}
```

#### Method 2: The Activity Result API
If your app is built with XML Views or Fragments, you can launch the Compose flow using a standard `ActivityResultContract` and just wait for the callback:

```kotlin
val launcher = registerForActivityResult(EasyEspSetupContract()) { result ->
    result.onSuccess { 
        // Device is connected!
    }.onFailure { error ->
        // Handle failure
    }
}

// Start Setup Flow
launcher.launch(EasyEspConfig())
```

#### Method 3: Headless MVI (Build Your Own UI)
For developers who want complete control over the pixel-perfect design of their app, EasyESP provides a headless **MVI (Model-View-Intent) Session**. 

You simply send `EasyEspIntent`s to the session and observe the `EasyEspState`. The library handles all the ugly networking, timeouts, and state transitions in the background.

```kotlin
val session = EasyEsp.createSession(context, coroutineScope, EasyEspConfig())

// 1. Observe State
lifecycleScope.launch {
    session.state.collect { state -> 
        when(state) {
            is EasyEspState.Discovering -> showCustomRadarAnimation()
            is EasyEspState.EspDevicesFound -> displayDeviceList(state.devices)
            is EasyEspState.Provisioning -> showLoadingSpinner()
            is EasyEspState.Success -> navigateToDashboard()
            // ...
        }
    }
}

// 2. Send Intents to control the flow
session.sendIntent(EasyEspIntent.CheckPreconditionsAndStart)
```

---

### The Firmware Side 🔌

An IoT library is useless without the hardware script. Inside the repository, you will find `esp32_wifi_setup.ino`. 

Flash this to your ESP32 via the Arduino IDE. It uses a lightweight asynchronous WebServer to expose two endpoints:
1.  `GET /networks` — Returns a JSON array of surrounding Wi-Fi networks and signal strengths (RSSI).
2.  `POST /credentials` — Accepts the target `ssid` and `password` from the Android app, drops the temporary hotspot, and connects to the home router.

Because it uses standard HTTP (via Ktor on Android), you can easily extend this to pass user tokens, MQTT broker URLs, or custom sensor data during the setup phase!

---

### Get Started Today 🚀

Building IoT products is hard enough; onboarding shouldn't be. 

You can find the complete source code, the KMM shared library, the Jetpack Compose app, and the ESP32 firmware on GitHub. 

🔗 **GitHub Repository:** [codingwitharul/esay-esp](https://github.com/codingwitharul/esay-esp)
📚 **Full Documentation:** [EasyESP Documentation](https://codingwitharul.github.io/esay-esp/)

If this helps you build your next hardware project, drop a star on the repo! Happy coding! 👨‍💻💡
