# Skill - Fast Launch Preview (lp_)

This skill instructs the assistant on how to quickly compile, install, and launch the active preview screen (such as `CleanupScreen` or `Main_Preview_BonVentEtateScreen`) on the connected Android device or emulator, in the absolute fastest way possible.

---

## Trigger Phrases
- "lp_"
- "lance_preview"

---

## Steps to Execute

### 1. Configure the Preview Screen as Launcher
Ensure the app boots directly to the correct preview screen. Open `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens\MainScreen.kt` and set:
```kotlin
val its_dev_bigDatas = false
```
This bypasses other debug screens and loads the active preview screen (e.g. `CleanupScreen`) immediately on startup.

### 2. Compile and Install in Fast Mode
Run the highly optimized offline Gradle build to compile and install the application in parallel:
```powershell
.\gradlew.bat installDebug --offline --parallel --build-cache --configuration-cache
```

### 3. Launch via ADB
Launch the main activity on the connected device:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity
```

### 4. Capture and Verify the Interface
Wait 2 seconds, then capture a screen screenshot to visually verify that the preview is correctly rendered:
```powershell
android screen capture -a -o screen.png
```
Immediately read and display the screenshot to the user using the `view_file` tool on `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\screen.png`.
