---
name: h_
description: Afficher l'aide des skills disponibles. Trigger this whenever the user requests h_, help_, help_skill, or help_skills.
---

# Table des Skills Antigravity (AGY)

Voici la liste complète des compétences personnalisées disponibles, organisées par sections thématiques.
Déclenchez chaque skill en saisissant son mot-clé (trigger phrase) dans le chat.

---

## 📂 1. Définition et Règles du Skill `h_` (`regle h_`)

### 📋 Rôle & Description
Le skill `h_` est le catalogue d'aide central des compétences personnalisées de l'agent. Il affiche le catalogue complet organisé par sections ou permet une recherche ciblée (`h_ <terme>`).

### 📌 Édition de Skill vs Exécution (`regle h_` / `edite h_`)
- Lorsqu'utilisateur demande de **"régler"**, **"éditer"**, **"modifier"** ou **"configurer"** le skill `h_` (ex: `regle h_`, `regle le h_`, `edite h_`, `modifier h_`) :
  1. **NE PAS exécuter** le skill `h_` (ne pas afficher la table d'aide).
  2. **Proposer puis éditer uniquement** les fichiers de définition du skill [SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/h_/SKILL.md) (dans `config/skills/h_/` et `antigravity-cli/skills/a__skill_help/h_/`).
  3. Toujours générer l'artefact `learning_proposal.md` et demander l'accord explicite avant d'écrire.
- L'exécution du skill `h_` (affichage du catalogue d'aide) ne doit se faire **QUE si l'utilisateur saisit le mot-clé seul** (ex: `h_`, `help_`).

### 📌 Localisation des répertoires du skill `h_`
- **Config Global** : [C:\Users\Abou Mohamed\.gemini\config\skills\h_\SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/h_/SKILL.md)
- **CLI Central** : [C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\a__skill_help\h_\SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/a__skill_help/h_/SKILL.md)

> ⚠️ Ces deux fichiers doivent toujours être synchronisés (même contenu).

---

## 📂 2. Skills par Sections (`antigravity-cli/skills/`)

