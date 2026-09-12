---
name: tap_lit_debug_log_cat
description: Automatically detects TODO: tap_lit_debug_log_cat, injects a debug log, triggers a tap action via screen resolution, and reads the resulting logcat output.
---

# Skill - Tap & Read Logcat (tap_lit_debug_log_cat)

This skill automates debugging a specific user interaction by locating the `TODO: tap_lit_debug_log_cat` comment, injecting a `Log.d` statement on click, building/deploying the application, executing a UI tap command on that element, and dumping/reading the logcat logs to diagnose issues.

---

## Trigger Phrases
- "tap_lit_debug_log_cat"
- "Todo: tap_lit_debug_log_cat"
- "tap_logcat"

---

## Steps to Execute

### 1. Locate the Target TODO Comment
Search the codebase (under `app/src/main/java`) using PowerShell for the pattern:
`//TODO: tap_lit_debug_log_cat` or similar.
Identify the target file and line number.

### 2. Inject Log and Semantics
- Inject a debug log entry:
  `android.util.Log.d("DEBUG_TAP", "Component clicked at line <line_number>")`
  into the `onClick` handler of the component or layout element nearest to the comment.
- Add a temporary semantics property if needed to help locate the element in the hierarchy:
  `.semantics { set(value = "debug_target", key = SemanticsPropertyKey("debug_tag")) }`
- Remove the triggering `TODO` comment.

### 3. Build and Install (Deploy to Device)
Compile and deploy the client application onto the connected Android device or emulator. Run the appropriate build command (e.g. `gradlew installDebug` or use the `build_client` workflow).

### 4. Locate and Tap the Component
- Take a screen capture with annotations:
  `android screen capture -a -o new_ui.png`
- Find the bounding box/label that matches the component or its `debug_tag` semantics.
- Resolve the coordinates:
  `android screen resolve --screenshot=new_ui.png --string="input tap #<LABEL_INDEX>"`
- Perform the click:
  `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell input tap <X> <Y>`

### 5. Dump and Read Logcat
Wait for 2-3 seconds for logs to accumulate, then dump the Logcat buffer:
`& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" logcat -d -v time | Select-String "DEBUG_TAP"`
Display the resulting log lines in a clear Markdown table or code block.
