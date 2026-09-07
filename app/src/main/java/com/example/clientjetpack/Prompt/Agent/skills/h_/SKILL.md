---
name: h_
description: Afficher l'aide des skills disponibles. Trigger this whenever the user requests h_, help_, help_skill, or help_skills.
---

# Table des Skills Antigravity (AGY)

Voici la liste complète des compétences personnalisées globales disponibles. Vous pouvez déclencher chaque skill en saisissant son mot-clé (trigger phrase) dans le chat.

---

## 🌍 1. Catalogue des Skills Globaux (Système & Analyse)

Ces compétences sont installées au niveau global et servent à analyser le codebase, isoler des contextes ou orchestrer des tâches de développement.

| Nom du Skill | Fichier | Mots-clés (Triggers) | Description |
|:---|:---|:---|:---|
| **Aide des Skills** | [h_/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/h_/SKILL.md) | `h_`, `help_`, `help_skill`, `help_skills` | Affiche ce catalogue d'aide des compétences personnalisées. |
| **FragMap Data Flow** | [fm_/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/fm_/SKILL.md) | `fm_`, `fm_flow` | Explique et gère les synchronisations de données (Room, CSV, Firebase) dans les écrans FragMap. |
| **Fix TODOs** | [t_/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/t_/SKILL.md) | `t_`, `t_models`, `t_flow`, `t_usage`, `>clientApp`, `>ca`, `fix_todo` | Recherche et résout automatiquement les commentaires `TODO` dans le codebase actif. |
| **Supprimer Val -> Param** | [supp_val_.../SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/supp_val_et_remplace_par_passed_paramater_depuit_parent_val/SKILL.md) | `supp_val_`, `remplace_par_param_`, `val_to_param` | Supprime une propriété d'un data class et remplace ses usages par un paramètre passé depuis le parent ou un state local emember. Nettoie toute la chaîne de callbacks. |
| **Passe Paramètre Ici** | [passe_paramater_a_ici/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/passe_paramater_a_ici/SKILL.md) | `<a_ici`, `a_ici_`, `passe_a_ici`, `passe_param_ici`, `<passe todo.relative`, `passe_paramater_a_ici` | Résout les TODOs `<a_ici` / `<passe todo.relative` pour propager automatiquement un state/callback du composable parent vers l'enfant en un seul passage descendant. |
| **Passe on_ Callback Ici** | [passe_on_au_ici/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/passe_on_au_ici/SKILL.md) | `<passe_on todo.relative`, `passe_on_au_ici`, `on_a_ici_`, `passe_on_ici`, `<a_ici on_` | Résout les TODOs `<passe_on todo.relative` pour câbler automatiquement un callback `on_` lambda depuis l'enfant (déclencheur) jusqu'au parent (gestionnaire) en traversant tous les niveaux intermédiaires. |
| **Trouver le Parent Propriétaire** | [affiche_parent_val/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/affiche_parent_val/SKILL.md) | `<lien parent`, `lien_ver_paren_val`, `affiche_parent_val`, `<affiche_parent_val` | Remonte automatiquement la chaîne d'appels pour trouver le composable qui contient le `mutableStateOf` d'une variable et renvoie un lien Markdown cliquable vers ce fichier parent. |
| **Chained TODOs in Projects** | [chain_todos_au_autres_projects/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/chain_todos_au_autres_projects/SKILL.md) | `chain_todos_au_autres_projects`, `t_appClient_chain_todo`, `t>cli`, `>clientApp`, `>ca`, `clientApp`, `>light app` | Recherche et résout les commentaires `TODO` dans les projets externes (ClientJetPack ou Light App). |
| **TODO Context Exporter** | [t_contex_files_relative_pour_regle_todo/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/t_contex_files_relative_pour_regle_todo/SKILL.md) | `t_contex_files_relative_pour_regle_todo`, `t_contex`, `todo_context`, `save_c` | Extrait le contexte d'un `TODO` dans un fichier Markdown riche (`_contex.md`) pour être lu par une nouvelle session IA. |
| **Copy Coding Patterns** | [copie_paterns_et_fit/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/copie_paterns_et_fit/SKILL.md) | `copie_paterns_et_fit`, `copie_patterns_et_fit`, `t_copie_compos_et_fit`, `t_c_client` | Copie, adapte et déploie les architectures et patterns depuis le projet `ClientJetPack` vers le projet local. |
| **Isolate & Fix File** | [read_ingor_/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/read_ingor_/SKILL.md) | `read_ingor_<file>`, `read_i_list`, `read_ignor_last`, `ccl_` | Isole un fichier pour économiser les tokens d'analyse, applique des corrections puis restaure le contexte. |
| **Installer un Skill Global** | [installer_globale_skill/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/installer_globale_skill/SKILL.md) | `installe_<skill>`, `install global <skill>`, `installe_t_` | Copie un skill de l'espace de travail local vers vos répertoires de configuration globaux. |
| **Extract Dependency** | [t_extract/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/t_extract/SKILL.md) | `t_extract`, `extract_todo`, `TODO: extract` | Extrait automatiquement les blocs de code ou dépendances marqués par `TODO: extract`. |
| **Zip Colle** | [zip_colle/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/zip_colle/SKILL.md) | `zip_colle`, `colle_`, `colle`, `ok_`, `ok`, `z_` | Extrait le dernier zip/rar ou fichier Kotlin téléchargé, trouve sa correspondance dans le projet et l'écrase. |
| **Git Branch from TODOs** | [git_branch_todos/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/git_branch_todos/SKILL.md) | `git_branch_todos`, `creer_sub_branche_todo`, `b_t`, `gbt_`, `sub_branch_todo`, `t_bra` | Scanne le codebase pour détecter les TODOs, crée une branche Git au nom résumé des TODOs et associe la liste des TODOs en description de branche. |
| **Add Trigger to Skill** | [add_trigger_au_skill/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/add_trigger_au_skill/SKILL.md) | `tig_b_t_<term>`, `add_trigger_au_skill` | Ajoute automatiquement un trigger personnalisé au skill git_branch_todos. |
| **Fusionner vers Master** | [fusion_au_master/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/fusion_au_master/SKILL.md) | `fusion_au_master`, `fusion`, `merge_master` | Fusionne proprement la branche actuelle vers `master`/`main` : commit, fetch, merge, push et bascule finale sur la branche principale. |
| **Fusionner vers Tahfid** | [fusion_app_tahfide/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/fusion_app_tahfide/SKILL.md) | `fusion_app_tahfide`, `fusion_tahfid`, `merge_tahfid`, `fusion_a_tahfid` | Fusionne proprement la branche actuelle vers `app_tahfid_quran` : commit, fetch, merge, push et bascule finale sur la branche de production. |
| **Build APK Play Store Phone** | [build_apk_au_playe_store_phone/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/build_apk_au_playe_store_phone/SKILL.md) | `build_PS`, `build_apk_au_playe_store_phone`, `build_tahfid`, `b_tahfid`, `bt_`, `b_v+1` | Compile and deploy the Tahfid Quran application APK to the connected phone's Playe_Store/app_tahfid/ storage folder. |
| **Smart Build Router** | [smart_build_router/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/smart_build_router/SKILL.md) | `b_v+1`, `b_v+1+z`, `b_+1`, `b_+1+z`, `b+1`, `b+1+z`, `smart_build_router` | Route build requests depending on the active workspace context (Light App vs Client). |
| **Branch Git & TODOs Tahfid** | [t_branche_a_tahfid/SKILL.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/t_branche_a_tahfid/SKILL.md) | `t_branche_a_tahfid`, `tb_tahfid`, `t_bt_`, `tb_` | Scanne le codebase pour détecter les TODOs, crée une branche Git au nom résumé, crée un fichier de résumé dans `app_tahfid_todos/<resume_todos>.md` (ou `big_todo_jomla_app/` si ClientJetPack), commite ce fichier, pousse la branche vers le remote, et affiche un rapport détaillé. |

