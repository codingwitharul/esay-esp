package com.easyesp.shared.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.easyesp.shared.mvi.EasyEspConfig

class EasyEspActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scanTimeoutMillis = intent.getLongExtra(EXTRA_SCAN_TIMEOUT, 60_000L)
        val retryDelayMillis = intent.getLongExtra(EXTRA_RETRY_DELAY, 2000L)
        
        val config = EasyEspConfig(
            scanTimeoutMillis = scanTimeoutMillis,
            retryDelayMillis = retryDelayMillis
        )

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EasyEspSetupUI(
                        config = config,
                        onSuccess = {
                            setResult(RESULT_OK)
                            finish()
                        },
                        onError = { errorMsg ->
                            val data = Intent().apply { putExtra(EXTRA_ERROR_MESSAGE, errorMsg) }
                            setResult(RESULT_CANCELED, data)
                            finish()
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_SCAN_TIMEOUT = "extra_scan_timeout"
        const val EXTRA_RETRY_DELAY = "extra_retry_delay"
        const val EXTRA_ERROR_MESSAGE = "extra_error_message"
    }
}

/**
 * An [ActivityResultContract] to easily launch [EasyEspActivity] and get the result via a callback.
 */
class EasyEspSetupContract : ActivityResultContract<EasyEspConfig, Result<Unit>>() {
    override fun createIntent(context: Context, input: EasyEspConfig): Intent {
        return Intent(context, EasyEspActivity::class.java).apply {
            putExtra(EasyEspActivity.EXTRA_SCAN_TIMEOUT, input.scanTimeoutMillis)
            putExtra(EasyEspActivity.EXTRA_RETRY_DELAY, input.retryDelayMillis)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result<Unit> {
        return if (resultCode == Activity.RESULT_OK) {
            Result.success(Unit)
        } else {
            val errorMsg = intent?.getStringExtra(EasyEspActivity.EXTRA_ERROR_MESSAGE) ?: "Setup cancelled or failed"
            Result.failure(Exception(errorMsg))
        }
    }
}
