# Skill - Context Transfer (conTr_)

This skill teaches the assistant how to recover, catalog, and overwrite context from past implementation sessions by reading and managing files inside the context transfer folder [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).

Whenever the user triggers "conT_", "conTr_", "/contexTrensefert", "context_transfer", "ctsave_", "ctc_", "ctecrase_", "cwc_", or "ct_deepRead_", the assistant must follow these instructions.

---

## Trigger Phrases
- "conT_"
- "conTr_"
- "/contexTrensefert"
- "context_transfer"
- "ctsave_"
- "ctc_"
- "ctecrase_"
- "cwc_"
- "ct_deepRead_"
- "ct_l"

---

## Steps to Execute

### When "cwc_<conversation_id>" (or "cwc_<conversation_id>_agy" or "cwc_") is triggered:
#### 1. Locate and Read the Context File
Locate the context file matching the conversation ID in [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext) (e.g., `<conversation_id>_agy.md`).
#### 2. Display Context Content Only
Display the contents of the context markdown file to the user. Do **NOT** read, open, or inspect any of the actual source code files listed or mentioned within it.

### When "ct_deepRead_<conversation_id>" (or "ct_deepRead_<conversation_id>_agy" or "ct_deepRead_") is triggered:
#### 1. Read the Context File
Locate and read the context file matching the conversation ID in [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext) (e.g., `<conversation_id>_agy.md`).
#### 2. Open and Read All Referenced Source Files
Extract the file paths of all source files listed or modified in that context file. Open and read all of these source files to load their complete context into the AI session.

### When "ct_l" is triggered:
#### 1. Find the Latest Context File
Locate the most recently modified/created `.md` file inside the [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext) folder.
#### 2. Deep Read Referenced Source Files
Extract all file paths listed or modified in that latest context file. Open and read each of these source files to load their complete context into the current AI session.

### When "ctc_" is triggered:
#### 1. List Available Contexts
Read all `.md` files in [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).
#### 2. Display a Table of Conversations
Render a clean Markdown table with:
- **Conversation File**: Clickable link to the context file.
- **ID de la Conversation**: The conversation ID extracted from the filename.
- **Petit Résumé**: A brief 1-2 sentence summary of what implementation context is captured in that file.

### When "ctecrase_" is triggered:
#### 1. Identify Target Context File
Look up the context file associated with the current conversation ID (or a specified ID) in [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).
#### 2. Overwrite Content
Overwrite the contents of the target file with the updated explanation/architecture details from the current conversation using the `write_to_file` tool with `Overwrite: true`.

---

### When standard triggers ("conT_", "conTr_", "/contexTrensefert", "context_transfer", "ctsave_") are triggered:
#### 1. Read Context Folder
Read the directory contents of [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).
Read each `.md` file in this directory to understand the templates and codebase structures (each context file is named using the format `<conversation_id>_agy.md`).
#### 2. Apply the Context to New Requests
Use the loaded context files as a reference template. For example, if the user asks to "create M13_Operation_Fab", replicate the exact directory layout, action files, ViewModel states, and database sync methods described in the context files (such as [f96cf255-aa97-44e3-82b7-4c6e83c2df8f_agy.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext/f96cf255-aa97-44e3-82b7-4c6e83c2df8f_agy.md)) for the new model.