---

## 📂 Gestion des Références de Projets (`ref_<nom>` / `ref_json`)

Lorsque l’assistant rencontre un mot-clé ou déclencheur sous la forme `ref_<nom>` (par exemple `ref_DevApp`, `ref_agy_c`) ou `ref_json` (équivalent à `references`), il **doit** :
1. Lire le fichier de configuration `ref_json` (qui correspond au fichier [references.json](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/references.json)) à la racine de l’espace de travail ou dans le dossier antigravity-cli.
2. Rechercher dans `projects` la clé dont la liste `noms` contient le nom demandé (par exemple `DevApp`, `agy_c` ou `ref_json`), de manière insensible à la casse.
3. Extraire le chemin `path` spécifié pour ce projet.
4. Appliquer ce `path` comme répertoire racine cible pour toutes les lectures, écritures, recherches de fichiers ou exécutions de commandes liées à ce projet.

## 🛠️ Résolution des Noms de Skills Globaux
- Lorsque l'utilisateur fait référence à `h_` ou `help_skill`/`help_skills` (ex: "edite h_"), cela désigne le skill global d'aide [h_](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/h_/SKILL.md).
- Lorsque l'utilisateur fait référence à `b_c` ou `build_client` (ex: "edite b_c"), cela désigne le skill global de build [build_client](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/build_client/SKILL.md).
- L'assistant doit automatiquement cibler les fichiers de ces répertoires globaux sous `C:\Users\Abou Mohamed\.gemini\config\skills\` ou `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\`.

---

## 🔍 Instructions de Recherche / Filtrage

Si l'utilisateur ajoute un terme ou une requête après le mot-clé (ex: `h_ t_`, `h_ build`, `h_<terme>`) :
1. Extraire la `<requête>` recherchée.
2. Filtrer le tableau des **Skills Globaux** en ne conservant que les lignes où la `<requête>` correspond (partiellement ou totalement) à :
   - Le **Nom du Skill**
   - Les **Mots-clés (Triggers)**
   - La **Description**
3. Afficher uniquement les compétences correspondantes sous forme de tableau. Si aucun match n'est trouvé, afficher un message l'indiquant et lister le catalogue complet.
