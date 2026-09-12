---
name: t_
description: Automatically searches for, identifies, and fixes TODO comments in the active codebase. Includes dispatch capabilities.
---

# Skill - Fix TODOs (t_)

This skill instructs the assistant on how to automatically search for, identify, and fix `TODO` comments in the active codebase, and display a list of changed files at the end of the explanations.

Additionally, this skill supports the **`t_models`** sub-trigger, which automatically adds `appDatabase.kt` and the `Models` package to the active restricted context before proceeding with the standard steps.

If **`t_ai`** (or **`t_agy`**) is triggered, the assistant will specifically search for, identify, and fix TODOs located inside the reference directory **`r_aa__GeminiAi_Agent_AGY`** (`app/src/main/java/a__GeminiAi_Agent_AGY`).

If **`t_usage`** is triggered, the assistant will also compute and display the percentage and amount of model tokens consumed so far, and what remains in the current session (out of the calibrated 200,000 token limit) at the end of the execution report.

If **`>clientApp`** or **`>ca`** is triggered, the assistant will automatically redirect and execute the **Client JetPack Fix TODOs & Coding Patterns (t_appClient_chain_todo)** skill to inspect relative/chained TODOs and search coding patterns or files in the external client codebase.

**Central Dispatcher & Priority Override Capability**: If a `TODO` comment contains `v_u` (or `v_u_`), do NOT resolve any TODOs; immediately redirect and automatically execute the **`v_u`** skill (detecting uncommitted files and copying them to clipboard and hist_copie.md). If a `TODO` comment contains a trigger phrase for another custom skill (e.g., `TODO: log_`, `TODO: sem_`, `TODO: con_c`, `TODO: cop_`, `TODO: room_d`), the assistant must automatically chain and execute the corresponding custom skill's steps on that file/package, rather than applying a manual code fix.

---

## Trigger Phrases
- "t_"
- "t_ai"
- "t_agy"
- "t_models"
- "t_usage"
- ">clientApp"
- ">ca"
- "fix_todo"
- "fix_todos"
- "t_regle_sub_todos"

---

## Steps to Execute

### When "t_models" is triggered:

#### 1. Add appDatabase.kt and Models packages to Context (cwa_)
Automatically invoke the context addition (`cwa_`) rules for the following packages:
- **`AppDatabase`** (Package: `com.example.light_app_controles.Modules.Base.SQL.Daos`)
- **`Models`** (Package: `EntreApps.Shared.Models`)

Generate and append the folder rules to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore):

```text
# Added via t_models (com.example.light_app_controles.Modules.Base.SQL.Daos)
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/com/
!app/src/main/java/com/example/
!app/src/main/java/com/example/light_app_controles/
!app/src/main/java/com/example/light_app_controles/Modules/
!app/src/main/java/com/example/light_app_controles/Modules/Base/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/Daos/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/Daos/**

# Added via t_models (EntreApps.Shared.Models)
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/EntreApps/
!app/src/main/java/EntreApps/Shared/
!app/src/main/java/EntreApps/Shared/Models/
!app/src/main/java/EntreApps/Shared/Models/**
```

#### 2. Execute standard TODO fixing
Proceed directly to the standard steps below to locate and fix TODOs.

### When "t_usage" is triggered:

#### 1. Calculate and Report Token Usage
At the end of the standard execution steps, estimate the total token count of the current conversation (using the transcript file size `transcript_full.jsonl` size in bytes / 4 + system context overhead of ~15,000 tokens) and calculate what percentage of the calibrated 200,000 token limit this represents. Output both the consumed token percentage and amount, as well as the remaining token amount and percentage prominently at the end of the report.

### When ">clientApp" or ">ca" is triggered:

#### 1. Dispatch to Client JetPack Fix TODOs & Coding Patterns Skill
Immediately redirect execution to the **Client JetPack Fix TODOs & Coding Patterns (t_appClient_chain_todo)** skill. Run its steps to locate outstanding relative/chained TODOs in the client codebase, search coding patterns or files, apply fixes, and report success. Do not execute the standard local codebase TODO steps.

