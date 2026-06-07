# Skill - Fast Build and Install

This skill instructs the assistant on how to automatically compile the Android application, generate the APK, and install it on the connected device or emulator whenever the user requests a build.

---

## Trigger Phrases
- "build"

---

## Steps to Execute

### 1. Execute the Gradle Install Task
Run the Gradle wrapper script `gradlew.bat` with the `installDebug` task in the project root directory (`C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack`):
```powershell
.\gradlew.bat installDebug --offline --parallel --build-cache --configuration-cache
```

### 2. Verify Output
- Monitor the build progress.
- If the build succeeds and shows `BUILD SUCCESSFUL`, inform the user.
- If the build fails, parse the compilation errors and present them to the user with references to the files.
