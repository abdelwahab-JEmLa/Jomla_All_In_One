# Skill - Concise Code (consize_comments)

This skill instructs the assistant on how to automatically make the code of context files as concise as possible by removing all unnecessary comments, log statements, and redundant semantics (such as Jetpack Compose modifiers or custom tags that don't affect core application logic), while strictly preserving the functional behavior of the code.

Additionally, this skill supports batch concising via the `con_c` trigger, which recursively identifies all sibling files and subfiles of the package containing the `TODO: con_c` comment and applies concising to all of them.

---

## Trigger Phrases
- "consize_commants"
- "co_"
- "con_"
- "con_c"
- "TODO: con_c"

---

## Steps to Execute

### 1. Locate and Concise Files with `TODO` Comments containing `co_` or `con_c`
When triggered, the assistant should automatically:
- Search the codebase (within `app/src/main/java`) using a `grep_search` query for:
  - `co_` (specifically looking for `TODO` comments like `//TODO(1): co_` or `TODO` containing `co_`).
  - `con_c` (specifically looking for `TODO` comments like `//TODO(1): con_c` or `TODO` containing `con_c`).
- **If `TODO: con_c` is triggered**:
  - Find the directory (package) containing the file where the `TODO: con_c` comment is located.
  - Recursively locate all sibling files and subfiles in that package directory (all `.kt` or `.java` files).
  - Apply the code concising process to **all** these sibling and subfiles.
  - Remove the triggering `TODO: con_c` comment from the file.
- **If `TODO: co_` is triggered**:
  - Apply the code concising process to that specific file only, and remove the triggering `TODO` comment.
- If no files are flagged with a `co_` or `con_c` comment, fall back to identifying target files in the active context or open editors that require minimization.

### 2. Strip Comments
Locate and remove all unnecessary comments:
- Single-line comments starting with `//` (unless they are compiler directives, suppressions, or critical configuration comments).
- Multi-line comments (`/* ... */`), including non-essential KDoc/JavaDoc blocks, unless explicitly required to keep.

### 3. Strip Log Statements
Locate and remove logging statements that do not affect return values or control flow:
- `Log.v(...)`, `Log.d(...)`, `Log.i(...)`, `Log.w(...)`, `Log.e(...)`
- `println(...)`, `print(...)`, `System.out.println(...)`
- Any custom log wrapper calls.

### 4. Strip Redundant Semantics (Jetpack Compose / UI)
Identify Jetpack Compose `Modifier.semantics` or other semantics/accessibility properties that can be safely removed or simplified without altering layout sizing, user inputs, or main logic flows:
- `clearAndSetSemantics { ... }`
- Redundant `contentDescription = null` or non-essential screen-reader properties, if requested.
- Empty or unused modifier declarations.

### 5. Review and Verify Functionality
Ensure that:
- The code syntactically compiles successfully (no missing imports, no dangling brackets, or broken references).
- The logical behavior (control flow, UI elements, user interaction) is completely unchanged.
- Perform a fast build of the application (`build`) to verify that no compilation errors were introduced.