### When "t_ai" (or "t_agy") is triggered:

#### 1. Target the a__GeminiAi_Agent_AGY Directory
Le répertoire cible est toujours :
`<project_root>\app\src\main\java\a__GeminiAi_Agent_AGY`
(= `D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY` sur le workspace actif)

*Méthode de Scan Rapide — 2 passes filtrées :*

**Passe 1 — Fichiers code actifs (`.kt`, `.java`, `.xml`) :**
```powershell
Get-ChildItem -Recurse -Include "*.kt","*.java","*.xml" "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY" |
  Select-String -Pattern "//\s*TODO|<!--\s*TODO" |
  Select-Object Filename, LineNumber, Line | Format-Table -AutoSize -Wrap
```

**Passe 2 — Fichiers non-code (`.md`, `.py`, `.txt`, `.bat`), en excluant SKILL.md et dossiers References/Examples :**
```powershell
Get-ChildItem -Recurse -Include "*.md","*.py","*.txt","*.bat" "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY" |
  Where-Object {
    $_.Name -ne "SKILL.md" -and
    $_.FullName -notmatch "\\References\\" -and
    $_.FullName -notmatch "\\Examples\\"
  } |
  Select-String -Pattern "//\s*TODO|#\s*TODO|<!--\s*TODO" |
  Select-Object Filename, LineNumber, Line | Format-Table -AutoSize -Wrap
```

**Règles de filtrage des résultats :**
- Ignorer les lignes dont le contenu est une description de skill (phrases longues en prose).
- Ne retenir que les TODOs qui ressemblent à des **actions concrètes** (ex: `//TODO(1): ...`).
- Nettoyer les marqueurs `//<--` et lignes temporaires dans les fichiers non-code après résolution.

#### 2. Fix + Sync vers le skill global correspondant

Après avoir résolu un TODO dans un fichier de `a__GeminiAi_Agent_AGY`, **synchroniser automatiquement le même fix vers les fichiers skill globaux miroirs**.

**Règle de résolution du miroir global :**
Pour chaque fichier AGY modifié (`a__GeminiAi_Agent_AGY/<Section>/<skill_name>/SKILL.md`), construire les 2 chemins globaux :
- `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\<Section>\<skill_name>\SKILL.md`
- `C:\Users\Abou Mohamed\.gemini\config\skills\<Section>\<skill_name>\SKILL.md`

**Étapes de synchronisation :**
1. Identifier le chemin relatif `<Section>/<skill_name>` du fichier AGY modifié.
2. Construire les 2 chemins globaux.
3. Vérifier leur existence (`Test-Path`).
4. Si existants → appliquer le **même fix** (`replace_file_content`) sur chacun.
5. Si non existants → signaler dans le rapport final sans bloquer.

Proceed directly with the standard steps to analyze, fix the TODOs found in `a__GeminiAi_Agent_AGY`, and remove the TODO comments without compiling.

---

### Standard Steps to Execute:

**Filtre des TODOs différés (De branche A regle apre)** :
- Si le trigger utilisé est le standard `t_` (ou tout autre trigger différent de `t_regle_sub_todos`), **ignorer** et ne pas afficher ni résoudre les TODOs contenant le motif `//TODO(1 De branche A regle apre):` (ou toute variante de cette forme indiquant un traitement différé).
- Si le trigger utilisé est `t_regle_sub_todos`, inclure et résoudre ces TODOs spécifiques.

Pour lister les TODOs applicables selon le cas, exécuter la commande PowerShell appropriée.
**RÈGLE OBLIGATOIRE — Scan Synchrone** : La commande PowerShell de scan TODO (`Get-ChildItem | Select-String ...`) doit TOUJOURS être lancée avec un `WaitMsBeforeAsync` suffisamment élevé (minimum 20000 ms) pour garantir qu'elle se termine de manière synchrone, sans être envoyée en tâche de fond. Ne jamais lancer ce scan en background ni utiliser `manage_task`.


