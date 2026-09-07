# Skill - AS Click Run (as_click_run)

This skill instructs the assistant on how to compile, install, and launch the active Android application on the connected device or emulator using Gradle and ADB. This serves as a highly reliable, background-compatible execution sequence.

---

## Trigger Phrases
- "as_click_run"
- "as_run"
- "r_"
- "click_run"

---

## Steps to Execute

### 1. Compile and Install in Fast Mode
Run the highly optimized offline Gradle build to compile and install the application in parallel:
```powershell
.\gradlew.bat installDebug --offline --parallel --build-cache --configuration-cache
```

### 2. Launch Application via ADB
Launch the main activity on the connected device:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity
```

### 3. Verify Output
- If the build succeeds and shows `BUILD SUCCESSFUL` followed by the ADB starting intent output, report success to the user.
- If the build fails, parse the compilation errors and present them to the user.
