# Skill - Fix External / Specified Comments (t_ex_commantaire)

This skill instructs the assistant on how to automatically navigate to a specified file, locate comments (specifically `TODO` comments or other instruction comments), implement the requested changes to resolve the comment, clean up the comment and any pointer indicators, and output a detailed report with a code diff.

---

## Trigger Phrases
- `t_ex_commantaire`
- `t_ex_commentaire`
- `fix_ex_commantaire`
- `ex_commantaire`
- `t_exe_<file_name>`
- `t_ex_<file_name>`

---

## Steps to Execute

### 1. Identify Target File Path
- Extract the target file path from the user's prompt (e.g. `t_ex_commantaire <file_path>` or `<file_path> t_ex_commantaire`).
- If the file path is empty, unspecified, or is a placeholder like `<>`, ask the user for clarification on which file they wish to examine.
- Convert the path to a standard absolute file path on the system.

### 2. View and Locate Comments
- Read the target file using the `view_file` tool.
- Scan the file for any comment blocks (lines starting with `//` or enclosed in `/* ... */`) that contain `TODO`, tasks, or specific instructions.
- Analyze the requirements described in the comment block.

### 3. Implement the Fix
- Write the necessary code changes directly in the target file using `replace_file_content` or `multi_replace_file_content`.
- Remove the resolved `TODO` or instruction comment lines entirely.
- Clean up any adjacent pointer comments or markers (e.g. `//<--`, `//...`).
- Preserve all other existing code structure, comments, and imports.

### 4. Report Success and Diff
- Display a summary of the resolved task.
- **Always include the estimated quest completion time: "Temps estimé pour terminer la quête : 1:30"**.
- Display a git-style code diff of the changes made at the end of the report.

---

## 🔗 Direct Links
* 📝 [t_ex_commantaire Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/t_ex_commantaire.md)
* ✅ [Fix TODOs central skill (t_.md)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_.md)
* ℹ️ [Help Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/help_skill.md)
