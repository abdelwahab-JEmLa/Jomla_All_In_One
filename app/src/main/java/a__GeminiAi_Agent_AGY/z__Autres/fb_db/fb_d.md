# Skill - Firebase Cache Database Query (fb_d)

This skill instructs the assistant on how to automatically search for, identify, and execute a Firebase offline database cache query whenever a `TODO: fb_d` comment is placed in the codebase (e.g. `//TODO: fb_d -OV3rmZB2ffLjTxgS0p4`) or when a direct search query is typed in the chat. The assistant pulls the latest Firebase Realtime DB offline cache database from the device, runs the search query against the cache, extracts matched paths and values, formats the results with direct console links, saves the output to `copy_/fb_db/last_fb_d.md`, updates `app/src/main/java/skill_agent/fb_db/last_query.md`, and removes the comment.

---

## Trigger Phrases
- "fb_d"
- "Todo: fb_d"
- "firebase_search"
- "fb_query"

---

## Steps to Execute

### 1. Locate Dynamic Firebase TODO Comments
Search the codebase (`app/src/main/java`) for any dynamic Firebase cache query comments:
- Query: `TODO: fb_d` (case-insensitive)
- Extract the file name, line number, and the query expression following `fb_d` (e.g., `-OV3rmZB2ffLjTxgS0p4` or `zohire`).

### 2. Pull the Latest Database from the Device
Do not compile. Pull the active Firebase Realtime Database cache from the connected device using ADB:
1. Ensure the destination file exists in `/data/local/tmp` with `666` permissions:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "touch /data/local/tmp/fb_db_temp && chmod 666 /data/local/tmp/fb_db_temp"`
2. Copy the active Firebase cache file to the app's cache directory:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles cp databases/abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app_default cache/fb_db_temp"`
3. Copy from app cache to `/data/local/tmp/fb_db_temp` (which has writable permissions):
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles cp cache/fb_db_temp /data/local/tmp/fb_db_temp"`
4. Pull the file locally to `fb_db_temp` in the project root:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /data/local/tmp/fb_db_temp fb_db_temp`

### 3. Run the Search Script
Execute the search python script:
- Run `python copy_/fb_db/run_fb_d.py <query>` (replacing `<query>` with the expression or word to search).
- If triggered by a `TODO: fb_d` comment in the codebase, you can run `python copy_/fb_db/run_fb_d.py` without arguments and the script will automatically locate, execute, and delete the comment from the codebase.

### 4. Format and Display Report
Display a summary of the matches, including:
- Total matching paths/records.
- List of matching paths.
- For each path, provide:
  - Clickable link to open the node in the Firebase Console:
    `https://console.firebase.google.com/project/abdelwahab-jemla-com/database/abdelwahab-jemla-com-default-rtdb/data/~2F...` (where `/` in the path is replaced by `~2F`).
  - Clickable link to open the raw JSON API endpoint:
    `https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app/...json`
  - The formatted JSON content of the value block (use syntax highlighting).

---

### 🔗 Liens Directs vers les Fichiers de configuration de ce Skill
* 📋 [fb_d.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/fb_db/fb_d.md)
* 🐍 [run_fb_d.py](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/fb_db/run_fb_d.py)
* 💾 [last_fb_d.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/fb_db/last_fb_d.md)
* ℹ️ [last_query.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/fb_db/last_query.md)
