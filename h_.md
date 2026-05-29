# Table des Skills Antigravity (AGY)

Voici la liste complète des compétences personnalisées disponibles dans ce projet. Vous pouvez déclencher chaque skill en saisissant son mot-clé (trigger phrase) dans le chat.

---

## 🛠️ Catalogue des Skills

| Nom du Skill | Fichier | Mots-clés (Triggers) | Description |
| :--- | :--- | :--- | :--- |
| **Annotated Screen Capture** | [`screenshot.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/screenshot.md) | `scr_s` | Capture l'écran de l'appareil Android ou de l'émulateur connecté avec des boîtes de délimitation étiquetées et l'affiche. |
| **Tap Android FAB** | [`tap.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/tap.md) | `tap` | Capture l'écran, localise le bouton flottant (FAB) ou l'élément ciblé, résout les coordonnées et simule un clic sur l'appareil. |
| **Fast Build and Install** | [`build.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/build.md) | `build` | Compile et installe rapidement l'application en mode hors-ligne optimisé (`--offline --parallel --build-cache --configuration-cache`). |
| **Fast Build, Install and Launch** | [`launch.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/launch.md) | `lance_r`, `l_`, `l_r` | Compile, installe l'application sur le téléphone et lance automatiquement son activité principale (`MainActivity`). |
| **Commit, Tag and Push** | [`push_tagged.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/push_tagged.md) | `push_taged`, `push_`, `p_` | Commite proprement les modifications locales, génère/incrémente intelligemment un tag Git, l'applique et pousse le tout sur GitHub. |
| **Todo Bubelle - UI Bug Hunter** | [`todo_bubelle.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/todo_bubelle.md) | `todo_bubelle`, `todo_b`, `fix_ui`, `bubelle` | Capture l'écran, analyse les bulles d'erreur/anomalies UI, identifie le composable responsable, applique un correctif dans le code, relance l'app (`lance_r`) et prend un screenshot de vérification (`scr_s`). |
| **Context Unique Working_IN** | [`context_working_in.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/context_working_in.md) | `agy_context_unique_workingIn_active`, `agy_context_unique_workingIn_desactive`, `c_w_a`, `c_w_d`, `c_w_e` | Active (isole), Désactive (restaure), ou vérifie l'état du contexte restreint uniquement sur `Working_IN.Feature`. |
| **Concise Code** | [`consize_comments.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/consize_comments.md) | `consize_commants`, `co_` | Enlève les commentaires, les logs et les semantics des fichiers du contexte pour rendre le code le plus concis possible sans en altérer le fonctionnement. |
| **Fast Launch Preview** | [`launch_preview.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/launch_preview.md) | `lp_`, `lance_preview` | Compile, installe et lance le preview de l'application de la façon la plus rapide possible. |
| **Fix TODOs and Launch** | [`t_.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/t_.md) | `t_`, `t_models`, `fix_todo` | Résout automatiquement les TODOs dans le code, compile et lance l'application (`l_r`), puis affiche le diff du code modifié à la fin. Supporte `t_models` pour inclure automatiquement la base de données et les modèles. |
| **Help Skills** | [`help_skill.md`](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/help_skill.md) | `help_skill`, `h_`, `help_` | Affiche ce tableau d'aide documentant l'ensemble des Skills disponibles. |

---

## 🔗 Liens Directs vers les Fichiers de configuration des Skills

Vous pouvez éditer et configurer directement ces fichiers dans le répertoire de l'agent :

* ℹ️ [Help Skill](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/help_skill.md)
* 🛠️ [Fast Build and Install](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/build.md)
* 🚀 [Fast Build, Install and Launch](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/launch.md)
* 📸 [Annotated Screen Capture](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/screenshot.md)
* 👆 [Tap Android FAB](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/tap.md)
* 🐛 [Todo Bubelle - UI Bug Hunter](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/todo_bubelle.md)
* 📂 [Context Unique Working_IN](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/context_working_in.md)
* 📝 [Concise Code](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/consize_comments.md)
* ⚡ [Fast Launch Preview](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/launch_preview.md)
* ✅ [Fix TODOs and Launch](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/t_.md)
* 📦 [Commit, Tag and Push](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/push_tagged.md)
