package com.easyesp.shared.mvi

import com.easyesp.shared.communication.EspWifiNetwork
import com.easyesp.shared.wifi.DiscoveredDevice

sealed class EasyEspState {
    object Idle : EasyEspState()
    object Discovering : EasyEspState()
    object ScanTimeout : EasyEspState()
    data class PermissionOrWifiRequired(val isWifiEnabled: Boolean, val hasPermissions: Boolean) : EasyEspState()
    data class EspDevicesFound(val devices: List<DiscoveredDevice>) : EasyEspState()
    data class ConnectingToEsp(val espDevice: DiscoveredDevice) : EasyEspState()
    data class ManualConnectionRequired(val espDevice: DiscoveredDevice) : EasyEspState()
    data class NetworkSelection(val espDevice: DiscoveredDevice, val networks: List<EspWifiNetwork>) : EasyEspState()
    data class CredentialInput(val espDevice: DiscoveredDevice, val network: EspWifiNetwork) : EasyEspState()
    data class Provisioning(val espDevice: DiscoveredDevice, val network: EspWifiNetwork) : EasyEspState()
    object Success : EasyEspState()
    data class Error(val message: String) : EasyEspState()
}
