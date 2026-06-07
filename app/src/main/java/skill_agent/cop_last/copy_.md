# Skill - Copy Package / Sibling Files (cop_last / cl_)

This skill instructs the assistant on how to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package whenever the user requests "cl_", "cop_last", "cop_l", "cop_", or "copy_package", or when a dynamic `TODO: cop_` is identified next to a component.

---

## Trigger Phrases
- "cl_"
- "cop_l"
- "cop_last"
- "cop_"
- "copy_package"
- "TODO: cop_"

---

## Active Reference Package

The current default reference package is set below:
- **Reference Directory**: `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\app\src\main\java\com\example\light_app_controles\B\Screens\Z\Screens\Test\ID1\Client_Map\App\Bon_Vent_Etate\View\ID3\WhatsappSendFolder\Feature\b_FastAdd_FloatingSeparated_Button_1\Actions`

---

## Steps to Execute

### 1. Identify Target Directory & Files
- **Case A: cl_ / cop_l / cop_last (Default Trigger)**: If the user triggers `cl_`, `cop_l`, or `cop_last` without a path, the assistant must read the exact list of file paths defined in [list_copied_files.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/copy_/list_copied_files.md). Copy *exactly* those files to the Windows clipboard.
- **Case B: Dynamic Request**: If the user provides a path (e.g. `<directory_path> cop_`), or a dynamic comment `//TODO: cop_` is found, set the target directory to that path or the parent folder of the file containing the comment. Scan that directory recursively for `.kt` files.

### 2. Copy Files Directly to Windows Clipboard (Ctrl+C Simulation)
- Run the PowerShell `Set-Clipboard` command to populate the Windows system clipboard with the actual `.kt` file objects, allowing the user to paste them directly into Android Studio or Windows Explorer using `Ctrl+V`:
  ```powershell
  Set-Clipboard -Path "<file_path_1>", "<file_path_2>", ...
  ```

### 3. Save Copied Files to list_copied_files.md
- If files were scanned dynamically (Case B), overwrite the contents of [list_copied_files.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/copy_/list_copied_files.md) with the absolute paths of all the `.kt` files that were successfully copied, ensuring they are saved for subsequent fast triggers.

### 4. Report Copy Success (Ultra-concise)
Always output a highly concise response containing ONLY:
1. **Nom court du package** (the last directory segment, e.g., `Actions` or `Options`).
2. **Liste des fichiers copiés** (names of all `.kt` files copied to the clipboard).
3. **Important**: Never output a text block, full contents, or "block note" representing the code of the files. Keep the message extremely lightweight, confirming that the files are now ready to be pasted with `Ctrl+V`.

### 5. Update Reference Package & Help Catalog
- Automatically copy and synchronize this skill file to both the project's folder `app/src/main/java/skill_agent/cop_last.md`, `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\copy_\copie_.md`, and the CLI's directory `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\cop_last.md`.
