package com.easyesp.shared.wifi

interface HotspotConnection {
    suspend fun connectToHotspot(ssid: String, password: String?): Boolean
    suspend fun disconnect()
    fun bindToCurrentWifi()
}
