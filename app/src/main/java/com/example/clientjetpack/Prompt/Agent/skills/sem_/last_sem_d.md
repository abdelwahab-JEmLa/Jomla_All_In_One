# Skill - Last Semantics Dump (last_sem_d)

This sub-skill is an extension of the **Semantics Inspector (sem_)** skill. 
It enforces the following persistence and direct-trigger rules:

1. **Direct Chat Trigger (Info Mode)**: 
   If the user enters `sem_` directly in the chat (without any variables), the assistant must immediately launch the `sem_` "Info Mode". 
   This means bypassing the codebase search for `TODO: sem_` comments, and jumping directly to executing an ADB uiautomator dump (`window_dump.xml`), extracting the active UI semantics, and formatting the results.

2. **Persistent Output (last_sem_d)**:
   Whenever the `sem_` skill successfully extracts and formats semantic data (whether triggered via a TODO comment or directly via the chat), the assistant must ALWAYS save a copy of the final formatted markdown report (the semantic tables and lists) into a file named `copy_/last_sem_d.md`.
   This ensures the user always has a persistently saved copy of the last semantic analysis.
