package com.easyesp.shared.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidDeviceDiscovery(
    private val context: Context
) : DeviceDiscovery {

    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @SuppressLint("MissingPermission")
    override fun startScanning(): Flow<List<DiscoveredDevice>> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    if (success) {
                        trySend(getDiscoveredDevices())
                    } else {
                        // Scan failed, maybe send old results anyway
                        trySend(getDiscoveredDevices())
                    }
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

        // Try to start a scan, and send initial results
        trySend(getDiscoveredDevices())
        @Suppress("DEPRECATION")
        wifiManager.startScan()

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    override fun stopScanning() {
        // No explicit stop needed, the Flow handles unregistering via awaitClose
    }

    @SuppressLint("MissingPermission")
    private fun getDiscoveredDevices(): List<DiscoveredDevice> {
        return wifiManager.scanResults
            // Filtering for generic ESP patterns or leaving it to UI
            // Assuming ESP32 networks contain "ESP" for this template, 
            // but we'll return all and let caller filter if necessary, 
            // or just ESP networks. Let's return all.
            .map { result ->
                DiscoveredDevice(
                    ssid = result.SSID,
                    macAddress = result.BSSID,
                    signalStrength = result.level
                )
            }
    }
}
