# EasyESP Connect

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#)
[![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)](#)
[![ESP32](https://img.shields.io/badge/ESP32-E7352C?style=for-the-badge&logo=espressif&logoColor=white)](#)

EasyESP Connect is an end-to-end solution for provisioning ESP32 devices to a local Wi-Fi network using a modern Android application. Built with **Jetpack Compose**, **Kotlin Coroutines**, and **Ktor**, it provides a seamless, animated, and highly resilient setup experience.

## ✨ Features

*   **Automated Device Discovery**: Uses Android's `WifiManager` to scan and detect nearby ESP32 devices broadcasting their setup SSID.
*   **Programmatic Hotspot Binding**: Leverages `WifiNetworkSpecifier` (Android 10+) to securely and automatically connect the user's phone to the ESP32 without leaving the app.
*   **Smart Fallbacks**: If the OS blocks the automatic connection or it times out, the app gracefully degrades to a guided manual connection screen.
*   **Modern Material 3 UI**: Beautifully designed screens using Jetpack Compose, featuring custom Lottie animations (radar scanning, success marks, timeout alerts).
*   **ESP32 Dual Mode (AP + STA)**: The firmware utilizes `WIFI_AP_STA` mode, allowing the ESP32 to host a configuration portal while simultaneously scanning the surrounding environment for home routers.

---

## 📱 Screenshots

*Coming Soon...*

---

## 🛠️ System Architecture

### 1. Android App
*   **UI**: Built entirely in Jetpack Compose utilizing `NavDisplay` and Material 3 Adaptive layouts for a smooth multi-step flow.
*   **State Management**: `EspViewModel` orchestrates an 11-step state machine (`EspUiState`), managing everything from permission requests to provisioning payloads.
*   **Networking**: Uses `Ktor` to communicate with the ESP32's local web server (`http://192.168.4.1`).

### 2. ESP32 Firmware
*   Written in Arduino C++.
*   Hosts a synchronous `WebServer` on port 80.
*   **Endpoints**:
    *   `GET /networks`: Returns a JSON array of surrounding Wi-Fi networks and their signal strengths (RSSI).
    *   `POST /credentials`: Receives the target `ssid` and `password` and initiates the connection to the home router.

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer).
*   An ESP32 Development Board.
*   Arduino IDE (with ESP32 board manager installed).

### Flashing the ESP32
1.  Open `firmware/esp32_wifi_setup.ino` in the Arduino IDE.
2.  Select your ESP32 board and COM port.
3.  Upload the sketch. (Ensure the Serial Monitor is set to **115200 baud** to view debug logs).

### Running the App
1.  Open the project in Android Studio.
2.  Sync Gradle files.
3.  Run the `:app` module on a physical Android device (Wi-Fi scanning and hotspot binding do not work reliably on emulators).

---

## 🛡️ Permissions

The app strictly requests only the necessary permissions required by Android for Wi-Fi provisioning:
*   `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: Mandated by Android to perform Wi-Fi network scans.
*   `ACCESS_WIFI_STATE` & `CHANGE_WIFI_STATE`: To check and toggle Wi-Fi status.
*   `ACCESS_NETWORK_STATE` & `CHANGE_NETWORK_STATE`: Required to bind the application's process to the temporary ESP32 hotspot network.
*   `INTERNET`: To communicate with the ESP32's local HTTP server.

---

## 📄 License

This project is licensed under the MIT License.
