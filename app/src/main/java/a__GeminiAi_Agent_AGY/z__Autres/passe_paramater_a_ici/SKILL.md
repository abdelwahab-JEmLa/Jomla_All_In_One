---
name: passe_paramater_a_ici
description: Resolves TODO comments tagged with <passe todo.relative or <a_ici to automatically propagate a state/value/callback down a Jetpack Compose hierarchy from a parent composable to a child composable. Triggered by <a_ici, passe_a_ici, <passe todo.relative, a_ici_, passe_param_ici.
---

# Skill — passe_paramater_a_ici (`<a_ici`)

Ce skill résout les TODOs taggués `<passe todo.relative` ou `<a_ici` dans le code Compose.  
Son rôle : **propager automatiquement** un paramètre/état depuis un composable **parent** vers un composable **enfant** en suivant la hiérarchie réelle de l'appel.

---

## Trigger Phrases
- `<a_ici`
- `a_ici_`
- `passe_a_ici`
- `passe_param_ici`
- `<passe todo.relative`
- `passe_paramater_a_ici`

---

## Format du TODO ciblé

Ce skill traite les TODOs suivants dans le code :

```kotlin
//TODO <passe todo.relative <a_ici <NomParam> depuis <NomParent> vers <NomEnfant>
//TODO <a_ici passe <NomParam> de <NomParent> a <NomEnfant>
```

Les infos clés extraites :
- **`<NomParam>`** : le nom du paramètre/état à propager (ex: `isExpanded`, `selectedColor`, `onValide`)
- **`<NomParent>`** : le composable propriétaire / émetteur (ex: `MyScreen`, `ParentCard`)
- **`<NomEnfant>`** : le composable récepteur / consommateur (ex: `ChildButton`, `ColorPicker`)

---

## Étapes d'exécution

### Étape 0 — Localiser et parser les TODOs

1. Lancer une recherche PowerShell pour trouver tous les TODOs contenant `<a_ici` ou `passe todo.relative` :

```powershell
$root = "<WorkspaceRoot>\app\src\main"
Get-ChildItem -Recurse -Include "*.kt" $root `
  | Select-String -Pattern "TODO.*(<a_ici|passe todo\.relative)" `
  | Select-Object Filename, LineNumber, Line | Format-Table -AutoSize -Wrap
```

2. Pour chaque TODO trouvé, extraire :
   - `<NomParam>` (le paramètre/état à passer)
   - `<NomParent>` (le composable qui possède/émet)
   - `<NomEnfant>` (le composable qui reçoit)

---

### Étape 1 — Cartographier la chaîne complète (Moteur ascendant)

Au lieu de supposer un lien direct, utilisez un **moteur de recherche ascendant** pour remonter la chaîne de l'enfant jusqu'au parent.

```powershell
$root = "<WorkspaceRoot>\app\src\main"

# 1. Trouver les appelants de l'Enfant
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "\b<NomEnfant>\s*\(" -Context 0,2

# 2. Répéter la recherche pour le composant intermédiaire trouvé, jusqu'à atteindre <NomParent>
```

---

### Étape 2 — Lire les signatures ciblées de toute la chaîne

⚠️ **NE PAS** utiliser `^fun` stricte pour la recherche (sujet aux erreurs d'espaces).

```powershell
# Lire la signature (Parent, Enfant, et tous les intermédiaires)
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "fun\s+<Nom>" -Context 0,20

# Localiser les sites d'appel
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "<NomEnfantOuInter>\s*\(" -Context 2,8
```

---

### Étape 3 — Batch edit (un appel multi_replace par fichier)

**Dans le fichier enfant (`<NomEnfant>`) et les INTERMÉDIAIRES** — ajouter le paramètre reçu :
```kotlin
// Avant
fun <NomEnfant>(
    // ...autres params
) {

// Après
fun <NomEnfant>(
    // ...autres params
    <NomParam>: <Type> = <DefaultValue>,
    on_pour_update_<NomParam>: (<Type>) -> Unit = {} // (Si nécessaire)
) {
    // Dans les intermédiaires, relayer au site d'appel suivant :
    <NomSuivant>(
        <NomParam> = <NomParam>,
        on_pour_update_<NomParam> = on_pour_update_<NomParam>
    )
```

**Dans le fichier parent (`<NomParent>`)** — deux modifications dans un même appel :
```kotlin
// 1. Ajouter la variable/state si elle n'existe pas encore
var <NomParam> by remember { mutableStateOf(<DefaultValue>) }

// 2. Passer la valeur au site d'appel de l'enfant
<NomEnfant>(
    // ...
    <NomParam> = <NomParam>,
)
```

---

### Étape 4 — Nettoyage des TODOs

Supprimer dans tous les fichiers modifiés :
- `//TODO.*<a_ici.*`
- `//TODO.*passe todo\.relative.*`
- Les `//` adjacents liés à ces TODOs

### Étape 5 — Gestion des Appelants Multiples & Importations

1. **Appelants Multiples** : Si le composable enfant possède plusieurs appelants (ex: utilisé dans différents écrans ou sections) :
   - Cartographier chaque branche d'appel indépendante.
   - Propager le paramètre `<NomParam>` (et le callback `on_pour_update_<NomParam>` si nécessaire) dans chaque branche.
   - Définir des valeurs par défaut saines (ex: `<NomParam>: <Type> = <DefaultValue>`) pour ne pas casser la compilation des branches n'utilisant pas ou n'ayant pas besoin de personnaliser ce paramètre immédiatement.
2. **Importations** : S'assurer d'importer les classes de modèles nécessaires dans les fichiers modifiés et nettoyer les importations inutilisées.

---

## Convention de nommage

| Rôle | Format |
|------|--------|
| Paramètre passé vers le bas | `<NomParam>: Type = defaultValue` |
| Callback remontant vers le parent | `on_<NomParam>: (Type) -> Unit = {}` |
| Site d'appel dans le parent | `<NomParam> = <NomParam>,` |
| State owner dans le parent | `var <NomParam> by remember { mutableStateOf(defaultValue) }` |

---

## Règles d'économie token (OBLIGATOIRES)

| Situation | Action |
|-----------|--------|
| Cartographier parent + enfant | 1 bloc PowerShell avec les 2 recherches parallèles |
| Lire une signature | `Select-String -Context 0,20` uniquement |
| Localiser un site d'appel | `Select-String -Context 2,8` uniquement |
| Modifier signature + appel | `multi_replace_file_content` avec N chunks (1 appel) |

**INTERDIT** : `view_file` entier sur un fichier > 150 lignes pour trouver une signature.  
**INTERDIT** : Appels `view_file` séparés pour chaque fichier.  
**INTERDIT** : Lancer les recherches parent et enfant dans deux commandes séquentielles séparées.

---

## Différence avec `passe_recieve_val`

| Critère | `passe_recieve_val` | `passe_paramater_a_ici` |
|---------|---------------------|-------------------------|
| Direction | Bidirectionnel (montée + descente) | Descendant uniquement (parent → enfant) |
| Déclencheur | TODO `passe>recieve>val` | TODO `<a_ici` / `<passe todo.relative` |
| Complexité | Chaîne de callbacks complexe | Passage simple de paramètre |
| Convention nom | `on_`, `recieve_` | Nom direct du paramètre |
