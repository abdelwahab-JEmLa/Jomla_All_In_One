---
name: learn_improve_skill_depuit_convesation_imp_
description: Scans the conversation to identify the active skill, extracts corrections, and generates/applies proposed improvements (handling folder renames, frontmatter sync, and help catalog registration).
---

# Skill - Improve Skill from Conversation (learn_improve_skill_depuit_convesation)

This skill automates the process of updating/improving existing global Antigravity skills based on the context, bugs, successes, or user corrections encountered in the current conversation. It extracts which skill was primarily active, identifies how to optimize or correct its instructions, and proposes an update to its `SKILL.md` file.

---

## Trigger Phrases
- "/learn/learn"
- "learn_improve_skill_depuit_convesation"
- "learn_improve_skill_depuit_convesation_imp_"
- "/learn_improve_skill_depuit_convesation_imp_"
- "/learn_improve_skill"
- "/improve"
- "/imrove"
- "/<skill> /improve"
- "/<skill> /imrove"
- "/<skill> /imp"
- "improve le skill"
- "improve_skill"
- "l_s"
- "l_s_"
- "imp_l_s"
- "imp_l_s_"
- "i_l_s"
- "i_l_s_"
- "i_t_"
- "impro>"
- "imp>"
- "imp_"
- "improve_"
- "imrove_"
- "improv_"
- "i_build_client"
- "i_zip_colle"
- "i_log_"
- "i_tap_"
- "i_installer_globale_skill"
- "i_lit_patterns"
- "i_consize_comments"
- "i_passe_recieve_val"
- "i_sem_"

---

## Steps to Execute

### 1. Retrieve the Active Conversation ID
Get the active conversation ID from the context metadata (e.g., `Conversation ID: <ID>`).

### 2. Locate and Parse the Conversation Transcript
**Fast-Track (Économie de tokens)** : 
1. Utilise prioritairement ta **mémoire contextuelle immédiate** pour analyser les erreurs, les requêtes, et le comportement du skill ciblé.
2. **NE LIS PAS** les fichiers `transcript.jsonl` via PowerShell sauf si la conversation a été tronquée et que l'historique te manque. Cela économise énormément de temps et de tokens.

Si nécessaire, cherche dans :
- Transcript: `<appDataDir>\brain\<conversation-id>\.system_generated\logs\transcript.jsonl`
- Full Transcript: `<appDataDir>\brain\<conversation-id>\.system_generated\logs\transcript_full.jsonl`

Using PowerShell `Get-Content` or standard tools, search the transcript for:
- All skills viewed or referenced (e.g., matching `.gemini/config/skills/` or `.gemini/antigravity-cli/skills/`).
- Identify the **primary active skill** (the skill whose instructions were read or whose trigger matched the initial user intent, such as `t_`, `build_client`, or `zip_colle`).
- **Specific Mode (Self-Improvement)**: If `imp_l_s`, `imp_l_s_`, `imp_`, `improve imp_`, or `/learn_improve_skill_depuit_convesation_imp_` is triggered without a target skill name, bypass the transcript scan and directly target the `learn_improve_skill_depuit_convesation_imp_` skill itself for improvements.
- **Targeted Improvement (/<skill> /improve)**: If the user explicitly triggers the improvement with `/<skill_name> /improve`, `/<skill_name> /imrove`, or `/<skill_name> /imp` (e.g. `/t_ /improve`), bypass the transcript scan and directly target the specified `<skill_name>` for improvements.

### 3. Extract Corrections, Bugs, and Resolutions
Analyze the transcript steps and the current conversation history to find:
- **Default Analysis (No target specified)**: Si l'utilisateur n'a pas spécifié de requête ou de skill précis, l'assistant doit lire la conversation actuelle pour identifier les points clés les plus importants à améliorer dans le skill actif.
- **Identification des erreurs** : Repérer les erreurs commises par le skill ou l'assistant durant la session, en comprendre l'origine, et modifier le skill pour y remédier.
- **Optimisation de rapidité/efficacité** : Si des chemins plus directs, plus rapides ou plus efficaces sont possibles pour accomplir la tâche, les intégrer au déroulement du skill.
- **Corrections utilisateur** : Capturer les retours explicites de l'utilisateur (ex: "non", "erreur", "échec") et les corrections de code apportées.
- **Auto-Trigger Detection** : Si l'utilisateur a invoqué un skill en utilisant une nouvelle phrase de déclenchement (new trigger) non reconnue au préalable, proposer automatiquement d'ajouter ce nouveau trigger à la liste des triggers du skill ciblé.
- **Capture exacte (Adaptation au User)** : Cherche **exactement** le mot ou la phrase tapée par l'utilisateur pour lancer la quête (même si c'est une faute de frappe comme `/imrove` ou `cl_`) et ajoute-la obligatoirement aux `Trigger Phrases`.

### 4. Formulate Proposed Skill Updates
- Locate the global skill's `SKILL.md` file:
  - `C:\Users\Abou Mohamed\.gemini\config\skills\<skill_name>\SKILL.md`
  - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\<skill_name>\SKILL.md`
- Read the existing contents using PowerShell (`Get-Content`).
- Draft the proposed changes (e.g. adding new steps, highlighting constraints, adding new trigger phrases).
- **Rename + Frontmatter Sync Rule**: If the proposal includes renaming the skill folder (e.g. to add a trigger suffix), the diff must also include updating the `name:` property in the SKILL.md YAML frontmatter to match the new folder name.

### 5. Proposal Bypass for Direct Edits
- **Bypass Rule**: If the skill is triggered via `/improve` or `/<skill_name> /improve`, the assistant must **NOT** create a `learning_proposal.md` artifact and must **NOT** stop to wait for user confirmation.
- Instead, directly edit the targeted skill's `SKILL.md` file and apply the changes immediately in the same turn.

### 6. Apply Changes Globally
Write the updated content directly to both global paths:
- `C:\Users\Abou Mohamed\.gemini\config\skills\<skill_name>\SKILL.md`
- `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\<skill_name>\SKILL.md`

### 7. Sync Help Catalog (`h_`)
- Check if the target skill is registered in `h_/SKILL.md`.
- If **not listed**: add a new row in the appropriate section with its name, relative file link, triggers, and description.
- If **already listed**: update its triggers list and ensure the file link points to the correct folder name (important after a rename).
- Remove the local `learning_proposal.md` file and confirm success.
