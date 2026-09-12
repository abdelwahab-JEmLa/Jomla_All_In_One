---
name: todo_to_extarnale_chat
description: Use this skill to automatically gather all related code files and context (including visual images/screenshots) to solve a given TODO, bundle them together, and copy them to the clipboard for pasting into an external Deep-Thinking Web AI (like Claude Sonnet/Thinking or Gemini Pro). Trigger this whenever the user requests "todo_to_extarnale_chat", "con_cop", "copy_context", "cont_copie", "con_copie", "cop_cont", "cc_", "cc_se", "cc_sans_explication", or mentions copying context/TODO to an external AI.
---

# Skill - Todo to External Chat Copy (todo_to_extarnale_chat)

This skill automates the process of using a fast AI assistant to quickly locate all relevant source files and visual/image assets related to a specific TODO, summarize the architectural context, bundle them into a single clipboard payload, and copy them. This allows the user to paste the complete problem context directly into a slower, Deep-Thinking external AI (like Claude Sonnet/Thinking) to generate the fix.

> 📖 **Exemples d'exécution** :
> - [E_1.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/a_parSections/To_Externale_Ai/todo_to_extarnale_chat/References/Examples/E_1.md) — Exemple général (tri/sort TODO)
> - [on_click_todos/exmp_1.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/a_parSections/To_Externale_Ai/todo_to_extarnale_chat/References/Examples/on_click_todos/exmp_1.md) — Exemple TODO `on_click` (câblage callback bouton → IA externe)
> - [relative_chained_todos/exmp_1.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/a_parSections/To_Externale_Ai/todo_to_extarnale_chat/References/Examples/relative_chained_todos/exmp_1.md) — Exemple réel : TODOs(1) + TODOs(2.C relatifs) chaîne impression Bluetooth *(projet ClientJetPack)*


## Trigger Phrases
- "todo_to_extarnale_chat"
- "con_cop"
- "copy_context"
- "cont_copie"
- "con_copie"
- "cop_cont"
- "cc_"
- "cc_se"
- "cc_sans_explication"

## Goal
To locate all files related to the target TODO (using fast search/grep tools), integrate image/screenshot context if available, generate an explanatory context summary, overwrite the clipboard backup file (`hist_copie.md`), and copy everything directly to the Windows Clipboard for the external Deep-Thinking AI.

---

## Steps to Execute

### 1. Identify Target TODO & Locate Relevant Flow Files
- Locate the target TODO in the codebase.
- **Quick Gathering (Standard TODOs)**: For most TODOs, do NOT spend time doing deep searches. Simply gather the active file and files in the immediate directory or directly referenced. Collect up to 10 files total.
- **Deep Trace for `on_click` TODOs**: If the TODO is of style `on_click` (or callback wiring):
  1. **Fast "Ctrl+Click" Search**: Rapidly trace the usage chain (who calls the composable, where the callback goes, models involved) using fast search tools (`grep_search` / `Select-String`).
  2. **Broad Collection**: Initially integrate ALL related files found in the trace, even if it exceeds 20 files. 
  3. **Filtering**: Quickly filter the list down to the most relevant files concerned with the callback path so the external Deep-Thinking AI has everything needed to solve it. Perform this search and filter phase as rapidly as possible to save time.
- **CRITICAL**: **NEVER** include internal agent skill instructions (like `t_.md`, `todo_to_extarnale_chat.md`, etc.) in the gathered files or clipboard copy payload.

### 2. Inspect and Include Visual/Image Context (if requested or triggered by TODO)
- **CRITICAL CONDITIONAL RULE**: Only check for and include image files if the user explicitly mentions keywords such as "image", "screenshot", "capture", "visuel", "screen", "photo", or "png"/"jpg" in their request, OR if the target TODO comment contains the keyword `img_`. If none of these conditions are met, skip this step entirely and do NOT add any image files to `hist_copie.md` or the clipboard.
- If requested or triggered by the TODO containing `img_`:
  - Check for image files starting with `img_`, named `img.jpg`/`img.png`, or files matching `Screenshot_*` on the Desktop (`C:\Users\Abou Mohamed\Desktop`) or in the workspace to capture visual bugs/output screens.
  - View and analyze the image, include a description of the visual layout/bug in the context file, and explicitly state in the context file that this screenshot is attached for the external AI to visually inspect and review the interface layout/bug.
  - Add the image link to `hist_copie.md` so the copying script places the actual image file in the clipboard alongside the code files.

