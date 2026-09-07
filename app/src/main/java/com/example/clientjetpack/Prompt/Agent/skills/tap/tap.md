# Skill - Tap Android FAB (High-Speed Mode)

This skill instructs the assistant on how to automatically capture the screen layout, identify the Floating Action Button (FAB) or another targeted element, resolve its coordinates, and tap it as fast as possible whenever the user requests a "tap" (with or without a target query), or run `tap_l` for instant replay using saved coordinates.

---

## Trigger Phrases
- "tap"
- "tap <target>" (e.g., `tap "But1"`, `tap "UZA"`)
- "tap_l" (instant replay of the last tapped coordinates)
- "shel_tap_l" (launches the app on the phone, waits 1.5 seconds, then replays the last tap)

---

## Steps to Execute

### Method A: High-Speed Instant Replay (tap_l & shel_tap_l)
If `tap_l` or `shel_tap_l` is requested:
1. Directly execute the python script with the corresponding argument:
   - For simple replay:
     ```powershell
     python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\tap_fast.py" "tap_l"
     ```
   - For app launch + replay:
     ```powershell
     python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\tap_fast.py" "shel_tap_l"
     ```
2. This will instantly simulate the click at the last saved coordinates (either immediately or after booting up the app).

---

### Method B: High-Speed Layout-Based Tap (Default for standard queries)
This method executes in less than 1.5 seconds by parsing the UI layout tree directly, saving coordinates to `last_tap.txt` upon successful execution.

1. Run the `tap_fast.py` script, optionally passing the target element name as an argument:
   ```powershell
   python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\tap_fast.py"
   ```
   *To target a specific button or element (e.g., "But1_Export_M8_Room_To_Csv"):*
   ```powershell
   python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\tap_fast.py" "But1"
   ```

2. Confirm the script's output indicating it located the element, saved coordinates to `last_tap.txt`, and successfully triggered the click.

---

### Method C: Visual Bounding-Box Fallback
If the XML layout tree is unavailable or the element is not found, fall back to the visual method:

1. **Capture the Screen with Annotations**:
   ```powershell
   android screen capture -a -o new_ui.png
   ```

2. **Locate the Bounding Box Label**:
   Analyze `new_ui.png` to identify the bounding box label corresponding to the target element (e.g. `#16` for the FAB).

3. **Resolve Coordinates**:
   ```powershell
   android screen resolve --screenshot=new_ui.png --string="input tap #<LABEL_INDEX>"
   ```

4. **Execute Tap Command**:
   ```powershell
   & "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell input tap <X> <Y>
   ```
