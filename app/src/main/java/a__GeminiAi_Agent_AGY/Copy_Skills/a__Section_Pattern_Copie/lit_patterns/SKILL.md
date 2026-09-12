---
name: lit_patterns
description: Copier des patterns de codage, codes et composables (inter-projets ou intra-projet), les adapter aux nouveaux paramètres locaux, et nettoyer les TODOs. Supporte la résolution via references.json.
---

# Skill - Copy/Read Coding Patterns (lit_patterns)

Ce skill permet de chercher, copier et adapter des patterns de code, des fonctions ou des composables d'un emplacement à un autre (soit au sein du même projet, soit entre différents projets en résolvant leurs chemins via `references.json`).

## Trigger Phrases
- "lit_patterns"
- "lit_pattern"
- "read_c_p"
- "c_p"
- "t_copie_pattern_coding_et_funcs"
- "t_c"
- "tc"
- "t_copiePattersApp"
- "t_c_client"
- "integre_ici"
- "todo_relative : depuit_ce_pattern>"
- "<interg"

## Steps to Execute

### 1. Recherche des TODOs / Instructions (Mode automatique `t_`)
- Si lancé directement, cherchez les commentaires `TODO` ou instructions dans le projet actif (en ciblant uniquement `app/src/` pour la rapidité).
- Prenez en compte les TODOs formatés spécifiquement, par exemple : `todo : integre_ici todo_relative : depuit_ce_pattern>` ou `//... lit_patterns et fait que <description>`.
- Si le commentaire mentionne un autre projet (ex: "dans light app", "aller vers client"), lisez le fichier de configuration globale [references.json](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/references.json) (déclencheur `ref_json`) pour trouver le chemin absolu du projet cible.
- Identifiez les fichiers source (où se trouve le code à copier) et destination (où le coller).

#### 1.1. Résolution automatique Donneur/Receveur (même projet, même fichier)
Si le TODO ne spécifie pas explicitement le fichier source, appliquer cette heuristique **intra-projet** :
1. Le **fichier qui contient le TODO** est toujours le **Receveur** (destination).
2. Chercher un fichier portant le **même nom** dans d'autres packages/applications du même projet :
   ```powershell
   Get-ChildItem -Recurse -Include "<NomFichier>.kt" "<workspace>\app\src\main" `
     | Where-Object { $_.FullName -ne "<chemin_du_receveur>" } `
     | Select-Object FullName
   ```
3. Parmi les candidats, choisir comme **Donneur** le fichier qui :
   - **Ne contient pas** le TODO (il a déjà l'implémentation souhaitée).
   - Contient le pattern ou la fonctionnalité décrite dans le commentaire (ex: ExoPlayer, video player, etc.).
4. Si plusieurs candidats existent, inspecter rapidement chacun (recherche `-Pattern` ciblée) pour identifier lequel implémente la fonctionnalité désirée.

#### 1.2. Vérification que le Donneur a déjà l'implémentation
Avant de copier, confirmer que le fichier Donneur contient effectivement le pattern cherché (ex: `ExoPlayer`, `AndroidView`, `DisposableEffect`) via un `Select-String` rapide. Si aucun candidat ne l'a, signaler à l'utilisateur et demander de spécifier la source manuellement.

### 2. Copie du Pattern ou du Composant (Inter-projet ou Intra-projet)
- **Conformité stricte :** Vous ne devez **jamais sortir du pattern de codage** spécifié. Si le code source peut être copié directement et adapté ("fit") au nouvel environnement, faites-le sans chercher à réinventer la logique.
- **Cas Intra-projet** : Copiez le code d'un composable/fichier vers un autre au sein du même espace de travail.
- **Cas Inter-projet** : Utilisez PowerShell (`Copy-Item`) pour copier les fichiers ou dossiers sources du projet d'origine vers le projet de destination.
- **Adaptation des imports** : Après copie, vérifier et ajouter les imports manquants dans le Receveur (ex: `ExoPlayer`, `DisposableEffect`, `AndroidView`, `Uri`, `PlayerView`). Supprimer les imports devenus inutiles dans le Receveur (ex: l'ancienne approche remplacée).

### 3. Adaptation des Paramètres et Types (Parameter Fitting)
- Lors de l'écriture ou du collage du code dans le fichier cible, adaptez la signature et le corps pour **matcher exactement les paramètres et types attendus** (ex: liaison de données locales, ViewModels spécifiques ou structures d'états locales).
- Remplacez les injections de dépendances globales (ex: `koinInject`) par des liaisons locales si la destination ne supporte pas Koin.
- Supprimez les imports et dépendances inutilisées (WiFi, bases de données spécifiques non présentes, etc.) pour éviter les erreurs de compilation.
- **Nettoyage après action** : Lors de la copie d'une action de création (par exemple, création de produit à partir d'une recherche), assurez-vous que le champ de recherche de la destination est effacé après le succès de l'action pour éviter les saisies obsolètes (ex : en appelant `onSearchTextChange("")` ou la méthode de mise à jour de valeur active correspondante).
- **Synchronisation Room/Mémoire** : Si le pattern copié modifie des entités persistées (Room), utilisez `@Upsert` ou `upsertData()` au lieu de `@Update` pour supporter à la fois la création et la mise à jour, et veillez à synchroniser les listes mémoire de la destination (ex: `viewModel.active_Datas.list_X`) en remplaçant les éléments existants par leur ID et en concaténant les nouveaux éléments.
- **Adaptation des sources de données (DAO vs UI Data)** : Si le composant de destination (Receiver) met à jour ses données via un DAO, mais que le code d'origine (Donor) utilise une logique de mise à jour d'interface (UI update data), vous devez adapter le code copié pour utiliser le DAO de la destination.

### 4. Verification et Nettoyage
- Une fois l'intégration terminée avec succès, supprimez les commentaires `TODO` ou pointeurs d'instructions dans les deux projets (source et destination).
