🚀 **Stop frustrating your IoT users with terrible Wi-Fi onboarding!**

If you've ever built a hardware product with an ESP32, you know the hardest part isn't writing the firmware—it's getting the user to successfully connect the device to their home Wi-Fi network. 

Users have to leave your app, go to their phone settings, connect to a temporary hotspot, ignore the "No Internet" warnings, return to the app, and pray it doesn't time out. On modern Android versions, the OS will often silently route setup requests to Cellular Data, causing the setup to fail completely. 

As developers, we can do better. 

Today, I’m incredibly excited to open-source **EasyESP Android** — a battle-tested library that makes ESP32 Wi-Fi provisioning completely seamless and fully automated! ✨

**What makes it different?**
📡 **Automated Discovery:** Passively scans for local ESP32 devices without any user input.
🔗 **Programmatic Hotspot Binding:** Connects the phone to the ESP32 and forces all HTTP setup traffic over Wi-Fi, bypassing Android's strict cellular data rules.
🎨 **Drop-In Jetpack Compose UI:** A beautiful, animated, and state-driven provisioning flow you can add to your app in 3 lines of code.
🧠 **MVI Architecture:** Want to build your own UI? Hook directly into the headless MVI session.

And it doesn't stop at Android...

**For Hardware Developers:**
I've also published the companion C++ firmware to the **Official Arduino Library Manager**! Just search for `EasyEspAndroid` in your Arduino IDE to instantly add an asynchronous, dual-mode (AP + STA) configuration portal to your ESP32.

Check out the full GitHub repository, the Medium deep-dive, and the documentation below! If this saves you a few days of networking headaches, I'd love a ⭐ on GitHub!

🔗 **GitHub Repo:** https://github.com/codingwitharul/esay-esp
📚 **Documentation:** https://codingwitharul.github.io/esay-esp/
📝 **Medium Article:** [Link to your Medium Article here]

#AndroidDev #Kotlin #JetpackCompose #IoT #ESP32 #Arduino #OpenSource #Hardware #AndroidDevelopment