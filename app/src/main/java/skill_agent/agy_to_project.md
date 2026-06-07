# Skill - AGY to Project Synchronizer

This skill allows the user to manually trigger a full synchronization of the custom skills and configuration files from the global AGY skills folder to the project's local Java `skill_agent` folder, and update the root `h_.md` file.

---

## Trigger Phrases
- "agy_to_project"
- "agy_to_proj"
- "a_t_p"

---

## Steps to Execute

### 1. Synchronize Skill Files from Global AGY to Project Folder
- **Copy/Replace All Files**: Perform a recursive copy/overwrite of all files, folders, and subfolders from the global AGY skills directory `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\` into the project's local `skill_agent` directory `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\app\src\main\java\skill_agent\`.
- **Ensure Integrity**: All different or missing files in the destination should be copied, overwriting local copies to match the global AGY configurations.

### 2. Update and Overwrite Root h_.md File
- Copy/replace the file `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\app\src\main\java\skill_agent\h_.md` to `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\h_.md` to ensure the root documentation is fully up to date.

### 3. Display Sync Confirmation
- Print a clear list of files that were updated or copied, and verify success.
