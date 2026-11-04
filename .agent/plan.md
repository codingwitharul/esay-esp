# Project Plan

Create a Kotlin Multiplatform Mobile (KMM) library for setting up WiFi connectivity for IoT devices like ESP32. Provide two modes: UI + Logic, or Logic-only. Features include device discovery, connecting to ESP32 hotspot, fetching available WiFi networks, sending credentials, and setup/resetup/forget functionality. Include a reference `.ino` firmware script for the ESP32. The default UI should use a stepper approach with Lottie animations for discovering and connecting, ending with a success screen.

## Project Brief

# Project Brief: EasyEsp WiFi Provisioning

## Features
1. **Device Discovery & Hotspot Connection**: Automatically scan for and connect to ESP32 device hotspots directly from the mobile application.
2. **Network Fetching & Provisioning**: Retrieve available WiFi networks from the connected ESP32 and securely transmit user-provided credentials (SSID/Password) to the device.
3. **Dual-Mode SDK (UI & Logic)**: Provides developers with a flexible implementation offering either a fully-built UI Stepper (with Lottie animations for discovering, connecting, and success states) or a headless Logic-only mode for custom UIs.
4. **Device State Management**: Core functionalities to perform initial setup, re-setup of existing configurations, and "forgetting" paired IoT devices.

## High-Level Tech Stack
* **Core Language & Concurrency**: Kotlin and Kotlin Coroutines for asynchronous operations (network calls, WiFi scanning, hardware communication).
* **Shared Business Logic**: Kotlin Multiplatform (KMM) structure to share the logic for ESP32 communication while abstracting platform-specific WiFi APIs (e.g., Android `WifiManager`/`ConnectivityManager`).
* **UI Framework**: Jetpack Compose for declarative implementation of the default stepper UI and success screens.
* **Navigation & Adaptive Strategy**: 
  * Strictly uses **Jetpack Navigation 3** (state-driven) for managing transitions between stepper stages (Discovery -> Select Network -> Input Credentials -> Success).
  * Uses the **Compose Material Adaptive** library to ensure all UI layouts flexibly scale across phones, foldables, and tablets.
* **Animations**: Lottie for Compose to handle the required visual feedback animations (discovering, connecting, success).

## Implementation Steps

### Task_1_Core_Logic: Set up KMM shared module and define WiFi/ESP32 communication interfaces.
- **Status:** COMPLETED
- **Updates:** Set up the KMP 'shared' module and configured gradle files. Defined interfaces for DeviceDiscovery, HotspotConnection, EspCommunication, and DeviceStateManager. Also created the reference firmware script 'firmware/esp32_wifi_setup.ino'. The shared module builds successfully.
- **Acceptance Criteria:**
  - Shared module builds
  - Interfaces for WiFi scanning and ESP32 communication are defined
- **Duration:** N/A

### Task_2_Platform_Wifi: Implement Android-specific WiFi connection logic and ESP32 communication protocols.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Android WifiManager/ConnectivityManager implemented
  - Network fetching from ESP32 implemented
  - ESP32 credential transmission logic complete
- **StartTime:** 2026-11-03 17:37:48 IST

### Task_3_Headless_SDK: Implement the headless Logic-only SDK mode and device state management.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Logic-only mode APIs available
  - Setup, re-setup, and forget device functions implemented

### Task_4_Compose_UI: Build the UI Stepper mode using Jetpack Compose, Navigation 3, Material Adaptive, and Lottie.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Stepper UI built
  - Lottie animations integrated for discovery/connecting/success
  - UI scales across devices using Material Adaptive
  - Navigation 3 implemented for state transitions

### Task_5_Run_Verify: Run and verify the EasyEsp WiFi Provisioning Android app. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** PENDING
- **Acceptance Criteria:**
  - project builds successfully
  - make sure all existing tests pass
  - build pass
  - app does not crash

