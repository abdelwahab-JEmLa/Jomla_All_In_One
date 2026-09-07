# Table des Skills Hardware & PC (HP)

Voici la liste des compétences d'administration et de diagnostic PC/Hardware disponibles dans ce projet. Vous pouvez déclencher ce tableau d'aide en saisissant le mot-clé `hp_` (ou `hw_`, `help_pc`, `help_hw`, `skillpc_`, `h_pc`) dans le chat.

---

## ⚡ Règles d'Exécution Rapide (Rules for Fast Execution)
- Si l'utilisateur saisit `h_pc` (ou `hw_`, `help_pc`, `help_hw`, `skillpc_`, `hp_`), l'assistant **doit charger immédiatement ce fichier** [hw_.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md) et afficher son contenu. Ne pas scanner d'autres répertoires.
- Pour les compétences Android (`h_`, `help_`), charger immédiatement [help_skill.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/help_skill.md).

---

## Trigger Phrases
- `hp_`
- `hw_`
- `help_pc`
- `help_hw`
- `skillpc_`
- `h_pc`

---

## 🖥️ Catalogue des Skills PC & Hardware

| Nom du Skill                       | Fichier                                                                                                                                                  | Mots-clés (Triggers)                                                                                       | Description                                                                                                                                                                                                     |
|:-----------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **PC Specialist**                  | [`skill_pc.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/skill_pc.md)                     | `skill_pc`, `pc_spec`, `pc_status`, `diagnose_pc`                                                          | Exécute des diagnostics complets sur le PC hôte Windows (CPU, RAM, Disque, Réseau, Processus) et génère un rapport de performance.                                                                                |
| **Firebase M1 to Excel**           | [`skill_pc.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/skill_pc.md)                     | `fb_m1_excel`, `m1_excel`, `Todo: fb_m1_excel`                                                             | Extrait les références M01Produit (M1) de Firebase offline cache et les exporte dans un fichier Excel stylisé sur le Bureau.                                                                                    |
| **PC Cleaner**                     | [`clean_pc.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/clean_pc.md)                     | `clean_pc`, `nettoyer_pc`, `pc_clean`, `cleanup_pc`, `cl_p`, `cl_e`, `cl_s`                                                        | Nettoie les fichiers temporaires, le cache de Windows Update, les fichiers logs et vide la corbeille pour libérer de l'espace sur le disque C:. |
| **View Desktop Image**             | [`scr_.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/scr_.md)                             | `scr_<name>`, `scr_<number>`, `image_<name>`                                                               | Trouve une image spécifique ou un numéro (ex: `scr_2` pour `2.jpg` ou `image_2`) sur le Bureau, l'ouvre et en fait l'analyse. |
| **Fix Excel Comment & Filter**     | [`fix_commante_exel.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/fix_commante_exel.md) | `fix_excels`, `fix_excels_`, `fix_commante_exel_`, `fix_comment_excel_`, `fix_excel_`, `fix_excel` | Trouve les commentaires de contrainte (ex: `dep>0`) dans les fichiers Excel du Bureau, applique le filtre et supprime le commentaire. |
| **Fix Specified Comments**         | [`t_ex_commantaire.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/t_ex_commantaire.md) | `t_ex_commantaire`, `t_ex_commentaire`, `ex_commantaire`, `fix_ex_commantaire`, `t_exe_<file_name>`, `t_ex_<file_name>` | Navigue vers un fichier spécifique, y localise les commentaires d'instruction ou TODOs, applique le correctif demandé et supprime le commentaire. |

---

## 🔗 Liens Directs vers les Fichiers de configuration

* 🖥️ [PC Specialist](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/skill_pc.md)
* 🧹 [PC Cleaner](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/clean_pc.md)
* ⚙️ [PC Help Page (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md)
* 🖼️ [View Desktop Image (scr_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/scr_.md)
* 📊 [Fix Excel Comment (fix_commante_exel)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/fix_commante_exel.md)
* 📝 [Fix Specified Comments (t_ex_commantaire)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/t_ex_commantaire.md)
