# Skill - Annotated Screen Capture

This skill instructs the assistant on how to automatically capture the connected Android device or emulator's screen with annotations (labeled bounding boxes) and display it to the user whenever the user writes "scr_s" or requests a screenshot with annotations.

---

## Trigger Phrases
- "scr_s"

---

## Steps to Execute

### 1. Capture the Screen
Run the `android screen capture` command to capture the device screen. Always save it to a standard file name like `screen.png` in the workspace directory:
```powershell
android screen capture -a -o screen.png
```

### 2. View/Display the Screenshot
Immediately read and display the screenshot to the user using the `view_file` tool:
- Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\ClientJetPack\screen.png`

### 3. Report Success
Provide the user with a direct markdown link to the screenshot file and describe the screen content if needed.
