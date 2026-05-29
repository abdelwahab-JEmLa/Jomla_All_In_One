# Skill - Todo Bubble (todo_bubelle)

This skill instructs the assistant on how to automatically capture the screen, identify visual bubbles or annotations on the screen, resolve the layout/UI problems they highlight, and then build, launch, and verify the app.

---

## Trigger Phrases
- "todo_bubelle"
- "todo_b"
- "todo_bubble"

---

## Steps to Execute

### 1. Capture and View the Screen
First, take a screenshot of the connected Android device or emulator with accessibility annotations to find any visual feedback, bubble, or tooltip pointing to issues:
```powershell
android screen capture -a -o screen.png
```
Immediately read and display the screenshot to analyze the user interface using the `view_file` tool:
- Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\screen.png`

### 2. Read and Analyze the Bubble / Tooltip
- Scan the annotated screenshot for any tooltips or bubbles (especially red or highlighted ones).
- Focus on the element pointed to by the pointer/cursor/arrow in the UI.
- Read the content of the bubble/tooltip or its accessibility labels (e.g., `contentDescription` or layout properties).

### 3. Fix the Problem in Code
- Search the codebase to find where the problematic component is located.
- Implement the fix in the source code (e.g., correct translation, colors, layout alignment, state updates, or database entries).
- Ensure the code compiles properly and preserves existing documentation.

### 4. Build, Install, and Launch
Run the build and launch process to verify the changes:
```powershell
.\gradlew.bat installDebug --offline --parallel --build-cache --configuration-cache
```
Once it builds successfully, launch the application:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity
```

### 5. Verify the Fix
Verify the result by capturing a new screen with annotations to confirm the bubble/issue is resolved or the UI is updated:
```powershell
android screen capture -a -o screen_after.png
```
Read the new screenshot using `view_file`:
- Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\screen_after.png`
