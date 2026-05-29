# Skill - Tap Android FAB

This skill instructs the assistant on how to automatically capture the screen, identify the Floating Action Button (FAB), resolve its coordinates, and tap it whenever the user requests a "tap".

---

## Trigger Phrases
- "tap"

---

## Steps to Execute

### 1. Capture the Screen with Annotations
Take a screenshot of the connected Android device or emulator and overlay bounding boxes:
```powershell
android screen capture -a -o new_ui.png
```

### 2. Locate the FAB Bounding Box
Analyze `new_ui.png` to identify the bounding box label corresponding to the main FAB:
- When closed, it is the circular button displaying the app logo (e.g., labeled `#16`).
- When open, it is the circular button displaying the Close/Plus icon (e.g., labeled `#20`).

### 3. Resolve Coordinates
Run `android screen resolve` with the correct bounding box label:
```powershell
android screen resolve --screenshot=new_ui.png --string="input tap #<LABEL_INDEX>"
```
*Example:*
```powershell
android screen resolve --screenshot=new_ui.png --string="input tap #16"
# Outputs: input tap 541 1306
```

### 4. Execute Tap Command
Run the tap command using the Android SDK's `adb.exe` tool (located in `C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe`):
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell input tap <X> <Y>
```
