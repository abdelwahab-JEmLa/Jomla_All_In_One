# Skill - Fast Build, Export and Deploy to SD Card

This skill instructs the assistant on how to build the application, automatically update the version name in `app/build.gradle.kts` to use the current date/time and append `.A_AllInOne`, compile the app, and immediately restore the Gradle file to its original state so that no git changes are committed (ignoring changes), before packaging and deploying the ZIP to the connected phone's SD Card.

---

## Trigger Phrases
- "build_"

---

## Steps to Execute

### 1. Update Version Name in `build.gradle.kts`
Before building, read `app/build.gradle.kts` and dynamically update the `versionName` to include the current day, hour, and minute, and append the `.A_AllInOne` suffix.
- Format: `versionName = "<Major>.<Minor>.<Patch>.<Build>_<Day>.<Hour>:<Minute>.<ProjectSuffix>.A_AllInOne"`
  *Example target value*: `versionName = "1.14.0.05_29.23:43.Boutique_JeMla_All_In_On_Project.A_AllInOne"` (assuming current date is 29th, 23:43).
- **Save a backup** of the original `app/build.gradle.kts` content to revert it later.
- Parse the major/minor prefix (e.g. `1.14`) to use for folder names. Default to `1.14` if parsing fails.

### 2. Compile and Assemble the App
Run the Gradle wrapper script `gradlew.bat` in the project root directory to compile and build the debug APK:
```powershell
.\gradlew.bat assembleDebug --offline --parallel --build-cache --configuration-cache
```

### 3. Revert `build.gradle.kts` (Ignore Sync Changes)
Immediately restore the original content of `app/build.gradle.kts` so that Git remains clean and the temporary version change is ignored/reverted in your working directory.

### 4. Create Export Directory on Desktop
Create the specified directory hierarchy on the Desktop:
```powershell
New-Item -ItemType Directory -Force -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0.\A_AllInOne"
```

### 5. Copy APK to Destination
Copy the built APK `app-debug.apk` to the newly created folder:
```powershell
Copy-Item -Path "app\build\outputs\apk\debug\app-debug.apk" -Destination "C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0.\A_AllInOne\app-debug.apk" -Force
```

### 6. Compress the Export Folder
Zip the `0.` directory to `0.zip` under the version folder:
```powershell
Compress-Archive -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0." -DestinationPath "C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0.zip" -Force
```

### 7. Create Phone Directory and Push ZIP package via ADB
Create the destination folder structure on the phone and push the ZIP package to the SD card:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell mkdir -p /sdcard/Abdelwahab_jeMla.com/Playe_Store/<VERSION>/
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" push "C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0.zip" "/sdcard/Abdelwahab_jeMla.com/Playe_Store/<VERSION>/"
```

### 8. Report Success
Provide the user with a detailed summary showing:
- **Version Initiale** : The original versionName.
- **Version Générée (Temporaire)** : The dynamically generated versionName with the timestamp and suffix.
- **Chemin de l'export local** : Clickable link to the local folder on Desktop.
- **Fichier ZIP créé** : Clickable link to the generated zip file.
- **Chemin de déploiement SD Card** : The destination path on the Android device.
- **Confirmation de transfert & Nettoyage** : Confirmation that compilation succeeded, file was pushed, and `build.gradle.kts` was successfully reverted to keep Git status clean.
