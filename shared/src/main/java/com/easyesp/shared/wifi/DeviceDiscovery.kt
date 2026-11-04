package com.easyesp.shared.wifi

import kotlinx.coroutines.flow.Flow

interface DeviceDiscovery {
    fun startScanning(): Flow<List<DiscoveredDevice>>
    fun stopScanning()
}
