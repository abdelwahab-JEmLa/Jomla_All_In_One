# Skill - Fast Build, Install and Launch

This skill instructs the assistant on how to automatically compile the Android application, install it on the connected device or emulator, launch the main activity immediately, capture and display the screen with annotations to verify the UI changes, and provide the user with a direct markdown link in the final success report, whenever the user requests "lance_r".

---

## Trigger Phrases
- "lance_r"
- "l_"
- "l_r"

---

## Steps to Execute

### 1. Compile and Install the App
Run the Gradle wrapper script `gradlew.bat` with the `installDebug` task in the project root directory (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles`):
```powershell
.\gradlew.bat installDebug --offline --parallel --build-cache --configuration-cache
```
*Note: Wait for this build to finish successfully.*

### 2. Launch the Application Activity
Once the build succeeds, launch the application's launcher activity using ADB (Android Debug Bridge):
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity
```
*Note: Wait a brief moment (e.g. 2-3 seconds) for the app interface to load before capturing the screen.*

### 3. Capture the Screen with Annotations
Take a screenshot of the launched app with accessibility annotations to verify the UI:
```powershell
android screen capture -a -o screen.png
```

### 4. View and Display the Screenshot
Immediately read and display the screenshot to the user using the `view_file` tool:
- Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\screen.png`

### 5. Report Success with Verification Link
Provide the user with a detailed success report showing:
- A confirmation that the build, installation, and launch were successful.
- A direct clickable markdown link to [screen.png](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/screen.png) so they can quickly inspect the visual results.
