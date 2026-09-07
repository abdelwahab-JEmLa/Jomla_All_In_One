# Skill - Real-Time Logcat Filter (log_f)

This skill instructs the assistant on how to automatically capture the current device logcat and filter it in real-time by search terms specified directly in the user's trigger phrase (e.g., `log_f data xp4` or `log_f But3_CsvToRoom`).

---

## Trigger Phrases
- `log_f <term1> <term2> ...`
- `log_f`

---

## Steps to Execute

### 1. Extract Search Terms
Extract the search terms specified after `log_f` in the user's prompt:
- If terms are provided (e.g., `log_f data xp4`), extract all terms (e.g., `["data", "xp4"]`).
- If no terms are provided, default to tracking recently modified classes or operations (e.g., `["But3_CsvToRoom", "But6_FireBaseToCsv"]`) or ask the user for terms.

### 2. Dump Logcat via ADB
Directly dump the latest 20000 lines from the device's logcat buffer (this keeps the log retrieval fast while providing a wide enough history to not miss events):
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" logcat -d -t 20000 -v time
```

### 3. Filter the Log Lines
Filter the dumped logcat output. A line is considered a match if it contains **all** of the extracted search terms (case-insensitive).

### 4. Format and Display the Logs
Present the matching log lines as a Markdown table:
- **Timestamp**
- **Level** (V, D, I, W, E, F)
- **Tag**
- **Message**

If no logs match the criteria, report the result clearly and print the last 20 raw logcat lines from the device for context.

---

## 🔗 Direct Links
* 📋 [Logcat Filter Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/log_f.md)
