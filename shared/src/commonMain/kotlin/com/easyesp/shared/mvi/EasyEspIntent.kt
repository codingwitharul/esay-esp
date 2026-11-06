package com.easyesp.shared.mvi

import com.easyesp.shared.communication.EspWifiNetwork
import com.easyesp.shared.wifi.DiscoveredDevice

sealed class EasyEspIntent {
    /** Check permissions and start discovery */
    object CheckPreconditionsAndStart : EasyEspIntent()
    
    /** Stop scanning for devices */
    object StopDiscovery : EasyEspIntent()
    
    /** Select a discovered ESP device and attempt to connect to its hotspot */
    data class ConnectToEsp(val device: DiscoveredDevice) : EasyEspIntent()
    
    /** Proceed fetching networks after a manual Wi-Fi connection was established */
    data class ProceedAfterManualConnection(val device: DiscoveredDevice) : EasyEspIntent()
    
    /** Select a target Wi-Fi network from the ESP's available networks */
    data class SelectNetwork(val espDevice: DiscoveredDevice, val network: EspWifiNetwork) : EasyEspIntent()
    
    /** Send Wi-Fi credentials to the ESP device */
    data class ProvideCredentials(val espDevice: DiscoveredDevice, val network: EspWifiNetwork, val password: String) : EasyEspIntent()
    
    /** Reset the state to Idle */
    object Reset : EasyEspIntent()
}
