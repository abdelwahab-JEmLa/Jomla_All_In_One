---
name: read_ingor_
description: Temporarily isolates files during debugging/editing to save tokens, and restores the context once done. Supports turbo mode (ccl_).
---

# Skill - Isolate & Fix File (read_ingor_<filename>)

Ce skill permet à l'assistant d'isoler temporairement un ou plusieurs fichiers lors d'une tâche de débogage ou d'édition afin de ne pas consommer les tokens avec le reste du projet, puis de restaurer le contexte initial une fois la tâche accomplie.

---

## Trigger Phrases
- `read_ingor_<filename>`
- `read_ignor_<filename>`
- `isole_<filename>`
- `read_i_list` (pour lister les fichiers de contexte disponibles)
- `read_ignor_last` ou `read_ingor_last` (isole et corrige le dernier contexte)
- `consome_c_last` ou `cc_last` ou `ccl_` (consomme le dernier contexte de façon optimisée — **SANS relire les fichiers sources**)

---

## Steps to Execute

### === CAS A : L'utilisateur déclenche `read_i_list` ===

1. **Lister les contextes :**
   Utilisez l'outil de listing pour trouver tous les fichiers se terminant par `_contex.md` dans le dossier `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\t_\contexs\`.
2. **Extraire les métadonnées :**
   Lisez brièvement chaque fichier trouvé pour extraire la cible du TODO (souvent marquée par `**Cible :**`).
