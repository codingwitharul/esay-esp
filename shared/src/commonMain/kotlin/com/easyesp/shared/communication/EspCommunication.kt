package com.easyesp.shared.communication

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

interface EspCommunication {
    suspend fun getAvailableNetworks(): List<EspWifiNetwork>
    suspend fun sendCredentials(ssid: String, password: String): Boolean
}

class KtorEspCommunication : EspCommunication {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val baseUrl = "http://192.168.4.1"

    override suspend fun getAvailableNetworks(): List<EspWifiNetwork> {
        return try {
            client.get("$baseUrl/networks").body()
        } catch (e: Exception) {
            Log.e("EspCommunication", "Failed to GET networks from ESP32", e)
            emptyList()
        }
    }

    override suspend fun sendCredentials(ssid: String, password: String): Boolean {
        return try {
            val response: HttpResponse = client.post("$baseUrl/credentials") {
                parameter("ssid", ssid)
                parameter("password", password)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            Log.e("EspCommunication", "Failed to POST credentials to ESP32", e)
            false
        }
    }
}
