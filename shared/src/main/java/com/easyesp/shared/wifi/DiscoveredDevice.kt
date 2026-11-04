package com.easyesp.shared.wifi

data class DiscoveredDevice(
    val ssid: String,
    val macAddress: String,
    val signalStrength: Int
)
