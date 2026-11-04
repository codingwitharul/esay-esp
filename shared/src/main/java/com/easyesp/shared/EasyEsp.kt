package com.easyesp.shared

import android.content.Context
import com.easyesp.shared.communication.KtorEspCommunication
import com.easyesp.shared.device.AndroidDeviceStateManager
import com.easyesp.shared.wifi.AndroidDeviceDiscovery
import com.easyesp.shared.wifi.AndroidHotspotConnection
import com.easyesp.shared.mvi.EasyEspConfig
import com.easyesp.shared.mvi.EasyEspSession
import kotlinx.coroutines.CoroutineScope

object EasyEsp {
    /**
     * Creates a fully configured instance of [EasyEspClient] ready to be used.
     * 
     * @param context Application context used for initializing platform specific components.
     * @return A configured [EasyEspClient] instance.
     */
    fun createClient(context: Context): EasyEspClient {
        return EasyEspClient(
            deviceDiscovery = AndroidDeviceDiscovery(context.applicationContext),
            hotspotConnection = AndroidHotspotConnection(context.applicationContext),
            espCommunication = KtorEspCommunication(),
            deviceStateManager = AndroidDeviceStateManager(context.applicationContext)
        )
    }

    /**
     * Creates an MVI session for controlling the EasyESP setup flow entirely via code.
     * Use this if you want to implement your own UI and just send intents to the library.
     *
     * @param context Application context.
     * @param coroutineScope Scope where the session will launch internal coroutines.
     * @param config Configuration options.
     * @return A configured [EasyEspSession] instance.
     */
    fun createSession(
        context: Context,
        coroutineScope: CoroutineScope,
        config: EasyEspConfig = EasyEspConfig()
    ): EasyEspSession {
        return EasyEspSession(
            context = context.applicationContext,
            client = createClient(context.applicationContext),
            config = config,
            coroutineScope = coroutineScope
        )
    }
}