Les compétences spécialisées sont organisées par sous-dossiers thématiques dans :
`C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\`

---

### 📁 `Copy_Skills/` — Copie de code, patterns et fichiers
> Compétences liées à la copie de code, la réutilisation de patterns et le transfert de fichiers entre projets.

| Skill | Triggers | Description courte |
|:---|:---|:---|
| [To_Externale_Ai](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/To_Externale_Ai) | `cc_`, `cc_se`, `con_cop`, `copy_context`, `todo_to_extarnale_chat` | Copie le contexte TODO vers un chat IA externe |
| [copie_paterns_et_fit](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/copie_paterns_et_fit) | `copie_paterns_et_fit`, `t_c_client`, `t_copie_compos_et_fit` | Copie et adapte des patterns/composables entre projets |
| [t_contex_files_relative_pour_regle_todo](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/t_contex_files_relative_pour_regle_todo) | `t_contex`, `todo_context`, `save_c` | Extrait le contexte complet d'un TODO dans un fichier Markdown |
| [t_extract](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/t_extract) | `t_extract`, `extract_todo`, `TODO: extract` | Extrait des blocs de code ou dépendances identifiés |
| [zip_colle](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/zip_colle) | `zip_colle`, `colle_`, `colle`, `z_`, `ok_` | Extrait et remplace des fichiers Kotlin depuis les téléchargements |
| [cop_last](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/cop_last) | `cop_last` | Copie le dernier fichier actif |
| [copy_skill](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/copy_skill) | `copy_skill` | Copie un skill vers un autre répertoire |
| [copy_save_au_frech_list_copy_uncomited_edited_files](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Copy_Skills/copy_save_au_frech_list_copy_uncomited_edited_files) | `copy_uncomited`, `cu_`, `sc_u`, `save_uncomited_`, `copy_edited` | Copie tous les fichiers non commités (edited) du projet Android actif et sauvegarde leurs chemins dans `cop_last/list_copied_files` |

---

### 📁 `Puch_Skills/` — Build, Git push/tag/fusion
> Compétences liées à la compilation d'APKs, au versioning, aux commits, tags et fusions de branches Git.

| Skill | Triggers | Description courte |
|:---|:---|:---|
| [build_apk_au_playe_store_phone](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/build_apk_au_playe_store_phone) | `bt_`, `b_v+1`, `build_tahfid`, `build_PS` | Compile et déploie l'APK Tahfid sur le téléphone |
| [build_client](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/build_client) | `b_c`, `b+1`, `b_c_all`, `build_client` | Compile l'app client JetPack et incrémente la version |
| [smart_build_router](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/smart_build_router) | `b+1`, `b_+1`, `b+1+z`, `b_v+1+z` | Router de build : redirige vers Tahfid ou Client selon le workspace |
| [p_v](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/p_v) | `p_v` | Commit avec la version comme message + tag `par_version_instaled` + push |
| [push_tagged](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/push_tagged) | `push_`, `p_`, `push_taged`, `p_tag=cleanup` | Commit, tag auto-versionné et push distant |
| [p_skills](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/p_skills) | `p_skills`, `p_sk`, `p_ski`, `revers_p_skill`, `r_p_sk`, `rev_p_sk`, `rev_p_ski`, `reverse_p_sk`, `reverse_c_sk`, `rev_c_sk` | Synchronisation bidirectionnelle des skills entre AGY Global et le projet Android (`a__GeminiAi_Agent_AGY`) |
| [fusion_au_master](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/fusion_au_master) | `fusion_au_master`, `fusion`, `merge_master` | Fusionne la branche courante vers master/main |
| [fusion_app_tahfide](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/fusion_app_tahfide) | `fusion_tahfid`, `fusion_a_tahfid`, `merge_tahfid` | Fusionne la branche courante vers app_tahfid_quran |
| [git_branch_todos](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Puch_Skills/git_branch_todos) | `b_t`, `gbt_`, `git_branch_todos`, `sub_branch_todo` | Crée une branche Git à partir de la liste des TODOs |

---

### 📁 `Todo_Skills/` — Détection, résolution et gestion des TODOs
> Compétences liées à la détection, résolution automatique, export et traçabilité des TODOs dans le codebase.

| Skill | Triggers | Description courte |
|:---|:---|:---|
| [t_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/t_) | `t_`, `fix_todo` | Résout automatiquement les TODOs du codebase actif |
| [chain_todos_au_autres_projects](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/chain_todos_au_autres_projects) | `chain_todos_au_autres_projects`, `t>cli`, `>clientApp`, `>light app` | Détecte et traite les TODOs inter-projets |
| [t_appClient_chain_todo](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/t_appClient_chain_todo) | `t>cli`, `>ca`, `clientApp` | TODOs chaînés vers l'application cliente |
| [t_contex_files_relative_pour_regle_todo](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/t_contex_files_relative_pour_regle_todo) | `t_contex`, `todo_context`, `save_c` | Extrait le contexte complet d'un TODO dans un fichier Markdown |
| [t_extract](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/t_extract) | `t_extract`, `TODO: extract` | Extrait les blocs de code ou dépendances marqués |
| [git_branch_todos](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/git_branch_todos) | `b_t`, `gbt_`, `t_bra` | Crée une branche Git à partir des TODOs |
| [t_branche_a_tahfid](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/t_branche_a_tahfid) | `t_branche_a_tahfid`, `tb_tahfid`, `t_bt_`, `tb_` | Crée une branche et rapport de TODOs pour Tahfid |
| [search_last_todo_historique_git](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/search_last_todo_historique_git) | `search_last_todo` | Historique et traçabilité des modifications TODO dans Git |
| [To_Externale_Ai](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Todo_Skills/To_Externale_Ai) | `cc_`, `cc_se`, `con_cop`, `copy_context` | Export du contexte TODO vers un chat IA externe |

---

### 📁 `Installe_Skill_Skills/` — Création et installation de skills
> Compétences liées à la création, la configuration et l'installation globale de nouveaux skills.

| Skill | Triggers | Description courte |
|:---|:---|:---|
| [installer_globale_skill](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Installe_Skill_Skills/installer_globale_skill) | `installe_<skill>`, `install global <skill>`, `installe_t_` | Copie un skill local vers le répertoire global |
| [add_trigger_au_skill](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Installe_Skill_Skills/add_trigger_au_skill) | `tig_b_t_<term>`, `add_trigger_au_skill` | Ajoute un nouveau trigger à un skill existant |
| [skill_creatore](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/Installe_Skill_Skills/skill_creatore) | `skill_creatore` | Assistant de création de nouveau skill |

---

### 📁 `z__Autres/` — Skills divers (Compose, debug, logcat, DB…)
> Compétences variées : navigation Compose, debug logcat, Room/Firebase, styles, et amélioration de skills.

| Skill | Triggers | Description courte |
|:---|:---|:---|
| [affiche_parent_val](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/affiche_parent_val) | `affiche_parent_val`, `lien_ver_paren_val`, `<lien parent` | Trouve le composable parent propriétaire d'une valeur |
| [passe_paramater_a_ici](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/passe_paramater_a_ici) | `<a_ici`, `a_ici_`, `passe_a_ici`, `passe_param_ici`, `<passe todo.relative` | Propage un état/valeur vers le bas de la hiérarchie Compose |
| [passe_on_au_ici](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/passe_on_au_ici) | `<passe_on todo.relative`, `on_a_ici_`, `passe_on_ici`, `passe_on_au_ici` | Remonte un callback on_ vers le parent dans la hiérarchie Compose |
| [passe_recieve_val](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/passe_recieve_val) | `passe_recieve_val` | Propage un état/callback avec minimum de tokens |
| [supp_val_et_remplace_par_passed_paramater_depuit_parent_val](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/supp_val_et_remplace_par_passed_paramater_depuit_parent_val) | `supp_val_`, `remplace_par_param_`, `val_to_param` | Supprime une valeur locale et la remplace par un paramètre passé |
| [consize_comments](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/consize_comments) | `consize_comments`, `ccl_` | Supprime les commentaires, logs et semantics pour code concis |
| [read_ingor_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/read_ingor_) | `read_ingor_<file>`, `read_i_list`, `read_ignor_last`, `ccl_` | Isole temporairement des fichiers pour économiser les tokens |
| [log_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/log_) | `log_` | Inspecte le logcat ADB en temps réel selon les TODOs |
| [sem_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/sem_) | `sem_` | Injecte des semantics et inspecte la hiérarchie UI Android |
| [tap](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/tap) | `tap` | Capture l'écran, identifie le FAB et exécute un tap ADB |
| [tap_lit_debug_log_cat](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/tap_lit_debug_log_cat) | `tap_lit` | Tap + lecture du logcat pour debug rapide |
| [fm_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/fm_) | `fm_`, `fm_flow` | Synchro Room/CSV/Firebase dans les écrans FragMap |
| [learn_improve_skill_depuit_convesation_imp_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/z__Autres/learn_improve_skill_depuit_convesation_imp_) | `/improve`, `/learn/learn`, `/learn_improve_skill_depuit_convesation_imp_` | Améliore un skill à partir des corrections de la conversation |

---

### 📁 `a__skill_help/` — Aide des skills
> Le catalogue d'aide lui-même.

| Skill | Triggers | Description courte |
|:---|:---|:---|
| [h_](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/a__skill_help/h_/SKILL.md) | `h_`, `help_`, `help_skill`, `help_skills` | Affiche ce catalogue d'aide des skills |

---

## 📂 3. Gestion des Références de Projets (`ref_<nom>` / `ref_json`)

**Répertoire de référence** :
```
C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\b__References_Files\
```

Lorsque l'assistant rencontre un mot-clé ou déclencheur sous la forme `ref_<nom>` (par exemple `ref_DevApp`, `ref_agy_c`) ou `ref_json` (équivalent à `references`), il **doit** :
1. Chercher le fichier de configuration dans le répertoire [`b__References_Files`](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/b__References_Files) (ex: `references.json`, ou tout autre fichier de mapping présent dans ce dossier).
2. Le lire via PowerShell :
   ```powershell
   Get-Content -Path "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\b__References_Files\references.json"
   ```
3. Rechercher dans `projects` la clé dont la liste `noms` contient le nom demandé, de manière insensible à la casse.
4. Extraire le chemin `path` spécifié pour ce projet.
5. Appliquer ce `path` comme répertoire racine cible pour toutes les opérations liées à ce projet.

> ⚠️ **Chemin canonique** : Toujours résoudre `ref_json` depuis `b__References_Files\`. Ne pas utiliser l'ancien chemin `antigravity-cli\references.json` sauf si `b__References_Files` est absent.

---


## 🔍 Instructions de Recherche / Filtrage

Si l'utilisateur ajoute un terme ou une requête après le mot-clé (ex: `h_ t_`, `h_ build`, `h_<terme>`) :
1. Extraire la `<requête>` recherchée.
2. **Exact Match Check** : Vérifier si la `<requête>` correspond exactement à l'un des **Triggers** d'un skill de n'importe quelle section.
   - Si c'est le cas, ne pas afficher le tableau. Lire et afficher directement l'intégralité du fichier `SKILL.md` correspondant dans le chat.
3. **Filtre de recherche** : Si ce n'est pas un match exact, filtrer **tous les tableaux de sections** en ne conservant que les lignes où la `<requête>` correspond (partiellement ou totalement) à :
   - Le **Nom du Skill**
   - Les **Triggers**
   - La **Description courte**
4. Afficher uniquement les compétences correspondantes sous forme de tableau à 3 colonnes (Skill, Triggers, Description). Si aucun match n'est trouvé, afficher un message l'indiquant et lister le catalogue complet.

