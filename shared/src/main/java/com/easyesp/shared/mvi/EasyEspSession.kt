package com.easyesp.shared.mvi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.easyesp.shared.EasyEspClient
import com.easyesp.shared.communication.EspWifiNetwork
import com.easyesp.shared.wifi.DiscoveredDevice
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "EasyEspSession"

class EasyEspSession(
    private val context: Context,
    private val client: EasyEspClient,
    private val config: EasyEspConfig = EasyEspConfig(),
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow<EasyEspState>(EasyEspState.Idle)
    val state: StateFlow<EasyEspState> = _state.asStateFlow()

    private var discoveryJob: Job? = null
    private var actionJob: Job? = null

    fun sendIntent(intent: EasyEspIntent) {
        when (intent) {
            is EasyEspIntent.CheckPreconditionsAndStart -> checkPreconditionsAndStart()
            is EasyEspIntent.StopDiscovery -> stopDiscovery()
            is EasyEspIntent.ConnectToEsp -> connectToEsp(intent.device)
            is EasyEspIntent.ProceedAfterManualConnection -> proceedAfterManualConnection(intent.device)
            is EasyEspIntent.SelectNetwork -> selectNetwork(intent.espDevice, intent.network)
            is EasyEspIntent.ProvideCredentials -> provideCredentials(intent.espDevice, intent.network, intent.password)
            is EasyEspIntent.Reset -> reset()
        }
    }

    private fun checkPreconditionsAndStart() {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val isWifiEnabled = wifiManager.isWifiEnabled

        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        Log.i(TAG, "Preconditions check -> isWifiEnabled: $isWifiEnabled, hasLocationPermission: $hasLocationPermission")

        if (!isWifiEnabled || !hasLocationPermission) {
            _state.value = EasyEspState.PermissionOrWifiRequired(isWifiEnabled, hasLocationPermission)
        } else {
            startDiscovery()
        }
    }

    private fun startDiscovery() {
        Log.i(TAG, "Starting device discovery / scanning...")
        stopDiscovery()
        _state.value = EasyEspState.Discovering
        
        discoveryJob = coroutineScope.launch {
            val timeoutJob = launch {
                delay(config.scanTimeoutMillis)
                if (_state.value is EasyEspState.Discovering) {
                    Log.w(TAG, "Device discovery timed out after ${config.scanTimeoutMillis}ms.")
                    _state.value = EasyEspState.ScanTimeout
                    client.stopScanning()
                }
            }

            try {
                client.startScanning().collect { devices ->
                    Log.i(TAG, "Discovered ${devices.size} devices: ${devices.map { it.ssid }}")
                    if (devices.isNotEmpty()) {
                        timeoutJob.cancel()
                        _state.value = EasyEspState.EspDevicesFound(devices)
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Discovery failed with error: ${e.message}", e)
                _state.value = EasyEspState.Error("Discovery failed: ${e.message}")
            } finally {
                timeoutJob.cancel()
            }
        }
    }

    private fun stopDiscovery() {
        Log.d(TAG, "Stopping device discovery.")
        discoveryJob?.cancel()
        client.stopScanning()
    }

    private fun connectToEsp(device: DiscoveredDevice) {
        Log.i(TAG, "Connecting to ESP device hotspot: SSID = ${device.ssid}, MAC = ${device.macAddress}")
        _state.value = EasyEspState.ConnectingToEsp(device)
        stopDiscovery()
        
        actionJob?.cancel()
        actionJob = coroutineScope.launch {
            try {
                val success = client.connectToHotspot(device.ssid, null)
                Log.i(TAG, "Hotspot connection result for ${device.ssid}: success = $success")
                if (success) {
                    fetchNetworksOrFallback(device)
                } else {
                    Log.w(TAG, "Automated hotspot connection timed out or failed. Falling back to Manual Connection guide.")
                    _state.value = EasyEspState.ManualConnectionRequired(device)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.w(TAG, "Exception during hotspot connection, falling back to manual: ${e.message}")
                _state.value = EasyEspState.ManualConnectionRequired(device)
            }
        }
    }

    private fun proceedAfterManualConnection(device: DiscoveredDevice) {
        Log.i(TAG, "User confirmed manual connection to ESP hotspot. Forcing process Wi-Fi binding and fetching networks...")
        _state.value = EasyEspState.ConnectingToEsp(device)
        
        client.bindToCurrentWifi()

        actionJob?.cancel()
        actionJob = coroutineScope.launch {
            fetchNetworksOrFallback(device)
        }
    }

    private suspend fun fetchNetworksOrFallback(device: DiscoveredDevice) {
        var networks = client.getAvailableNetworks()
        if (networks.isEmpty()) {
            Log.d(TAG, "Initial network list empty. Retrying after ${config.retryDelayMillis}ms...")
            delay(config.retryDelayMillis)
            networks = client.getAvailableNetworks()
        }
        Log.i(TAG, "Fetched ${networks.size} available Wi-Fi networks from ESP device.")
        if (networks.isNotEmpty()) {
            _state.value = EasyEspState.NetworkSelection(device, networks)
        } else {
            Log.w(TAG, "No Wi-Fi networks returned by ESP device.")
            _state.value = EasyEspState.ManualConnectionRequired(device)
        }
    }

    private fun selectNetwork(espDevice: DiscoveredDevice, network: EspWifiNetwork) {
        Log.i(TAG, "Selected network for provisioning: ${network.ssid} (Secure: ${network.isSecure})")
        _state.value = EasyEspState.CredentialInput(espDevice, network)
    }

    private fun provideCredentials(espDevice: DiscoveredDevice, network: EspWifiNetwork, password: String) {
        Log.i(TAG, "Providing credentials to ESP device for network: ${network.ssid}")
        _state.value = EasyEspState.Provisioning(espDevice, network)
        
        actionJob?.cancel()
        actionJob = coroutineScope.launch {
            try {
                val success = client.setupDevice(deviceId = espDevice.macAddress, wifiSsid = network.ssid, wifiPassword = password)
                Log.i(TAG, "Provisioning result for ${network.ssid}: success = $success")
                if (success) {
                    Log.i(TAG, "ESP device setup and provisioning completed successfully!")
                    _state.value = EasyEspState.Success
                } else {
                    Log.e(TAG, "Failed to send credentials to ESP device.")
                    _state.value = EasyEspState.Error("Failed to send credentials to ESP device.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Exception during provisioning: ${e.message}", e)
                _state.value = EasyEspState.Error("Error provisioning: ${e.message}")
            }
        }
    }

    private fun reset() {
        Log.i(TAG, "Resetting setup state to Idle.")
        stopDiscovery()
        actionJob?.cancel()
        _state.value = EasyEspState.Idle
    }
}
