---
name: affiche_parent_val
description: Trouve automatiquement le composable parent propriétaire (owner) d'une valeur (qui contient le var X by remember { mutableStateOf }) ou le gestionnaire d'un callback (qui définit on_Action = { ... }) en remontant la chaîne d'appels, et renvoie un lien cliquable vers ce parent. Déclenché par affiche_parent_val.
---

# Skill — Trouver le Parent Propriétaire (`affiche_parent_val`)

Ce skill a pour but d'aider l'utilisateur à localiser rapidement le composable "owner" :
1. D'une **valeur d'état** (celui qui la détient via `remember { mutableStateOf }`).
2. D'un **callback `on_`** (celui qui définit sa logique métier, ex: `on_Action = { ... }`, sans se contenter de le relayer).
Il ne modifie aucun fichier, il fournit uniquement un lien Markdown cliquable.

## Trigger Phrases
- `<lien parent`
- `lien_ver_paren_val`
- `affiche_parent_val`
- `<affiche_parent_val`

---

## Étapes d'exécution

### 1. Identifier la cible
À partir du commentaire TODO (ex: `//TODO <affiche_parent_val [NomParam]`), identifier :
- **`<NomParam>`** : Le nom de la variable ou du paramètre dont on cherche le propriétaire.
- **`<Enfant>`** : Le composable où se trouve actuellement le TODO.

### 2. Moteur de Recherche Ascendant (Upward Chain Resolver)
Utiliser l'outil **`grep_search`** (très économe en tokens et extrêmement rapide) pour localiser le propriétaire.

**Logique de ciblage :**
- Si `<NomParam>` commence par `on_` (ex: `on_Valider`) : On cherche le composable parent qui assigne une logique au callback (ex: `on_Valider = { ... }`) au lieu de simplement le relayer (`on_Valider = on_Valider`).
- Si `<NomParam>` est une valeur d'état (ex: `isExpanded`) : On cherche le composable parent qui déclare `var <NomParam>.*mutableStateOf`.

**Lancer la recherche :**
1. **Chercher directement la déclaration (utile pour les variables) :**
   Utilisez l'outil `grep_search` avec le `Query` : `var\s+<NomParam>.*mutableStateOf` (mettez `IsRegex` à `true`) dans `app\src\main`.

2. **Si c'est un callback (on_...) ou si non trouvé : utiliser le moteur ascendant :**
   Utilisez l'outil `grep_search` avec le `Query` : `\b<Enfant>\s*\(` (mettez `IsRegex` à `true`, `MatchPerLine` à `true`) pour trouver l'appelant.
   -> Lisez le contexte avec `view_file` pour analyser le parent trouvé (voir s'il contient `on_<NomParam> = {` ou s'il le relaie). S'il le relaie, chercher le parent de ce parent.

### 3. Fournir les informations du parent propriétaire
Une fois le fichier propriétaire trouvé (celui qui détient la déclaration `remember` ou gère directement le callback), l'assistant doit fournir ses informations dans le format suivant, sans lister les fichiers intermédiaires non concernés pour optimiser la rapidité :

**Format attendu :**
Filename   : <NomDuFichier.kt>
Path       : <Chemin_Absolu_Vers_Le_Fichier>
LineNumber : <Numero_De_Ligne>
Line       : <Contenu_De_La_Ligne>
