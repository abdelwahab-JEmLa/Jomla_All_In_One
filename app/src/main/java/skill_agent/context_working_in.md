# Skill - Context Working_IN.Feature (c_w_a, c_w_d, c_w_e, cwa_, cwd_, cwe_, cwa_add_)

This skill instructs the assistant on how to isolate the AI's working context solely to specific packages or files, restore full workspace context, check the current status of the context restriction, or dynamically add new packages or files to the active context using full names, stored short-name aliases, or file searches.

---

## Trigger Phrases
- "agy_context_unique_workingIn_active"
- "agy_context_unique_workingIn_desactive"
- "c_w_a"
- "c_w_d"
- "c_w_e"
- "cwa_"
- "cwd_"
- "cwe_"
- "cwa_add_package <package>"
- "cwa_add_<short_name>"
- "cwa_add_<filename.kt>"
- "cwa_add_<filename>"
- "sw_enleve_<short_name>"
- "sw_enleve_<package>"

---

## Steps to Execute

### When "c_w_a", "cwa_" or "agy_context_unique_workingIn_active" is triggered:

#### 1. Create .antigravityignore in Project Root
Create or overwrite the `.antigravityignore` file at the root of the project (`C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\.antigravityignore`) with the following content:
```text
# Ignore everything by default
*

# Allow the path to Working_IN/Feature
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/Working_IN/
!app/src/main/java/Working_IN/Feature/
!app/src/main/java/Working_IN/Feature/**

# Allow skill_agent configuration so skills still work
!app/src/main/java/skill_agent/
!app/src/main/java/skill_agent/**
```

#### 2. Create .geminiignore in Project Root
Create or overwrite the `.geminiignore` file at the root of the project with identical content.

#### 3. Report Activation Success
Provide the user with a detailed confirmation showing:
- A clear message stating that the workspace context is now focused exclusively on `Working_IN.Feature`.
- Direct clickable markdown links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.geminiignore).

---

### When "c_w_d", "cwd_" or "agy_context_unique_workingIn_desactive" is triggered:

#### 1. Remove the Context Restrictions
Delete or clear the contents of `.antigravityignore` and `.geminiignore` files at the root of the project to allow the agent to see all files in the project.

#### 2. Report Deactivation Success
Provide the user with a clear message stating that the full workspace context is now restored and Antigravity can work on all packages of the project.

---

### When "c_w_e" or "cwe_" is triggered:

#### 1. Check File Status
Check if the `.antigravityignore` and/or `.geminiignore` files exist in the project root and read their contents to verify if the restrict rule `*` is active.

#### 2. Report Current Status
- **If active**: Confirm that context restriction is **ACTIF** (only `Working_IN.Feature` and other explicitly allowed packages/files are visible). List any individually allowed files along with their full package names or directory paths for clarity.
- **If inactive**: Confirm that context restriction is **INACTIF / DÉSACTIVÉ** (the full workspace context is visible).
- Show clickable links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.geminiignore).

---

### When "cwa_add_package <package>" is triggered:

#### 1. Parse and Translate the Package Name
Convert the dot-separated package name (e.g. `com.example.light_app_controles.Modules.Base.SQL.Daos`) into matching folder directory rules under `app/src/main/java/`.
For `com.example.light_app_controles.Modules.Base.SQL.Daos`, generate:
```text
# Package: com.example.light_app_controles.Modules.Base.SQL.Daos
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
```

#### 2. Append to Ignore Files
Open `.antigravityignore` and `.geminiignore` (create them with the base template if they do not exist). Check if these package rules already exist; if not, append them to the bottom of both files.

#### 3. Update Skill Configuration's Mapped Packages List
Extract the last segment of the package name (e.g. `Daos`). Save the mapping `<short_name> = <package>` under the **Mapped Packages** section at the bottom of the skill files `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\context_working_in.md` and `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\app\src\main\java\skill_agent\context_working_in.md`.

#### 4. Report Success
Confirm to the user that the package has been added and mapped, and show direct links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.geminiignore).

---

### When "cwa_add_<filename.kt>" or "cwa_add_<filename>" (with file extension) is triggered:

#### 1. Locate the File in the Project
Search the codebase to find the absolute or relative path of the file matching `<filename.kt>` or `<filename>`.

#### 2. Generate Path Allowance Rules
Generate the ignore allowance rules for every parent folder leading to that file, plus the file itself.
For example, for `app/src/main/java/.../M8BonVent.kt`:
```text
# File: M8BonVent.kt
!app/src/main/java/com/
!app/src/main/java/com/example/
...
!app/src/main/java/.../M8BonVent.kt
```

#### 3. Append to Ignore Files
Open `.antigravityignore` and `.geminiignore` and append the generated rules to the bottom of both files.

#### 4. Report Success
Provide the user with a confirmation of addition along with direct links to the file and ignore files.

---

### When "cwa_add_<short_name>" is triggered:

#### 1. Search in Mapped Packages
Read the **Mapped Packages** list at the bottom of this file. Look for an entry matching `<short_name>` (e.g. `Daos`).

#### 2. Perform Addition
- **If found**: Retrieve the full package name and run the `cwa_add_package <package>` steps.
- **If not found**: Report that the short name is unrecognized and prompt the user to register it first via `cwa_add_package <package>`.

---

### When "sw_enleve_<short_name>" or "sw_enleve_<package>" is triggered:

#### 1. Retrieve the Package Name
Search in the **Mapped Packages** list at the bottom of this file. If `<short_name>` matches a key, retrieve its associated full package name. If not, treat the argument directly as the full package name.

#### 2. Remove Package Rules from Ignore Files
Scan `.antigravityignore` and `.geminiignore`. Locate the block of comments and folder rules associated with the package (e.g. lines starting with `# Added via cwa_add_package` or individual rules matching the package directory pattern). Delete these rules and save both files.

#### 3. Report Success
Confirm to the user that the package has been successfully removed from the active context, and provide direct clickable links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/.geminiignore).

---

## Mapped Packages
- Daos = com.example.light_app_controles.Modules.Base.SQL.Daos
- FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button = com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
