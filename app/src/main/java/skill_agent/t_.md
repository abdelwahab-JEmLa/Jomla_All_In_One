# Skill - Fix TODOs and Launch (t_)

This skill instructs the assistant on how to automatically search for, identify, and fix `TODO` comments in the active codebase, compile/install the application on the connected device or emulator, launch the main activity, capture/verify the screen, and display a detailed code diff at the end of the explanations.

Additionally, this skill supports the **`t_models`** sub-trigger, which automatically adds `appDatabase.kt` and the `Models` package to the active restricted context before proceeding with the standard steps.

---

## Trigger Phrases
- "t_"
- "t_models"
- "fix_todo"
- "fix_todos"

---

## Steps to Execute

### When "t_models" is triggered:

#### 1. Add appDatabase.kt and Models packages to Context (cwa_)
Automatically invoke the context addition (`cwa_`) rules for the following packages:
- **`AppDatabase`** (Package: `com.example.light_app_controles.Modules.Base.SQL.Daos`)
- **`Models`** (Package: `EntreApps.Shared.Models`)

Generate and append the folder rules to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore):

```text
# Added via t_models (com.example.light_app_controles.Modules.Base.SQL.Daos)
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/com/
!app/src/main/java/com/example/
!app/src/main/java/com/example/light_app_controles/
!app/src/main/java/com/example/light_app_controles/Modules/
!app/src/main/java/com/example/light_app_controles/Modules/Base/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/Daos/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/Daos/**

# Added via t_models (EntreApps.Shared.Models)
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/EntreApps/
!app/src/main/java/EntreApps/Shared/
!app/src/main/java/EntreApps/Shared/Models/
!app/src/main/java/EntreApps/Shared/Models/**
```

#### 2. Execute standard TODO fixing, building, and launching
Proceed directly to the standard steps below to locate/fix TODOs, rebuild, launch, and verify.

---

### Standard Steps to Execute:

### 1. Locate outstanding TODOs in the codebase
Search the codebase to find any outstanding `TODO` comments using the `grep_search` tool:
- Query: `TODO`
- SearchPath: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java`

### 2. Implement the fixes in Code
- Select the relevant `TODO` comments, analyze their requirements, and apply the appropriate code fixes using `replace_file_content` or `multi_replace_file_content`.
- Remove the `TODO` comments after addressing them.

### 3. Compile, Install and Launch the Application
To verify that everything works correctly, run the build and launch tasks:
- Compile and install:
  ```powershell
  .\gradlew.bat installDebug --offline --parallel --build-cache --configuration-cache
  ```
- Launch the main activity:
  ```powershell
  & "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity
  ```

### 4. Capture and View the Screen
Capture the screen to verify visual correctness:
- Take screenshot:
  ```powershell
  android screen capture -a -o screen.png
  ```
- Display the screenshot using the `view_file` tool:
  * Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\screen.png`

### 5. Report Success and Display Code Diffs
Provide the user with a detailed report including:
- Clickable links to the modified files.
- A direct clickable markdown link to [screen.png](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/screen.png).
- **A detailed git-style code diff showing all modified files at the very end of your explanations.**
