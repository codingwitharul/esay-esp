package com.easyesp.shared.device

import kotlinx.coroutines.flow.StateFlow

interface DeviceStateManager {
    suspend fun getDeviceState(deviceId: String): DeviceState
    suspend fun markAsPaired(deviceId: String)
    suspend fun forgetDevice(deviceId: String)
    suspend fun resetSetup(deviceId: String)
}
