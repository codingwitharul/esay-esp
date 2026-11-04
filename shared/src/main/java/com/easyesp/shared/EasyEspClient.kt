package com.easyesp.shared

import com.easyesp.shared.communication.EspCommunication
import com.easyesp.shared.communication.EspWifiNetwork
import com.easyesp.shared.device.DeviceStateManager
import com.easyesp.shared.wifi.DeviceDiscovery
import com.easyesp.shared.wifi.DiscoveredDevice
import com.easyesp.shared.wifi.HotspotConnection
import kotlinx.coroutines.flow.Flow

class EasyEspClient(
    private val deviceDiscovery: DeviceDiscovery,
    private val hotspotConnection: HotspotConnection,
    private val espCommunication: EspCommunication,
    private val deviceStateManager: DeviceStateManager
) {
    fun startScanning(): Flow<List<DiscoveredDevice>> {
        return deviceDiscovery.startScanning()
    }

    fun stopScanning() {
        deviceDiscovery.stopScanning()
    }

    suspend fun connectToHotspot(ssid: String, password: String?): Boolean {
        return hotspotConnection.connectToHotspot(ssid, password)
    }

    fun bindToCurrentWifi() {
        hotspotConnection.bindToCurrentWifi()
    }

    suspend fun getAvailableNetworks(): List<EspWifiNetwork> {
        return espCommunication.getAvailableNetworks()
    }

    suspend fun setupDevice(deviceId: String, wifiSsid: String, wifiPassword: String): Boolean {
        val success = espCommunication.sendCredentials(wifiSsid, wifiPassword)
        if (success) {
            deviceStateManager.markAsPaired(deviceId)
        }
        return success
    }
}