*Pour une recherche standard (excluant les TODOs différés) :*
```powershell
Get-ChildItem -Recurse -Include "*.kt","*.java","*.xml","*.md" "<project_root>\app\src\main" `
  | Select-String -Pattern "//\s*TODO|#\s*TODO|<!--\s*TODO" `
  | Where-Object { $_.Line -notmatch "//TODO\(1 De branche A regle apre\):" } `
  | Select-Object Filename, LineNumber, Line | Format-Table -AutoSize -Wrap
```

*Pour le trigger `t_regle_sub_todos` (incluant uniquement ou également ces TODOs) :*
```powershell
Get-ChildItem -Recurse -Include "*.kt","*.java","*.xml","*.md" "<project_root>\app\src\main" `
  | Select-String -Pattern "//TODO\(1 De branche A regle apre\):" `
  | Select-Object Filename, LineNumber, Line | Format-Table -AutoSize -Wrap
```

**Workspace Detection Rule**: Use the **active workspace root** as `<project_root>`:
- If the user's active workspace is `d:\AndroidStudioProjects\ClientJetPack`, use that path.
- Otherwise, fall back to `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles`.
- The path is always declared in the conversation metadata (`Additional metadata > active workspaces`).

**Pattern note**: Use `//\s*TODO|#\s*TODO|<!--\s*TODO` (not just `TODO`) to avoid false positives
from method names like `toDouble()`, `toString()`, `toDoubleOrNull()`, and file references.

Note that some TODOs may be relative to/dependent on others, often indicated with specific markers like `//<--` or comments referring to previous/other TODOs (e.g. `//TODO(2.C Relative Au Todo(1):`). The assistant must carefully analyze these relationships and implement dependencies in the correct order.

### 1.2. Token Economy — Lecture Ciblée des Fichiers

**RÈGLE OBLIGATOIRE** : Minimiser les tokens consommés lors de l'analyse des fichiers.

#### A. Pré-scan avant lecture complète
Pour tout fichier > 150 lignes, avant de le lire en entier, exécuter un `Select-String` ciblé pour confirmer qu'il contient le pattern recherché. Si zéro résultat → ne pas lire le fichier. Si match → appliquer la Règle B.

```powershell
Select-String -Path "<fullpath>" -Pattern "<pattern_cherché>"
```

#### B. Lecture par plage autour du TODO (fichier > 120 lignes)
Quand le numéro de ligne du TODO est connu (fourni par le scan initial), lire avec une fenêtre de ±60 lignes via `view_file` :
```
view_file(StartLine=max(1, todoLine-50), EndLine=todoLine+60)
```
Ne lire le fichier entier que si la signature globale ou la structure complète est indispensable pour comprendre le contexte.

#### C. Callers : utiliser `-Context` au lieu de `view_file` complet
Pour voir comment un composant est appelé, utiliser `-Context 3,12` sur `Select-String` plutôt que de lire le fichier appelant entièrement :
```powershell
Get-ChildItem -Recurse -Include "*.kt" "<root>" `
  | Select-String -Pattern "<NomDuComposant>" -Context 3,12 `
  | Format-List
```
Cela fournit 3 lignes avant + 12 lignes après chaque appel — suffisant pour voir les arguments passés — sans charger tout le fichier.

### 1.5. Present Beginner-Friendly Summary
**CRITICAL RULE**: Before explaining the details of the code changes, the assistant MUST present a beginner-friendly summary of the problem, the list of concerned files to change, and a brief explanation of what changes will be made. **CRITICAL EXECUTION MANDATE**: The assistant MUST invoke `replace_file_content` (or other write tools) to apply the code modifications and delete the TODO comments in the VERY SAME turn. NEVER end the turn after outputting the summary without calling the file editing tools.

### 2. Implement the fixes in Code / Delegate to Skills
- **`v_u` Priority Override Check**: Immediately after scanning TODOs (and regardless of whether the TODO is relative, deferred, or standard), check if ANY scanned `TODO` line contains `v_u` or `v_u_`.
  - If a `TODO` containing `v_u` or `v_u_` is detected anywhere in the results, **stop fixing/resolving TODOs immediately** and redirect execution to launch the **`v_u`** skill instead. Do NOT resolve or delete any TODOs in this run.
