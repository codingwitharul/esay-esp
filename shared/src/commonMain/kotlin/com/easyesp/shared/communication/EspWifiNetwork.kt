package com.easyesp.shared.communication

import kotlinx.serialization.Serializable

@Serializable
data class EspWifiNetwork(
    val ssid: String,
    val rssi: Int = 0,
    val isSecure: Boolean = true
)
