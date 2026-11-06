#include <EasyEspConnect.h>

// Create an instance of the library
EasyEspConnect easyEsp;

void setup() {
  Serial.begin(115200);

  // Set up what happens when the device successfully connects to Wi-Fi
  easyEsp.onConnected([](String ip) {
    Serial.println("=========================================");
    Serial.println("PROVISIONING COMPLETE!");
    Serial.println("My new IP address is: " + ip);
    Serial.println("=========================================");
  });

  // Start the setup hotspot.
  // It broadcasts an open network named "EasyEsp-Setup" by default.
  easyEsp.begin();
}

void loop() {
  // This must be called to process the incoming Wi-Fi setup requests from the Android App
  easyEsp.loop();

  // You can run your normal IoT code here once provisioned
  if (easyEsp.isConnected()) {
      // Do normal things (e.g. read sensors, send MQTT messages)
  }
}
