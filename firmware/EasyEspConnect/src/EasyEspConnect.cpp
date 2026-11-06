#include "EasyEspConnect.h"

// Global pointer required to map WebServer callbacks to our class instance
EasyEspConnect* easyEspInstance = nullptr;

EasyEspConnect::EasyEspConnect() : server(80), lastWifiStatus(-1), connectedCallback(nullptr) {
    easyEspInstance = this;
}

void EasyEspConnect::begin(const char* apSsid, const char* apPassword) {
    Serial.println("\n[EasyESP] ESP32 Wi-Fi Setup Firmware Starting...");

    // Force disconnect and clear old Wi-Fi settings saved in NVS memory
    WiFi.disconnect(true, true);
    delay(100);

    // Set ESP32 to Access Point + Station mode so it can scan Wi-Fi networks while hosting AP
    WiFi.mode(WIFI_AP_STA);

    if(strlen(apPassword) == 0) {
        WiFi.softAP(apSsid); // Open network
    } else {
        WiFi.softAP(apSsid, apPassword); // Secured network
    }

    Serial.print("[EasyESP] AP IP address: ");
    Serial.println(WiFi.softAPIP());
    Serial.println("[EasyESP] AP SSID: " + String(apSsid));

    // Define endpoints for communication
    server.on("/networks", HTTP_GET, []() {
        if(easyEspInstance) easyEspInstance->handleGetNetworks();
    });
    server.on("/credentials", HTTP_POST, []() {
        if(easyEspInstance) easyEspInstance->handleSetCredentials();
    });

    server.begin();
    Serial.println("[EasyESP] HTTP server started on port 80.");
}

void EasyEspConnect::loop() {
    server.handleClient();

    // Monitor Wi-Fi connection status
    int status = WiFi.status();
    if (status != lastWifiStatus) {
        lastWifiStatus = status;
        if (status == WL_CONNECTED) {
            Serial.print("[EasyESP] Successfully connected to Home Wi-Fi! IP: ");
            Serial.println(WiFi.localIP());
            if (connectedCallback) {
                connectedCallback(WiFi.localIP().toString());
            }
        }
    }
}

bool EasyEspConnect::isConnected() {
    return WiFi.status() == WL_CONNECTED;
}

void EasyEspConnect::onConnected(std::function<void(String ip)> callback) {
    connectedCallback = callback;
}

void EasyEspConnect::handleGetNetworks() {
    Serial.println("[EasyESP] HTTP GET /networks received. Scanning surrounding Wi-Fi networks...");
    int n = WiFi.scanNetworks();
    Serial.print("[EasyESP] Scan complete. Found ");
    Serial.print(n);
    Serial.println(" networks.");

    String json = "[";
    for (int i = 0; i < n; ++i) {
        if (i > 0) json += ",";
        json += "{\"ssid\":\"" + WiFi.SSID(i) + "\",\"rssi\":" + String(WiFi.RSSI(i)) + ",\"isSecure\":" + (WiFi.encryptionType(i) == WIFI_AUTH_OPEN ? "false" : "true") + "}";
        Serial.println(" - " + WiFi.SSID(i) + " (RSSI: " + String(WiFi.RSSI(i)) + ")");
    }
    json += "]";
    server.send(200, "application/json", json);
}

void EasyEspConnect::handleSetCredentials() {
    Serial.println("[EasyESP] HTTP POST /credentials received.");
    if (server.hasArg("ssid") && server.hasArg("password")) {
        String ssid = server.arg("ssid");
        String password = server.arg("password");

        Serial.println("[EasyESP] -> Received SSID: " + ssid);

        server.send(200, "application/json", "{\"status\":\"success\"}");

        Serial.println("[EasyESP] Connecting to Wi-Fi network: " + ssid + " ...");
        WiFi.begin(ssid.c_str(), password.c_str());
    } else {
        Serial.println("[EasyESP] Error: Missing ssid or password arguments in POST request.");
        server.send(400, "application/json", "{\"status\":\"error\", \"message\":\"Missing ssid or password\"}");
    }
}
