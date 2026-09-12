# Skill - Room Database Query (room_d)

This skill instructs the assistant on how to automatically search for, identify, and execute a dynamic database query whenever a `TODO: room_d` comment is placed in the codebase (e.g. `//TODO: room_d m8 key 4 = fqTx`). The assistant pulls the latest database from the device, translates the query expression into SQL, queries the database, formats the results as a Markdown table (with specific columns for `M8` transactions, or generic columns for other tables), saves the output to `copy_/room_d/last_room_d.md`, updates `app/src/main/java/skill_agent/room_d/last_query.md`, and removes the comment.

---

## Trigger Phrases
- "room_d"
- "Todo: room_d"
- "room_query"

---

## Steps to Execute

### 1. Locate Dynamic Room TODO Comments
Search the codebase (`app/src/main/java`) for any dynamic room database queries:
- Query: `TODO: room_d` (case-insensitive)
- Extract the file name, line number, and the query expression following `room_d` (e.g., `m8 key 4 = fqTx`).

### 2. Pull the Latest Database from the Device
Do not compile. Directly pull the active SQLite database from the connected device using ADB:
1. Copy database to the app's cache directory:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles cp databases/app_database cache/app_database"`
2. Set readable permissions:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles chmod 666 cache/app_database"`
3. Copy to temporary path:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles cp cache/app_database /data/local/tmp/app_database"`
4. Pull the file locally to `app_database_temp`:
   `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /data/local/tmp/app_database app_database_temp`

### 3. Translate the Query Expression into SQL
Translate the simplified expression into a valid SQL query against the SQLite tables:
- **Table Mappings (Case Insensitive)**:
  - `m8` / `bon` -> `M8BonVent`
  - `m2` / `client` -> `M2Client`
  - `m1` / `produit` -> `M01Produit`
  - `m15` / `grossist` -> `M15Grossist`
  - `m9` / `compt` -> `M09AppCompt`
- **Expression Parsing**:
  - `key 4 = <value>` translates to `keyID LIKE '%<value>'` (e.g. `key 4 = fqTx` becomes `keyID LIKE '%fqTx'`).
  - Other conditions like `<column> = <value>` should map to SQL (`<column> = '<value>'` for strings or `<column> = <value>` for numbers).
  - Example: `m8 key 4 = fqTx` becomes `SELECT * FROM M8BonVent WHERE keyID LIKE '%fqTx' ORDER BY creationTimestamps DESC`.

### 4. Execute Query and Format Report
Run the query on `app_database_temp` and write the result into `copy_/room_d/last_room_d.md` formatted as a Markdown table.
- If querying `M8BonVent`, use the standard **13 important credit transaction columns**:
  `ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client`
- For other tables, dynamically format the Markdown table columns based on the table's column names.

### 5. Update Last Query Configuration
Update `app/src/main/java/skill_agent/room_d/last_query.md` with:
- **Source File**: Clickable path to the file that contained the TODO comment.
- **Expression**: The raw query expression.
- **SQL Statement**: The translated SQL statement executed.
- **Timestamp**: Current date/time.

### 6. Remove the Triggering TODO Comment
Remove the `TODO: room_d` comment from the source file.
