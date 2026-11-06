package com.easyesp.shared.device

import android.content.Context
import android.content.SharedPreferences

class AndroidDeviceStateManager(
    context: Context
) : DeviceStateManager {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("easyesp_device_states", Context.MODE_PRIVATE)

    override suspend fun getDeviceState(deviceId: String): DeviceState {
        val stateName = prefs.getString("state_$deviceId", null)
        return if (stateName != null) {
            try {
                DeviceState.valueOf(stateName)
            } catch (e: Exception) {
                DeviceState.UNINITIALIZED
            }
        } else {
            DeviceState.UNINITIALIZED
        }
    }

    override suspend fun markAsPaired(deviceId: String) {
        prefs.edit().putString("state_$deviceId", DeviceState.PAIRED.name).apply()
    }

    override suspend fun forgetDevice(deviceId: String) {
        prefs.edit().remove("state_$deviceId").apply()
    }

    override suspend fun resetSetup(deviceId: String) {
        prefs.edit().putString("state_$deviceId", DeviceState.SETUP_IN_PROGRESS.name).apply()
    }
}
