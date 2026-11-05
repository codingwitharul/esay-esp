# EasyESP Connect

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#)
[![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)](#)
[![ESP32](https://img.shields.io/badge/ESP32-E7352C?style=for-the-badge&logo=espressif&logoColor=white)](#)

**EasyESP Connect** is a battle-tested Kotlin Multiplatform (KMM) Library and companion ESP32 Firmware that makes provisioning ESP32 devices to a local Wi-Fi network incredibly simple and resilient.

**[📚 View the Full Documentation and Implementation Guide](https://codingwitharul.github.io/esay-esp/)**

## ✨ Key Features

*   **Automated Device Discovery**: Uses Android's `WifiManager` to passively scan for local ESP32 devices broadcasting setup SSIDs.
*   **Programmatic Hotspot Binding**: Bypasses OS restrictions to securely and automatically connect the user's phone to the ESP32 without leaving your app (using `WifiNetworkSpecifier`).
*   **MVI Architecture**: A robust `EasyEspSession` allows you to control the entire flow via code without being forced to use a specific UI.
*   **Ready-to-use Compose UI**: Drop in a pre-built Jetpack Compose Setup UI (`EasyEspSetupUI`) that handles everything visually, or launch it via `ActivityResultContract`.
*   **Open Network Configuration via Dual Mode**: The firmware (`WIFI_AP_STA`) acts as a configuration portal while simultaneously scanning the environment for home routers.

## 🚀 Quick Start Integration

### 1. Add Dependencies
First, add JitPack to your project-level `settings.gradle.kts` (or `build.gradle`):
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then, add the library to your app-level `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.github.codingwitharul:esay-esp:1.0.0")
}
```

Ensure your `AndroidManifest.xml` includes the necessary networking/location permissions and the **Cleartext traffic flag** to allow local HTTP connections:
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

### 2. Implement in your App
You can use the built-in Jetpack Compose UI out of the box:

```kotlin
import com.easyesp.shared.ui.EasyEspSetupUI
import com.easyesp.shared.mvi.EasyEspConfig

@Composable
fun MyScreen() {
    EasyEspSetupUI(
        config = EasyEspConfig(scanTimeoutMillis = 60_000L),
        onSuccess = { println("ESP Device Provisioned Successfully!") },
        onError = { error -> println("Setup Failed: $error") }
    )
}
```

Or, launch it safely from a standard Activity or Fragment:

```kotlin
import com.easyesp.shared.ui.EasyEspSetupContract
import com.easyesp.shared.mvi.EasyEspConfig

val launcher = registerForActivityResult(EasyEspSetupContract()) { result ->
    result.onSuccess { 
        // Success
    }.onFailure { 
        // Error
    }
}

// Start Setup
launcher.launch(EasyEspConfig())
```

### 3. Flash the Firmware
1. Open `firmware/esp32_wifi_setup.ino` in your Arduino IDE.
2. Select your ESP32 board and upload at `115200 baud`.

---

## 🛠 Advanced: Code-Only Implementation

If you want to build a completely custom UI, you can interact with the underlying MVI Session directly:

```kotlin
val session = EasyEsp.createSession(context, coroutineScope, EasyEspConfig())

// 1. Observe State
lifecycleScope.launch {
    session.state.collect { state -> 
        when(state) {
            is EasyEspState.Discovering -> showCustomLoader()
            is EasyEspState.EspDevicesFound -> displayCustomList(state.devices)
            // ...
        }
    }
}

// 2. Send Intents
session.sendIntent(EasyEspIntent.CheckPreconditionsAndStart)
```

**[Read more in the official docs](https://codingwitharul.github.io/esay-esp/releases/v1.0.0.html)**
