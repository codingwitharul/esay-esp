#include <WiFi.h>
#include <WebServer.h>

const char* AP_SSID = "EasyEsp-Setup";
const char* AP_PASS = ""; // Removed password to make it an Open Network and bypass WPA2 connection bugs

WebServer server(80);

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("\n[EasyESP] ESP32 Wi-Fi Setup Firmware Starting...");

  // Force disconnect and clear old Wi-Fi settings saved in NVS memory
  WiFi.disconnect(true, true);
  delay(100);

  // Set ESP32 to Access Point + Station mode so it can scan Wi-Fi networks while hosting AP
  WiFi.mode(WIFI_AP_STA);

  // Start the Access Point
  if(strlen(AP_PASS) == 0) {
      WiFi.softAP(AP_SSID); // Open network
  } else {
      WiFi.softAP(AP_SSID, AP_PASS); // Secured network
  }

  Serial.print("[EasyESP] AP IP address: ");
  Serial.println(WiFi.softAPIP());
  Serial.println("[EasyESP] AP SSID: " + String(AP_SSID));
  Serial.println("[EasyESP] AP Password: NONE (Open Network)");

  // Define endpoints for communication
  server.on("/networks", HTTP_GET, handleGetNetworks);
  server.on("/credentials", HTTP_POST, handleSetCredentials);

  server.begin();
  Serial.println("[EasyESP] HTTP server started on port 80.");
}

void loop() {
  server.handleClient();

  // Monitor Wi-Fi connection status if connecting to home router
  static int lastStatus = -1;
  int status = WiFi.status();
  if (status != lastStatus) {
    lastStatus = status;
    if (status == WL_CONNECTED) {
      Serial.print("[EasyESP] Successfully connected to Home Wi-Fi! IP: ");
      Serial.println(WiFi.localIP());
    }
  }
}

void handleGetNetworks() {
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

void handleSetCredentials() {
  Serial.println("[EasyESP] HTTP POST /credentials received.");
  if (server.hasArg("ssid") && server.hasArg("password")) {
    String ssid = server.arg("ssid");
    String password = server.arg("password");

    Serial.println("[EasyESP] -> Received SSID: " + ssid);
    Serial.println("[EasyESP] -> Received Password length: " + String(password.length()));

    server.send(200, "application/json", "{\"status\":\"success\"}");

    Serial.println("[EasyESP] Connecting to Wi-Fi network: " + ssid + " ...");
    WiFi.begin(ssid.c_str(), password.c_str());
  } else {
    Serial.println("[EasyESP] Error: Missing ssid or password arguments in POST request.");
    server.send(400, "application/json", "{\"status\":\"error\", \"message\":\"Missing ssid or password\"}");
  }
}
