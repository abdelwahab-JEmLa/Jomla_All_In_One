# Skill - Copy Coding Patterns to Light App (t_copiePattersApp)

This skill instructs the assistant on how to automatically copy, adapt, and port coding patterns, feature implementations, and fixes from the `ClientJetPack` codebase to the local `Light_App_Controles` (light app) project codebase. It guides the assistant on adjusting package imports, database calls, parameters, and layout structures to fit the light app's requirements.

---

## Trigger Phrases
- "t_copiePattersApp"
- "t>copiePattersApp"
- "copiePattersApp"
- "t_c_client"

---

## Steps to Execute

### 1. Recherche des TODOs (Comme `t_`)
- Utilisez `grep_search` pour chercher les commentaires `TODO` (ou `t_copiePattersApp`) **directement dans le code du projet source** `D:\AndroidStudioProjects\ClientJetPack`.
- Lisez ces commentaires pour comprendre ce qui doit être copié vers `Light_App_Controles`.

### 2. Génération du Contexte pour Nouvelle Session (Comme `t_contex`)
- Pour assurer une efficacité maximale et préserver les tokens d'une nouvelle session IA :
  - Extrayez le contexte (fichiers sources sur `D:\` et cibles locales).
  - Créez un fichier de résumé dans `app/src/main/java/skill_agent/t_/contexs/<conversation-id>_client_contex.md`.
  - Incluez obligatoirement la **Section 5. Règles d'Isolation (Ignore Rules)** en mentionnant `!*skill_agent/**` et les fichiers de destination locaux à ne pas ignorer, pour que la future IA puisse lancer `read_ingor_` et travailler efficacement sans s'aveugler.

### 3. Copy the Files/Folders in Bulk (Fast & Efficient)
- Si l'IA actuelle a suffisamment de contexte, copiez via PowerShell :
  ```powershell
  Copy-Item -Path "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\<source_path>" -Destination "app\src\main\java\<dest_path>" -Recurse -Force
  ```

### 4. Clean Up Unused Copied Files
- Review the copied folders and delete any files or directories containing scripts that are not used by the imported components.
- Specifically delete scripts that depend on Koin repositories, wifi transfers, or client-specific databases that are not part of the light app to prevent compiler errors:
  ```powershell
  Remove-Item -Path "app\src\main\java\<dest_path>\<unused_folder>" -Recurse -Force
  ```

### 5. Verify Library Dependencies
- Check if the imported code relies on libraries not yet configured in the light app's [build.gradle.kts](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/build.gradle.kts) (e.g. iText PDF libraries: `implementation("com.itextpdf:itext7-core:7.2.5")` and `implementation("com.itextpdf:html2pdf:4.0.5")`).
- Add the necessary dependencies to the `dependencies` block of the local [build.gradle.kts](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/build.gradle.kts) if missing.

### 6. Adapt Code to Local Architecture
- Open the ported files and adjust the code to match the local architecture:
  - **Koin Injections**: Replace Koin injections (e.g. `org.koin.compose.koinInject`) avec les Data-Bindings locaux de la Light App.
  - **WiFi Controls**: Remove references to WiFi data transfers.
  - **Imports**: Make sure import packages point to local namespaces (like `com.example.light_app_controles.R`).

### 7. Verify Local Compilation
- Run a compilation check in the local `Light_App_Controles` project to verify the changes:
  ```powershell
  .\gradlew.bat compileDebugKotlin --offline --parallel --build-cache --configuration-cache
  ```
- Parse any compilation errors and correct them immediately.

### 8. Clean up TODOs in BOTH Codebases
- Once the local build compiles successfully, locate the file containing the ported `TODO` in the `ClientJetPack` codebase (`D:\AndroidStudioProjects\ClientJetPack`) and delete the `TODO` comment (along with any helper pointer comments) using code edit tools.
- Delete the matching `TODO` in the local `Light_App_Controles` codebase as well.

### 9. Report Success and Display Diffs
- Provide the user avec le lien du contexte généré (si généré pour une nouvelle session) ou affichez les diffs si le travail a été fait immédiatement.
