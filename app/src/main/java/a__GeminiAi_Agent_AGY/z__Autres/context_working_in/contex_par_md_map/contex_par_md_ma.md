# Skill - Context Map Manager (cree_map, cta_map, cw_map_d, map_colore, ctm_ref)

This skill instructs the assistant on how to generate a visual map of the workspace files and directories, parse user annotations (`++` to allow/focus, `--` to ignore) in that map, apply or deactivate context restrictions based on it, and colorize/comment active sections.

---

## Trigger Phrases
- "cree_map" (Generates or refreshes the file map, preserving existing annotations)
- "cree_map_ecrase_keep" (Generates a fresh file map, erasing all existing annotations)
- "cta_map" (Applies the annotated map context restrictions)
- "cw_map_d" (Deactivates map context restrictions, restoring full context access)
- "map_colore" (Colorizes map markers and comments out fully active directories)
- "ctm_ref" (Refreshes the map preserving existing annotations, applies restrictions, and colorizes)

---

## Steps to Execute

### When "cree_map" is triggered:

#### 1. Scan the Workspace Folders and Files
Scan the workspace directories (under `app/src/main/java/`) as well as the root `build.gradle.kts` and `app/build.gradle.kts` files. If context restriction is active (i.e., `.antigravityignore` exists and restricts folders), only include the files and folders that are visible/allowed in the active context.

#### 2. Run the Python Script to Generate the Map
Run the python script `app/src/main/java/skill_agent/context_working_in/contex_par_md_map/generate_map.py` (which preserves annotations by default) using a terminal command:
```powershell
python app/src/main/java/skill_agent/context_working_in/contex_par_md_map/generate_map.py
```
This script will scan the codebase and write the visual tree directly to `files_affiched.md` inside `app/src/main/java/skill_agent/context_working_in/contex_par_md_map/`.

#### 3. Report Success
Provide a success confirmation message along with a direct clickable link to the created file:
[files_affiched.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/context_working_in/contex_par_md_map/files_affiched.md)

---

### When "cree_map_ecrase_keep" is triggered:

#### 1. Scan the Workspace Folders and Files
Scan the workspace directories.

#### 2. Run the Python Script to Overwrite the Map
Run the python script `app/src/main/java/skill_agent/context_working_in/contex_par_md_map/generate_map.py` using a terminal command:
```powershell
python app/src/main/java/skill_agent/context_working_in/contex_par_md_map/generate_map.py
```
This script will generate a refreshed map from scratch, while keeping/preserving all existing `++` or `--` annotations.

#### 3. Report Success
Provide a success confirmation message along with a direct clickable link to [files_affiched.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/context_working_in/contex_par_md_map/files_affiched.md).

---

### When "cta_map" is triggered:

#### 1. Parse Annotated Markers in files_affiched.md
Scan [files_affiched.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/context_working_in/contex_par_md_map/files_affiched.md) and look for:
- `++` next to a file or folder name (e.g. `├── Base/     ++`), indicating it and its children should be whitelisted.
- `--` next to a file or folder name (e.g. `├── Dao14VentPeriode.kt     --`), indicating it and its children should be ignored.

#### 2. Run the Python Script to Apply Map Restrictions
Run the python script `app/src/main/java/skill_agent/context_working_in/contex_par_md_map/apply_map.py` using a terminal command:
```powershell
python app/src/main/java/skill_agent/context_working_in/contex_par_md_map/apply_map.py
```
This script will parse the annotations, construct whitelists (`!`) and blacklists in `.antigravityignore` and `.geminiignore`, and save them.

#### 3. Report Success
Confirm to the user that the map restrictions have been applied and show direct links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore).

---

### When "cw_map_d" is triggered:

#### 1. Restore Full Context Access
Clear/overwrite `.antigravityignore` and `.geminiignore` with the deactivation header to restore full project visibility:
```text
# Context restrictions deactivated
```
Do **not** modify or delete the annotations inside `files_affiched.md` itself.

#### 2. Report Success
Provide a confirmation message stating that the full workspace context is restored and all files/directories are visible.

---

### When "map_colore" is triggered:

#### 1. Run the Python Script to Colorize the Map
Run the python script `app/src/main/java/skill_agent/context_working_in/contex_par_md_map/color_map.py` using a terminal command:
```powershell
python app/src/main/java/skill_agent/context_working_in/contex_par_md_map/color_map.py
```
This script cleans any existing HTML coloring, parses the `++` and `--` rules, colors active directories green, ignores red, and automatically comments out (`<!-- ... -->`) child directories where all descendants are fully active.

#### 2. Report Success
Confirm to the user that the colorization and collapsing have been applied to [files_affiched.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/context_working_in/contex_par_md_map/files_affiched.md).

---

### When "ctm_ref" is triggered:

#### 1. Run the Python Script to Refresh and Apply Map Restrictions
Run the python script `app/src/main/java/skill_agent/context_working_in/contex_par_md_map/refresh_map.py` using a terminal command:
```powershell
python app/src/main/java/skill_agent/context_working_in/contex_par_md_map/refresh_map.py
```
This script will parse existing annotations from `files_affiched.md`, scan the project files, re-generate the tree list preserving all annotations, write the result back, apply the ignore rules to `.antigravityignore` and `.geminiignore`, and finally execute the colorization.

#### 2. Report Success
Confirm to the user that the map has been refreshed and annotations/ignores have been synchronized, showing direct links to [files_affiched.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/context_working_in/contex_par_md_map/files_affiched.md), [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore), and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore).