- **Skill Dispatcher Check**: For each found `TODO` comment, check if it contains a trigger for another custom skill:
  - If it contains `/installer_globale_skill moi le skill <nom>` (e.g. `TODO: /installer_globale_skill moi le skill passe>recieve>val`) :
    1. Extraire `<nom>` depuis le commentaire (ex: `passe_recieve_val`).
    2. Vérifier si le skill existe déjà localement (`.agents/skills/<nom>/SKILL.md`) ou globalement.
    3. **S'il n'existe pas** : créer un SKILL.md minimal en inférant `name`, `description`, et `Trigger Phrases` à partir du nom et des commentaires adjacents du TODO.
    4. Exécuter les étapes de `installer_globale_skill` pour copier vers les deux répertoires globaux et supprimer la copie locale.
    5. Supprimer le TODO une fois l'installation confirmée.
  - If it contains `passe>recieve>val` or `passe_recieve_val` (e.g. `TODO: passe>recieve>val` or `TODO: passe>affiche_xxx`), execute the **Parameter Propagation (passe_recieve_val)** skill steps.
  - If it contains `log_` (e.g. `TODO: log_`), execute the **Real-Time Logcat Inspector (log_)** skill steps.
  - If it contains `sem_` (e.g. `TODO: sem_`), execute the **Semantics Inspector (sem_)** skill steps.
  - If it contains `con_` (e.g. `TODO: con_c` or `TODO: co_`), execute the **Concise Code (consize_comments)** skill steps.
  - If it contains `cop_` (e.g. `TODO: cop_`), execute the **Copy Package / Sibling Files (cop_last)** skill steps.
  - If it contains `room_d` (e.g. `TODO: room_d`), execute the **Room Database Query (room_d)** skill steps.
  - If it contains `t_git` or `search_last_todo_historique_git` (e.g. `TODO: t_git`), execute the **Search Last TODO Historique Git (search_last_todo_historique_git)** skill steps.
  - If it contains `tap screen` or `tap_screen` (e.g. `TODO: tap screen` or `fait tap screen`), execute the **Tap Android FAB (tap)** skill steps.
  - If it contains `<a_ici` or `<passe todo.relative` (without `on_`), execute the **Passe Paramètre Ici (passe_paramater_a_ici)** skill steps.
  - If it contains `<passe_on todo.relative` or `on_a_ici_` or `<a_ici on_`, execute the **Passe on_ Callback Ici (passe_on_au_ici)** skill steps.
  - If it contains `supp_val_` or `remplace_par_param_` or `val_to_param` (e.g. `TODO: supp_val_` or `TODO: supp_val_ affiche_X`), execute the **Supprimer Val → Param (supp_val_et_remplace_par_passed_paramater_depuit_parent_val)** skill steps.
- **Standard Fixes & Mandatory Implementation**: If no skill trigger is matched, analyze the `TODO` comment requirements, **fully implement the required feature, fix, or business logic code in the file(s)** specified or implied by the `TODO`, verify that the code solution satisfies the `TODO` requirement, and ONLY THEN delete the `TODO` comment and its indicator lines (such as `//<--`, `//<-`, or inline TODO pointers) using `replace_file_content` or `multi_replace_file_content`. Never delete a `TODO` comment without first implementing the code functionality it demands.
- **CRITICAL RULE**: Do NOT run compilation (like gradle compile) to verify errors during the `t_` skill execution. The `t_` skill is strictly to apply the code fixes without compiling. Compilation should only be run if explicitly requested by the user.


### 3. Report Success and Display Changed Files
Provide the user with a simple report at the end.
**CRITICAL RULE**: Do NOT display any details, explanations, code blocks, or summaries of the changes made to the code in the final report.
The final report must only include:
- **The time to complete the quest (e.g., "Temps estimé pour terminer la quête : 1:30").**
- **The list of changed files with clickable links.**
