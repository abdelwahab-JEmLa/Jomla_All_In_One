# Skill - Real-Time Logcat Inspector (log_)

This skill instructs the assistant on how to automatically search for, identify, and execute logcat log extraction based on a `TODO: log_ <Tag/Filter>` comment in the codebase, filter the adb logcat output by that specific tag or context, display the matching logs to the user, and remove the comment.

---

## Trigger Phrases
- "log_"
- "Todo: log_"
- "logcat"
- "adb_log"

---

## Steps to Execute

### 1. Locate Dynamic Log TODO Comments
Search the codebase (`app/src/main/java`) for any log comments using the `grep_search` tool:
- Query: `TODO: log_` (case-insensitive)
- If a comment like `//TODO: log_ <Tag>` or `//TODO: log_` is found, extract:
  - The target file path.
  - The line number.
  - The tag/filter word if specified (e.g. `//TODO: log_ M2Client` -> Tag is `M2Client`). If no tag is specified, use the name of the file or enclosing class as the default tag (e.g., if in `Setter_LongOperations.kt`, default tag is `Setter_LongOperations`).
- If no `TODO: log_` comment is found on disk, use any recently modified class/tag (e.g. `Setter_LongOperations` or `M2Client`) or ask the user for a tag.

### 2. Dump Logcat via ADB
Using the absolute path of ADB: `C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe`
Run the following PowerShell command to dump the logcat buffer (using the `-d` option to dump and exit, or `-t 500` to get the latest 500 lines):
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" logcat -d -v time
```

### 3. Filter and Format the Logs
- Parse the output of the logcat command.
- Filter the lines to find those matching the target tag/filter (case-insensitive).
- Format the filtered log lines in a clean, readable Markdown block or table showing:
  - **Timestamp**
  - **Level** (Verbose, Debug, Info, Warning, Error)
  - **Tag**
  - **Message**
- If no logs match the specific filter, output a message stating so, and show the latest 20 general logs from the device for context.

### 4. Remove the Triggering TODO Comment
- If a `TODO: log_` comment was found in step 1, remove it from the file using `replace_file_content` or `multi_replace_file_content`.

### 5. Present Clickable Links
Always present the user with clickable links to the modified/inspected files and the skill configuration file itself:
- 📝 [Logcat Inspector Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/log_.md)
