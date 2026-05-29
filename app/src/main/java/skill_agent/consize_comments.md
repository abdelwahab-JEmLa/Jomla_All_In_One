# Skill - Concise Code (consize_comments)

This skill instructs the assistant on how to automatically make the code of context files as concise as possible by removing all unnecessary comments, log statements, and redundant semantics (such as Jetpack Compose modifiers or custom tags that don't affect core application logic), while strictly preserving the functional behavior of the code.

---

## Trigger Phrases
- "consize_commants"
- "co_"

---

## Steps to Execute

### 1. Identify Target Files
Identify the target Kotlin/Java/XML files in the current context or open editors that require minimization.

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
