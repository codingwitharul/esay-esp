package com.easyesp.shared.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.easyesp.shared.EasyEsp
import com.easyesp.shared.communication.EspWifiNetwork
import com.easyesp.shared.wifi.DiscoveredDevice
import com.easyesp.shared.mvi.EasyEspConfig
import com.easyesp.shared.mvi.EasyEspIntent
import com.easyesp.shared.mvi.EasyEspState
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

private const val TAG = "EasyEspSetup"

class EspViewModel(
    application: Application,
    config: EasyEspConfig = EasyEspConfig()
) : AndroidViewModel(application) {
    
    private val session = EasyEsp.createSession(
        context = application,
        coroutineScope = viewModelScope,
        config = config
    )

    val uiState: StateFlow<EasyEspState> = session.state

    fun checkPreconditionsAndStart() {
        session.sendIntent(EasyEspIntent.CheckPreconditionsAndStart)
    }

    fun startDiscovery() {
        // Expose a way to restart discovery if needed, though checkPreconditionsAndStart handles it 
        session.sendIntent(EasyEspIntent.CheckPreconditionsAndStart)
    }

    fun stopDiscovery() {
        session.sendIntent(EasyEspIntent.StopDiscovery)
    }

    fun connectToEsp(device: DiscoveredDevice) {
        session.sendIntent(EasyEspIntent.ConnectToEsp(device))
    }

    fun proceedAfterManualConnection(device: DiscoveredDevice) {
        session.sendIntent(EasyEspIntent.ProceedAfterManualConnection(device))
    }

    fun selectNetwork(espDevice: DiscoveredDevice, network: EspWifiNetwork) {
        session.sendIntent(EasyEspIntent.SelectNetwork(espDevice, network))
    }

    fun provideCredentials(espDevice: DiscoveredDevice, network: EspWifiNetwork, password: String) {
        session.sendIntent(EasyEspIntent.ProvideCredentials(espDevice, network, password))
    }

    fun reset() {
        session.sendIntent(EasyEspIntent.Reset)
    }

    class Factory(
        private val application: Application,
        private val config: EasyEspConfig
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EspViewModel::class.java)) {
                return EspViewModel(application, config) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