3. **Afficher le tableau :**
   Présentez à l'utilisateur un tableau Markdown contenant tous les contextes extraits :
   | ID (Conversation) | Titre / Cible du TODO | Fichier (Cliquable) |
   |---|---|---|
   | `<id>` | `<Texte du TODO>` | [`<id>_contex.md`](file:///.../<id>_contex.md) |

*(L'utilisateur pourra alors copier le nom du fichier pour lancer l'isolation).*

---

### === CAS B : L'utilisateur déclenche `read_ingor_<filename>` ou `read_ignor_last` ===

(Mode classique — isole les fichiers, relit les sources, applique le fix)

#### 1. Sauvegarde (Backup)
Lisez le contenu actuel des fichiers `.antigravityignore` et `.geminiignore` (situés à la racine du projet).
Sauvegardez leur contenu exact dans un fichier temporaire local (par exemple `app/src/main/java/skill_agent/temp_ignore_backup.txt`).

#### 2. Isolation (Token Saving)
Écrasez le contenu de `.antigravityignore` et `.geminiignore` avec les règles d'isolation adaptées.
**🚨 ATTENTION (Nouvelle Session) :** Si l'utilisateur vous demande d'isoler via un ID ou un fichier (ex: `read_ingor_123_contex.md`), ou via `read_ignor_last` (dernier fichier dans `contexs/`), trouvez ce fichier dans `app/src/main/java/skill_agent/t_/contexs/`.
Vous DEVEZ récupérer toutes les règles de la **"Section 10. Règles d'Isolation"** de ce fichier pour ne pas masquer les fichiers relatifs !

Si c'est une isolation simple sans fichier de contexte, appliquez ces règles :
```text
# Ignorer tout
*
# Ne pas ignorer les dossiers
!*/
# Toujours garder l'accès aux skills IA !
!*skill_agent/**
# Ne pas ignorer le fichier ciblé
!*<filename>
```
*(Dans le cas standard, remplacez `<filename>` par le nom exact du fichier).*

#### 3. Read and Fix
1. Si un fichier `_contex.md` est utilisé, **lisez attentivement sa section "Flux d'exécution attendu (Flow)"** pour comprendre exactement ce que vous devez corriger.
2. Lisez le contenu du fichier ciblé (qui est maintenant isolé).
3. Effectuez les modifications, réparations ou ajouts nécessaires (fixes) directement via les outils d'édition de code.

#### 4. Restauration du contexte (Restore)
Une fois les modifications terminées avec succès :
1. Restaurez le contenu d'origine de `.antigravityignore` et `.geminiignore` en lisant la sauvegarde.
2. Supprimez le fichier de sauvegarde temporaire.
3. **CRITIQUE :** Si le TODO résolu mentionnait `t_copiePattersApp`, utilisez `grep_search` pour localiser le TODO équivalent dans le projet source `D:\AndroidStudioProjects\ClientJetPack` et supprimez-le également.

#### 5. Report Success
Présentez à l'utilisateur :
- Un résumé de ce qui a été modifié.
- Un affichage du diff (Git-style) montrant les modifications du fichier.
- Une confirmation claire que les fichiers d'ignore ont été restaurés et que l'ancien contexte est de nouveau actif.

---

### === CAS C : L'utilisateur déclenche `consome_c_last` / `cc_last` / `ccl_` ===

**🚀 Mode TURBO — Consommation directe du contexte SANS relire les fichiers sources.**

Ce mode est conçu pour être **ultra-rapide et économe en tokens**. Il exploite le fait que le fichier `_contex.md` (généré par le skill `t_contex` enrichi) est **auto-suffisant** : il contient déjà tous les types, champs, fonctions, imports, code environnant et plan d'implémentation. L'IA n'a donc PAS besoin de relire les fichiers `.kt` sources.

#### Étape 1 : Trouver le dernier contexte
Listez les fichiers dans `app/src/main/java/skill_agent/t_/contexs/` et prenez le **plus récent** (ou le seul s'il n'y en a qu'un).

#### Étape 2 : Sauvegarde rapide (Backup)
Même procédure que CAS B — sauvegarder `.antigravityignore` et `.geminiignore` dans `temp_ignore_backup.txt`.

#### Étape 3 : Isolation MINIMALE
Appliquer les règles d'isolation de la **Section 10** du fichier `_contex.md`, MAIS cette fois on isole uniquement pour pouvoir **écrire** dans le fichier principal. On ne va PAS lire les fichiers relatifs.

#### Étape 4 : Consommer le contexte (LA CLÉ DE L'EFFICACITÉ)

**🚨 RÈGLES STRICTES pour l'IA :**

1. **Lire UNIQUEMENT le fichier `_contex.md`** — c'est votre SEULE source de vérité.
2. **NE PAS lire les fichiers `.kt` relatifs** (M2Client.kt, ActiveDatas.kt, etc.) — toutes les informations nécessaires (champs, types, fonctions) sont DÉJÀ dans le contexte (Sections 3, 4, 6, 7).
3. **Suivre le plan d'implémentation** de la **Section 2** du contexte — il contient les étapes exactes, pas besoin de réfléchir au flow.
4. **Utiliser le tableau des variables** de la **Section 3** pour connaître les types et comment accéder aux données.
5. **Copier/adapter le pattern** de la **Section 8** si présent (pour les TODO `t_copiePattersApp`).
6. **Consulter les contraintes** de la **Section 9** pour éviter les pièges.
7. **Seule exception pour lire un fichier .kt :** Si le contexte ne contient PAS assez d'infos sur un point précis (ex: une signature de fonction manquante), alors ET SEULEMENT ALORS vous pouvez lire le fichier source concerné. Mais dans 95% des cas, le contexte suffit.

**Workflow condensé :**
```
Lire _contex.md → Comprendre §1 (résumé) → Suivre §2 (plan) → Appliquer le fix dans le fichier principal → Restaurer ignores
```

#### Étape 5 : Appliquer le fix
1. Lire **UNIQUEMENT le fichier principal** `.kt` (celui de la Section "Fichier Principal" du contexte).
2. Localiser le TODO exact dans le code.
3. Appliquer le fix en suivant le **Plan d'implémentation (§2)** et en utilisant les **Imports (§6, §7)** du contexte.
4. Supprimer le commentaire TODO une fois résolu.

#### Étape 6 : Restauration + Nettoyage
1. Restaurer `.antigravityignore` et `.geminiignore` depuis la sauvegarde.
2. Supprimer `temp_ignore_backup.txt`.
3. **NE SUPPRIMEZ PAS** le fichier `_contex.md` consommé du dossier `contexs/` SAUF si l'utilisateur le demande explicitement.
4. **CRITIQUE :** Si le TODO résolu mentionnait `t_copiePattersApp`, utilisez `grep_search` pour localiser le TODO équivalent dans le projet source `D:\AndroidStudioProjects\ClientJetPack` et supprimez-le également.

#### Étape 7 : Report Success
Présentez à l'utilisateur :
- ✅ Confirmation que le TODO a été résolu
- 📝 Diff des modifications apportées
- 🔄 Confirmation que les ignores sont restaurés
- 🗑️ Confirmation que le contexte a été consommé/nettoyé
