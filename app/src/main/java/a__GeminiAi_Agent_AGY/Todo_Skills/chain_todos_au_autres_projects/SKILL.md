---
name: chain_todos_au_autres_projects
description: Searches for, identifies, and resolves TODO comments in external projects (like ClientJetPack or Light App).
---

# Skill - Chained TODOs in Other Projects (chain_todos_au_autres_projects)

This skill instructs the assistant on how to automatically search for, identify, and resolve `TODO` comments in external projects (such as `ClientJetPack` or `Light_App_Controles`). It functions exactly like the standard `t_` skill, but targets external project directories, searching for relative/chained TODOs to understand the expected coding patterns before applying fixes.

---

## Trigger Phrases
- "chain_todos_au_autres_projects"
- "t_appClient_chain_todo"
- "t>cli"
- ">clientApp"
- ">ca"
- "clientApp"
- ">light app"

---

## Steps to Execute

### 1. Locate outstanding TODOs in the target codebase
Determine the target path to search for relative/chained TODOs based on the trigger or instruction:
- **Case 1: `>light app` is specified**: Search in the **`Light_App_Controles`** codebase (resolved via `ref_light app` in references.json to `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles`).
  - Target Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src`
- **Case 2: Standard execution**: Search in the client codebase:
  - Target Path: `D:\AndroidStudioProjects\ClientJetPack\app\src`

The absolute fastest way to locate all TODO comments is using the `grep_search` tool with the query `TODO` on the target paths (e.g., `app\src\main\java` and `app\src\androidTest\java`).

*Note for Windows environments*: If `grep_search` fails or is not available, immediately use the `run_command` tool to run the following fast PowerShell search:
```powershell
Get-ChildItem -Path "<Target_Path>" -Recurse -Filter "*.kt" | Select-String -Pattern "TODO"
```

Analyze comments such as:
- `//<--` or `//<-` pointers.
- Chained relative comments (e.g. `//TODO(2.C Relative Au Todo(1):` or `//TODO(1):`).
- Comments indicating expected structure or template code (e.g., `//...`).
Use these indicators to understand the exact coding patterns required for the implementation.

### 2. Implement the fixes in Client Code
- Analyze the `TODO` requirements and their relationships/dependencies (e.g. implementing dependent fixes in the correct order).
- Search for surrounding code patterns in the codebase to match the coding style (e.g. variables, database calls, ViewModels, or test assertions).
- Apply the appropriate manual code fixes using file editing tools (such as `replace_file_content` or `multi_replace_file_content`).

### 3. Clean up the Code
- Remove the resolved `TODO` comments from the file.
- Thoroughly clean up any associated pointer/indicator comments (such as `//<--`, `//<-`, `//...`) from adjacent lines to keep the code perfectly clean.

### 4. Report Success and Display Code Diffs
Provide the user with a detailed report including:
- Clickable links to the modified files in the `ClientJetPack` project.
- **Always display the time 1:30 to complete the quest (e.g., "Temps estimé pour terminer la quête : 1:30").**
- A detailed git-style code diff showing all modified files at the very end of your explanations.
