---
name: build_apk_au_playe_store_phone
description: Compile and deploy the Tahfid Quran application APK to the connected phone's Playe_Store/app_tahfid/ storage folder. Support building only Abdelwahab or only Amine by adding it to the trigger (e.g. "b_0 amine", "b_+1 abdelwahab").
---

# Skill - Build APK and Deploy to Playe Store Phone (Tahfid App)

This skill instructs the assistant on how to compile the application and deploy it to the connected phone's SD Card specifically under the `Playe_Store/app_tahfid/<VERSION>/` directory when working on the Tahfid Quran version of the application.

---

## Trigger Phrases
- "build_PS"
- "build_apk_au_playe_store_phone"
- "build_tahfid"
- "b_tahfid"
- "bt_"
- "b_v+1"
- "b_+1"
- "b_0"

*(Note: The user can append `abdelwahab` or `amine` to these triggers to restrict the build to a single app. For example: `b_0 amine`, `b_+1 abdelwahab`.)*

---

## Steps to Execute

### 1. Verify Active Branch / Application Type
Verify that you are on the `app_tahfid_quran` branch or that the project is configured for the Tahfid app:
```powershell
git branch --show-current
```

### 1.5 Parse the User Request
Determine two things from the user's prompt:
1. **Version Increment**: Does the trigger imply a version bump (`b_v+1`, `b_+1`) or building the current version (`b_0`, `build_PS`)?
2. **Target App**: Did the user specify a single target (`abdelwahab` or `amine`)? If `abdelwahab` is in the prompt, build ONLY Abdelwahab. If `amine` is in the prompt, build ONLY Amine. If neither is specified, build BOTH.

### 2. Read and Handle the Application Version Name
Extract the current `versionName` value from [app/build.gradle.kts](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/build.gradle.kts) (e.g. `1.13.6`).

- **If the trigger requests a version bump (e.g., `b_v+1`, `b_+1`)**:
  1. Parse the version to get the major, minor, and patch (e.g., `1`, `13`, `6` from `1.13.6`).
  2. Increment the patch version by 1 (e.g., `6` becomes `7`, resulting in `1.13.7`).
  3. Format the current month, day, hour, and minute as `MM_dd.HH_mm` (e.g., `06_17.15_44`).
  4. Form the new version name: `<Major>.<Minor>.<IncrementedPatch>.<Month>_<Day>.<Hour>_<Minute>` (e.g. `1.13.7.06_17.15_44`).
  5. Append `.app_tahfid` suffix to the end of the new version name (e.g. `1.13.8.06_29.00_44.app_tahfid`).
  6. Back up `app/build.gradle.kts` and temporarily replace the `versionName` line in `app/build.gradle.kts` with this new value.
- **If the trigger is standard (e.g., `b_0`, `build_PS`)**:
  1. Use the existing `versionName` (e.g. `1.13.6`) as `<VERSION>`. No modifications to `app/build.gradle.kts` are needed.

### 3. Compile and Export Abdelwahab APK (Skip if Target is ONLY Amine)
1. Back up `M00CentralParametresOfAllApps.kt` if not already done.
2. Ensure [M00CentralParametresOfAllApps.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/com/example/light_app_controles/Models/M00CentralParametresOfAllApps.kt) has:
   ```kotlin
   val au_Lence_Set_Compt_Ac_KeyId: String = Compts.AbdelwahabTravailleChezGros_KeyId.keyId,
   ```
3. Run the Gradle wrapper script to compile (consider running `.\gradlew.bat --stop` first if memory is constrained):
   ```powershell
   .\gradlew.bat assembleDebug --offline --parallel --build-cache --configuration-cache
   ```
4. Create the `abdelwahab` export directory on Desktop:
   ```powershell
   New-Item -ItemType Directory -Force -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\abdelwahab"
   ```
5. Copy the compiled APK to the `abdelwahab` directory:
   ```powershell
   Copy-Item -Path "app\build\outputs\apk\debug\app-debug.apk" -Destination "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\abdelwahab\تطبيق ابو الحازم بلتحفيظ القران الكريم الاستاذ عبدالوهاب.apk" -Force
   ```

### 4. Compile and Export Amine APK (Skip if Target is ONLY Abdelwahab)
1. Back up `M00CentralParametresOfAllApps.kt` if not already done.
2. Replace the active key in [M00CentralParametresOfAllApps.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/com/example/light_app_controles/Models/M00CentralParametresOfAllApps.kt) with `amine_madrasa_Compt_KeyId`:
   ```diff
   -    val au_Lence_Set_Compt_Ac_KeyId: String = Compts.AbdelwahabTravailleChezGros_KeyId.keyId,
   +    val au_Lence_Set_Compt_Ac_KeyId: String = amine_madrasa_Compt_KeyId,
   ```
3. Run the Gradle wrapper script to compile:
   ```powershell
   .\gradlew.bat assembleDebug --offline --parallel --build-cache --configuration-cache
   ```
4. Create the `amine` export directory on Desktop:
   ```powershell
   New-Item -ItemType Directory -Force -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\amine"
   ```
5. Copy the compiled APK to the `amine` directory:
   ```powershell
   Copy-Item -Path "app\build\outputs\apk\debug\app-debug.apk" -Destination "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\amine\تطبيق ابو الحازم بلتحفيظ القران الكريم الاستاذ امين.apk" -Force
   ```

### 5. Revert Files
Revert both `app/build.gradle.kts` (if modified) and `M00CentralParametresOfAllApps.kt` to their original states using your backups.

### 6. Compress the Export Folder
Zip the `<VERSION>` directory (which contains the compiled `abdelwahab` and/or `amine` folders) to `<VERSION>.zip` under the `app_tahfid` folder on Desktop:
```powershell
Compress-Archive -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>" -DestinationPath "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>.zip" -Force
```

### 7. Create Phone Storage Directory & Deploy via ADB
Create the destination folder structure on the phone and push both the ZIP archive and the raw compiled APK folder to the phone's SD Card storage using the ADB tool:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell mkdir -p /sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/<VERSION>/
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" push "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>.zip" "/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/"
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" push "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\" "/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/<VERSION>"
```

### 8. Report Success
Provide the user with a detailed summary showing:
- **Branche Active** : The active Git branch verified in step 1.
- **Version Détectée** : The versionName read from `build.gradle.kts`.
- **Cible(s) compilée(s)** : Abdelwahab, Amine, ou Les deux.
- **Chemin de l'export local** : Clickable link to the local folder on Desktop.
- **Fichier ZIP créé** : Clickable link to the generated zip file.
- **Chemin de déploiement SD Card (ZIP)** : The ZIP destination path on the Android device.
- **Chemin de déploiement SD Card (APK)** : The destination path of the raw folder on the Android device.
- **Confirmation de transfert** : Confirmation that compilation succeeded, the ZIP archive was created, and both were pushed to the phone.