### 3. Generate Context Summary (`ctsave_`) with Deep-Thinking Instructions
- Create a session folder inside `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/` named using the format:
  `<MM_dd HH_mm_ss> <Title>` (e.g. `06_17 17_28 Absences_PDF_Toggle`).
- Write the session context markdown file named `context_agy.md` inside this session folder.
- **CRITICAL SPEED OPTIMIZATION**: 
  - To make `cc_` run extremely fast (comparable to `t_`), the assistant must avoid generating duplicate files in a `files_edited/` folder, doing slow code truncations, or comment-wrapping (`/* ... */`), unless a file is huge (> 1000 lines) and optimization is explicitly requested by the user. Bypassing duplicate file writing saves substantial execution time and prevents compiler conflict errors.
  - In `context_agy.md`, include:
    1. The instruction: `"fix todo avec la facon la plus rapide (ne pas ecrire "todo resolved" ou "TODO resolved" a la fin)"`, followed by the exact code snippet showing where the TODO is located. **NEVER** write or include references to internal skill files (like `t_.md`) or any internal guidelines/instructions that belong to the local agentic IDE, as this confuses the external AI.
    2. A brief overview of the architectural context.
    3. **Do NOT embed full code of other files inside `context_agy.md`**: Since the related source files are copied directly via the clipboard and uploaded to the external AI, embedding their full code blocks inside the context file is redundant and wastes tokens.
  - Write the paths of the original project files directly in `hist_copie.md` so that the clipboard script copies them from their source locations, avoiding any temporary file duplication.

### 4. Overwrite `hist_copie.md` and Create `run_copy.bat`
- Write the Markdown links of the context file, all gathered source code files, and the visual assets to `app/src/main/java/skill_agent/copy_context/copy_skill/references/hist_copie.md`.
- **CRITICAL**: For `cc_se`, also write a copy of this `hist_copie.md` file inside the created session folder at `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/<MM_dd HH_mm_ss> <Title>/hist_copie.md` to maintain a permanent record of the file links copied in that session.
- Format for links:
  ```markdown
  ### 🔗 [Filename.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/...)
  ```
- **Create `run_copy.bat` inside the session folder**:
  Create the batch file `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/<MM_dd HH_mm_ss> <Title>/run_copy.bat` that allows the user to re-copy this specific session's files later by double-clicking it.
  Content of `run_copy.bat`:
  ```cmd
  @echo off
  copy /y "%~dp0hist_copie.md" "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\references\hist_copie.md"
  call "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
  ```

### 5. Execute Clipboard Copy (`cc_`)
- Run the batch file to copy the files directly to the Windows Clipboard:
  ```bash
  "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
  ```

### 5.5. Verify Clipboard Content
- Verify that the clipboard is correctly populated with the files. In the final response, check the execution logs or verify directly to ensure the files are successfully placed in the Windows Clipboard.

### 6. Display Summary to the User
- Present the user with a clean Markdown table listing:
  - **Context File**: Link to the generated context file.
  - **Copied Files**: A list of all code and image files bundled.
  - **Clipboard Verification**: A validation status (e.g. `✅ Réussi - Fichiers présents dans le presse-papiers` or `❌ Échec`).
  - **Execution Time**: The time taken to perform the search and bundle.
  - **Re-copy Link**: A direct link to re-copy this session's context: `[🚀 Re-copier cette session (run_copy.bat)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/<MM_dd%20HH_mm_ss>%20<Title>/run_copy.bat)`.
  - Direct executable link: `[🚀 Exécuter la Copie (run_cc_silent.vbs)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_skill/run_cc_silent.vbs)`.
- **Next Step Mention**: Always output a brief note reminding the user that they can write `ok_` once they've downloaded the AI's fix to automatically apply/overwrite the changes in the project.
