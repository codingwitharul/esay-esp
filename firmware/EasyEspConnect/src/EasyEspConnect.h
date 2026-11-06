#ifndef EASY_ESP_CONNECT_H
#define EASY_ESP_CONNECT_H

#include <WiFi.h>
#include <WebServer.h>
#include <functional>

/**
 * EasyEspConnect Library
 * Handles dual-mode Wi-Fi AP provisioning and Network Scanning.
 */
class EasyEspConnect {
public:
    EasyEspConnect();

    /**
     * Initializes the Wi-Fi AP for provisioning.
     * @param apSsid The name of the hotspot the ESP32 will broadcast.
     * @param apPassword Optional password. Leave blank for an Open network.
     */
    void begin(const char* apSsid = "EasyEsp-Setup", const char* apPassword = "");

    /**
     * Must be called inside the main Arduino loop() to process incoming HTTP requests.
     */
    void loop();

    /**
     * @return true if the ESP32 is successfully connected to the user's home Wi-Fi.
     */
    bool isConnected();

    /**
     * Registers a callback that is fired immediately after the ESP32 connects to Wi-Fi.
     */
    void onConnected(std::function<void(String ip)> callback);

    // Internal HTTP Handlers
    void handleGetNetworks();
    void handleSetCredentials();

private:
    WebServer server;
    std::function<void(String)> connectedCallback;
    int lastWifiStatus;
};

#endif
