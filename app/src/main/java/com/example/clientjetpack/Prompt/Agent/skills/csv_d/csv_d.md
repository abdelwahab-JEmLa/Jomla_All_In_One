# Skill - CSV Database Query (csv_d)

This skill instructs the assistant on how to automatically search for, identify, and execute a query across the local CSV files pulled from the phone's external storage (`TestDatas/` folder) whenever a `TODO: csv_d` comment is placed in the codebase (e.g. `//TODO: csv_d trix`) or when a direct search query is typed in the chat. The assistant pulls the latest CSV files from the device, runs the search query against all CSV files, parses and groups matches by file, formats them as Markdown tables, saves the output to `copy_/csv_d/last_csv_d.md`, updates `app/src/main/java/skill_agent/csv_d/last_query.md`, and removes the comment.

---

## Trigger Phrases
- "csv_d"
- "Todo: csv_d"
- "csv_search"
- "csv_query"

---

## Steps to Execute

### 1. Locate Dynamic CSV TODO Comments
Search the codebase (`app/src/main/java`) for any dynamic CSV query comments:
- Query: `TODO: csv_d` (case-insensitive)
- Extract the file name, line number, and the query expression following `csv_d` (e.g., `trix` or `zohire`).

### 2. Pull the Latest CSV Files from the Device
Do not compile. Pull the active CSV export files from the connected device's external storage using ADB:
`& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /storage/emulated/0/Abdelwahab_jeMla.com/CSV_Export/TestDatas copy_/TestDatas`

### 3. Run the Search Script
Execute the search python script:
- Run `python copy_/csv_d/run_csv_d.py <query>` (replacing `<query>` with the expression or word to search).
- If triggered by a `TODO: csv_d` comment in the codebase, you can run `python copy_/csv_d/run_csv_d.py` without arguments and the script will automatically locate, execute, and delete the comment from the codebase.

### 4. Format and Display Report
Display a summary of the matches, including:
- Total matching rows and files.
- For each CSV file with matches, present a Markdown table with the columns and values of the matching rows (capped at 50 rows per file in the main report).

---

### 🔗 Liens Directs vers les Fichiers de configuration de ce Skill
* 📋 [csv_d.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/csv_d/csv_d.md)
* 🐍 [run_csv_d.py](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/csv_d/run_csv_d.py)
* 💾 [last_csv_d.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/csv_d/last_csv_d.md)
* ℹ️ [last_query.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/csv_d/last_query.md)
