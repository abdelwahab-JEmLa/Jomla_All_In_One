# Skill - View Desktop Image (scr_)

This skill enables the assistant to dynamically locate, open, and analyze any specified image on the host PC Desktop based on the trigger phrase parameter.

---

## Trigger Phrases
- `scr_<image_name>` (e.g., `scr_2`, `scr_3.jpg`, `scr_capture_error`, `scr_image1`)
- `image_<image_name>` (e.g., `image_2`, `image_3.jpg`)

---

## Steps to Execute

### 1. Parse the Target Image Name
Extract the `<image_name>` from the trigger phrase. For example:
- If trigger is `scr_2`, the target name is `2`.
- If trigger is `scr_image3`, the target name is `image3`.
- If trigger is `scr_3.jpg`, the target name is `3.jpg`.

### 2. Search Variations on the Desktop
Search the PC Desktop (checking `C:\Users\Abou Mohamed\Desktop` and the OneDrive fallback `C:\Users\Abou Mohamed\OneDrive\Desktop`) for matching files.

If the target name **contains an extension** (e.g., `.jpg`, `.png`, `.jpeg`):
- Search directly for that filename.

If the target name **does not contain an extension**:
- Try appending common extensions: `.jpg`, `.png`, `.jpeg` (e.g., for `2` search `2.jpg`, `2.png`, `2.jpeg`).
- If the name is a number (e.g., `3`), also try searching for variations with prefix:
  - `image<number>` (e.g., `image3.png`, `image3.jpg`)
  - `image_<number>` (e.g., `image_3.png`, `image_3.jpg`)

### 3. Open and View the Image
- Use the `view_file` tool with the absolute path of the first matching image found.
- If no matching file is found, list the paths and filenames searched, and request clarification from the user.

### 4. Analyze Content
- Describe the visual layout of the loaded image.
- Focus on extracting error messages, UI issues, or code warnings.
- Suggest appropriate code modifications or troubleshooting steps.

---

## 🔗 Direct Links
* 🖼️ [scr_ Skill Config](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/scr_.md)
* ⚙️ [PC Help Page (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md)
