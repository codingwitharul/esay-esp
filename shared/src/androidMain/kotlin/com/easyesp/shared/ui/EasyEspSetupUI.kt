package com.easyesp.shared.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.easyesp.shared.mvi.EasyEspConfig
import com.easyesp.shared.mvi.EasyEspState

/**
 * A wrapper around [EspSetupScreen] that makes it easy to launch the UI
 * and receive callbacks upon success or failure, without needing to 
 * manage the ViewModel manually.
 *
 * @param config Configuration for the setup process (e.g. timeouts).
 * @param onSuccess Callback triggered when the ESP is provisioned successfully.
 * @param onError Callback triggered when the process fails entirely.
 */
@Composable
fun EasyEspSetupUI(
    config: EasyEspConfig = EasyEspConfig(),
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as Application
    
    val viewModel: EspViewModel = viewModel(
        factory = EspViewModel.Factory(application, config)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            is EasyEspState.Success -> onSuccess()
            is EasyEspState.Error -> onError((uiState as EasyEspState.Error).message)
            else -> {} // no-op
        }
    }

    EspSetupScreen(viewModel = viewModel)
}
