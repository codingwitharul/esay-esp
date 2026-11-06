package com.easyesp.shared.mvi

/**
 * Configuration options for the EasyEsp setup process.
 * 
 * @property scanTimeoutMillis Maximum duration to scan for ESP devices before timing out.
 * @property retryDelayMillis Delay before retrying network fetches from the ESP device.
 */
data class EasyEspConfig(
    val scanTimeoutMillis: Long = 60_000L,
    val retryDelayMillis: Long = 2000L
)
