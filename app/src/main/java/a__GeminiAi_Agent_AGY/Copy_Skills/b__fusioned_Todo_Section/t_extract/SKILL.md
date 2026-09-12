---
name: t_extract
description: Extracts a code block, utility, or dependency marked with a TODO: extract comment into a dedicated file under extarnaleDepandencie.
---

# Skill - Extract Dependency (t_extract)

This skill instructs the assistant on how to automatically extract a code block, utility, or dependency marked with a `TODO: extract` (or `TODO: <extract ...>`) comment into a dedicated file under the `extarnaleDepandencie` directory, and clean up the original TODO comment.

---

## Trigger Phrases
- `t_extract`
- `extract_todo`
- When a `TODO` found during `t_` contains the word `extract` (e.g., `TODO: extract`, `TODO: <extract ...>`)

---

## Steps to Execute

### 1. Parse the TODO Comment and Context
- Identify the code block or dependency associated with the `TODO: extract` comment.
- Extract the proposed filename/title (`<titre presentatife>`). If the comment specifies a name (e.g., `TODO: extract NetworkUtils`), use that as `<titre_presentatif>.kt`. If it is not explicitly named, generate a representative name based on the code's function.
- Identify the code/dependency structure to be moved.

### 2. Create the Extraction File
- Ensure the destination directory exists: `app/src/main/java/extarnaleDepandencie/Dependencie_Id/`.
- Create the file: `app/src/main/java/extarnaleDepandencie/Dependencie_Id/<titre_presentatif>.kt` (or appropriate extension based on content).
- Write the extracted code block, adding the package declaration:
  ```kotlin
  package extarnaleDepandencie.Dependencie_Id
  ```
  along with any required imports.

### 3. Clean up the Source File
- Remove the extracted code block from the original source file.
- Remove the original `TODO: extract` comment and any associated formatting markers.
- Import the newly created class/function in the original source file to ensure it compiles.

### 4. Update Context Restrictions (if active)
- If context restriction is active, automatically allow the newly created directory by appending the following rules to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore):
  ```text
  # Added via t_extract
  !app/src/main/java/extarnaleDepandencie/
  !app/src/main/java/extarnaleDepandencie/Dependencie_Id/
  !app/src/main/java/extarnaleDepandencie/Dependencie_Id/**
  ```

### 5. Report Success
- Provide the user with a detailed report of the extraction.
- Include clickable links to:
  - The modified source file.
  - The newly created dependency file at [app/src/main/java/extarnaleDepandencie/Dependencie_Id/<titre_presentatif>.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/extarnaleDepandencie/Dependencie_Id/).
  - The [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore) files.
