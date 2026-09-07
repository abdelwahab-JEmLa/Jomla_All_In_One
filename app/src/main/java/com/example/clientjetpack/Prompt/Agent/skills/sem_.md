# Skill - Real-Time Semantics Inspector (sem_)

This skill instructs the assistant on how to automatically detect any dynamic developer TODO comment containing `sem_` or `filter` (e.g., `//TODO: sem_ $variable` or `//TODO: filter $variable`) next to any component or layout element, automatically inject the corresponding custom semantics block directly onto the modified component or the component marked with `//<--` (such as a Text or specific view), remove the comment, and **immediately dump the active Android UI hierarchy using ADB uiautomator dump without rebuilding the app**, then extract and display the 13 important credit transaction values in a beautiful table to the user in near real-time.

---

## Trigger Phrases
- "sem_"
- "sem_d"
- "Todo: sem_"
- "Todo: filter"
- "sem_l"

---

## Steps to Execute

### 1. Locate Dynamic Semantics TODO Comments
Search the codebase (`app/src/main/java`) for any dynamic semantics/filter comments using the `grep_search` tool:
- Query: `TODO: sem_` or `TODO: filter` (case-insensitive)
- Extract the file name, line number, and targeted variable/filter expression.

### 2. Inject Semantics Modifier
- **No-Injection Rule (Info Mode):** If the `TODO` comment is just `TODO: sem_` without any variable or expression attached (e.g. `//TODO: sem_`), DO NOT inject a new `.semantics` block. Instead, skip directly to Step 3 and Step 4 to read the existing custom semantics properties from the active UI component.
- **Existing Semantics Block Rule:** If the `TODO: sem_` comment is already located inside a `.semantics(mergeDescendants = true) {` block:
  - If the comment contains an expression (e.g., `//TODO(1): sem_ list_M8bon . f key id last 4 = 7xp4`), the assistant must parse this expression (e.g., converting `list_M8bon . f key id last 4 = 7xp4` into `active_Datas.list_M8bon?.filter { it.keyID.takeLast(4) == "7xp4" } ?: emptyList()`) and inject it as a new `set(value = ..., key = SemanticsPropertyKey("..."))` inside the existing block. The block can have 4, 5, 6 or more `set()` statements.
  - The assistant must extract all the `set()` expressions and infos defined within the block. The assistant must then format and save these `set()` infos (supporting any number of sets: 3, 4, 5, 6 or more), along with the source file name (e.g., `A_Main_Preview_BonVentEtateScreen.kt`) and the exact line numbers of each `set()` call, into the file `skill_agent/sem_/last_semantics.md`.
- Locate the modified component or the specific component/line marked with `<--` (e.g., where the arrow comment `//<--` is placed next to the component).
- Inject a Jetpack Compose `.semantics` modifier directly on this component or layout element, setting the variable as a custom semantics property.
- **Dynamic Filtering Rule:** If the dynamic comment specifies a filter condition (e.g., `//TODO: sem_ allbons filter credit type`), parse the expression and inject the filtered value (e.g., `allBons.filter { it.etateActuellementEst.credit_type }`) rather than the raw variable.
- **Multi-Set Semantics Rule:** If the dynamic comment specifies multiple expressions or variables separated by `et`, `and`, `,`, or `&` (e.g., `//TODO: sem_ listM8bon .filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID } et listM8bon`), parse each of them and generate a separate `set(value = <expression>, key = SemanticsPropertyKey("<key_name>"))` statement for each expression inside the Compose `.semantics` block. If 2, 3, or more sets are specified, generate all of them inside the same `.semantics` block.
- **Critical Placement:** Inject the `.semantics` modifier directly into the modified component or the component marked with `//<--` rather than outer layout containers, ensuring the custom semantics properties are attached precisely to that element:
  ```kotlin
  Text(
      text = "...",
      modifier = Modifier
          .padding(16.dp)
          .semantics(mergeDescendants = true) {
              set(value = expression1, key = SemanticsPropertyKey("key1"))
              set(value = expression2, key = SemanticsPropertyKey("key2"))
              set(value = expression3, key = SemanticsPropertyKey("key3"))
          }
  )
  ```
- Remove the triggering `TODO` comment from the source file.

### 3. Dump and Pull UI Hierarchy via ADB (No Rebuild)
Do NOT run any Gradle compilation or build tasks. Directly capture the current active screen hierarchy from the device:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell uiautomator dump /sdcard/window_dump.xml
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /sdcard/window_dump.xml
```

### 4. Parse and Display the Semantics Data
Read the pulled `window_dump.xml` using the `view_file` tool. Parse the node hierarchy corresponding to the Compose container and extract the active transaction data.
- **List vs Info Display Rule:** When you parse the extracted semantic value:
  - If the extracted value represents a collection or **list** of items, display it as a full Markdown table (see table structure below).
  - If the extracted value represents a single object or basic **info** (e.g., not a list), display the semantic info cleanly as text, bullet points, or key-value pairs (NOT as a table).
If multiple custom semantics properties or sets were generated (e.g. `listM8bon`, `listM8bon_filtered`, `allBons`, or any additional sets like 4, 5, 6 or more sets), the assistant must parse and show the contents of each set in a clear, formatted presentation or dedicated Markdown table, detailing exactly what each set contains.
For any set representing transaction data that requires table formatting, construct and display a clean, beautiful Markdown table representing the main **13 important values** of the **`M8` Bons (`M8BonVent`)** under the **Credit** context:
1. **ID**: Short unique identifier of the bon (last 4 characters of `keyID`).
2. **Date & Heure**: Formatted timestamp/time of the transaction (`creationTimestamps` / `heurDebutInString`).
3. **État (Type)**: The transaction type state (`etateActuellementEst`).
4. **Montant Principal**: The main amount for the specific type transaction (`montant_principale_du_type`).
5. **Versement Fait**: The payment amount made (`versement_fait`).
6. **Ancien Crédit**: The previous debit balance of the client (`ancien_credit`).
7. **Nouveau Crédit**: The updated outstanding balance after this transaction (`new_credit_apre_tout_fait`).
8. **Crédit Cumulé**: The accumulated credit recorded (`sum_De_Credit_Fait`).
9. **Versement**: The active versement value (`versement`).
10. **Crédit Fait**: The active credit value (`credit_fait`).
11. **Nouvelle Situation**: The resulting customer situation balance (`new_situation`).
12. **Total Sauvegardé**: The total amount saved (`totale_saved`).
13. **Client**: The associated client parent ID (`parent_M2Client_KeyID`).

Use the following Markdown table structure containing all 13 columns to display the results:

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `3tUR` | 30 Avril \| 22:27 | Credit | 10890.00 دج | - | - | 10890.00 دج | 10890.00 دج | - | 10890.00 دج | 10890.00 دج | - | `GFD` |

### 5. Fast Read Last Semantics (sem_l)
If the user directly types **"sem_l"** in the chat, the assistant must bypass all other steps and simply read the contents of `skill_agent/sem_/last_semantics.md` and display the last saved semantics information directly to the user.
