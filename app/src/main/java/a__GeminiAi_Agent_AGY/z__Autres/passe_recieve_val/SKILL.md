---
name: passe_recieve_val
description: Propagates a state/callback down a Jetpack Compose hierarchy with minimum token cost. Uses two parallel PowerShell searches (passe> side + recieve> side) to auto-locate the owner level, then applies batch edits in one pass.
---

# Skill — passe_recieve_val (Moteur Parallèle)

Ce skill propage un paramètre (`on_pour_...` callback + `var X` mutable) d'un composable **source** (déclencheur) à un composable **destination** (consommateur).

Le moteur fonctionne en **deux recherches parallèles** pour localiser automatiquement le niveau où la `var` doit vivre, sans lire manuellement chaque fichier.

## Trigger Phrases
- `passe_recieve_val`
- `passe>recieve>val`
- `pass_receive_val`

---

## Convention de Nommage

| Rôle | Format |
|------|--------|
| Callback (passe>) | `on_<param>: (Type) -> Unit = {}` (transmet la valeur/mise à jour du type `Type` au parent) |
| État owner (recieve>var) | `var <param> by remember { mutableStateOf(defaultValue) }` (la source de vérité, ex: Boolean, String, Data class...) |
| Paramètre reçu (recieve>) | `recieve_<param>: Type = defaultValue` (reçoit le type `Type` ou nullable `Type? = null` du parent) |
| Site d'appel descendant | `recieve_<param> = <param>,` (ou `recieve_<param> = recieve_<param>,`) |

---

## Étapes d'exécution

### Étape 0 — Extraire les 3 infos clés du TODO
- **`<param>`** : nom du paramètre (ex: `affiche_buttons_lien_unite_couleur_au_couleut_parent`)
- **`<source>`** : composable qui déclenche (ex: `FabButton_newProto`)
- **`<dest>`** : composable qui consomme (ex: `FastInit_Outlined_Int_Edite_Modulable_Proto4`)

Dans le cadre des **relative TODOs** (ex: un TODO `passe>` lié à un TODO `recieve>`), on applique cette convention pour identifier et connecter les flux montants (`on_`) et descendants (`recieve_`).
- **Cas `passe> on_` avec `update ca` relatif** : Si le 1er TODO contient `passe> on_` (source) et le TODO relatif (parent) contient `update ca`, l'objectif est de remonter le callback `on_pour_update_<param>` de la source vers le parent jusqu'à l'endroit où la valeur (`var <param>`) est réellement stockée et mise à jour.

---

### Étape 1 — MOTEUR : Deux recherches parallèles

Lancer les **deux commandes en même temps** (un seul bloc PowerShell) :

```powershell
$root = "<root>\app\src\main"

# --- MOTEUR ASCENDANT Côté PASSE> (déclencheur)
Write-Host "=== PASSE> : callers de <source> ===" -ForegroundColor Cyan
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "\b<source>\s*\(" -Context 0,2
# -> Répéter pour chaque composant intermédiaire trouvé jusqu'à atteindre un parent commun

# --- MOTEUR ASCENDANT Côté RECIEVE>VAR (consommateur)
Write-Host "=== RECIEVE>VAR : callers de <dest> ===" -ForegroundColor Yellow
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "\b<dest>\s*\(" -Context 0,2
# -> Répéter pour chaque composant intermédiaire trouvé jusqu'à atteindre un parent commun
```

**Interpréter les résultats** :

- **PASSE> résultat** → liste des parents qui appellent `<source>`.  
  Le premier parent dans la chaîne reçoit `on_<param>` et le câble au déclencheur.

- **RECIEVE>VAR résultat** → liste des parents qui appellent `<dest>`.  
  Le composable **commun le plus haut** dans les deux listes = **le propriétaire de la `var`** (celui qui crée `var <param> by remember { mutableStateOf(false) }`).

> **Règle du propriétaire** : La `var` mutable vit au niveau le plus haut qui est **commun aux deux branches** (passe> et recieve>). En pratique c'est souvent le Screen/Scaffold.

---

### Étape 2 — Lire uniquement les signatures ciblées

Pour chaque fichier de la chaîne identifiée (Parent owner, enfants, et **tous les intermédiaires**) :

⚠️ **NE PAS** utiliser `^fun` stricte pour la recherche (sujet aux erreurs d'espaces).

```powershell
# Lire la signature (Parent, Enfant, Intermédiaires)
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "fun\s+<NomComposable>" -Context 0,20

# Localiser le site d'appel vers l'enfant
Get-ChildItem -Recurse -Include "*.kt" $root | Select-String -Pattern "<NomEnfantOuInter>\s*\(" -Context 2,8
```

---

### Étape 3 — Batch edit (un appel par fichier, N chunks)

**Au niveau owner (propriétaire de la var) :**
```kotlin
// Créer la var (valeur initiale adaptée : false, null, "", etc.)
var <param> by remember { mutableStateOf(defaultValue) }

// Câbler dans le composable source (via on_<param>)
on_<param> = { newVal -> <param> = newVal },

// Passer au composable destination (via param direct)
recieve_<param> = <param>,
```

**Aux niveaux intermédiaires (passe> et recieve> simultanément) :**
```kotlin
// Signature : ajouter le type générique (Boolean, String, Type? = null...)
recieve_<param>: Type = defaultValue,

// Site d'appel enfant : forwarder
recieve_<param> = recieve_<param>,
```

**Au composable source (déclencheur) :**
```kotlin
// Ajouter le callback dans la signature (Type correspond à la valeur propagée)
on_<param>: (Type) -> Unit = {},

// Appeler depuis l'interaction (ex: clic, sélection, callback)
onCheckedChange = { on_<param>(it) }
```

**Au composable destination (consommateur) :**
```kotlin
// Signature : ajouter avec la valeur par défaut adéquate
recieve_<param>: Type = defaultValue,

// Utiliser la valeur reçue
if (recieve_<param> != null) { /* logique selon le type */ }
```

---

### Étape 4 — Nettoyage
Supprimer dans tous les fichiers modifiés :
- `//TODO(...passe>recieve>val...)`
- `//TODO(...recieve>val ici...)`
- `//TODO(...ici tuva passe...)`
- `//<--` adjacents à ces TODOs

### Étape 5 — Gestion des Appelants Multiples & Importations

1. **Appelants Multiples** : Si le composable source ou destination possède plusieurs appelants (ex: utilisé dans différents écrans ou sections) :
   - Cartographier chaque branche d'appel indépendante.
   - Propager le paramètre ou le callback à travers toutes les branches concernées.
   - Utiliser des signatures avec valeurs par défaut (ex: `recieve_<param>: Type = defaultValue` ou `on_<param>: (Type) -> Unit = {}`) pour ne pas casser la compilation des branches n'ayant pas besoin de réagir ou de passer ces valeurs immédiatement.
2. **Importations** : S'assurer d'importer les classes de modèles nécessaires dans les fichiers modifiés et nettoyer les importations inutilisées.

---

## Règles d'économie token (OBLIGATOIRES)

| Situation | Action |
|-----------|--------|
| Cartographier toute la chaîne | 1 bloc PowerShell avec les 2 recherches parallèles |
| Lire une signature | `Select-String -Context 0,15` uniquement |
| Localiser un site d'appel | `Select-String -Context 2,8` uniquement |
| Modifier signature + appel | `multi_replace_file_content` avec N chunks (1 appel) |

**INTERDIT** : `view_file` entier sur un fichier > 150 lignes pour trouver une signature.
**INTERDIT** : Appels `view_file` séparés pour chaque fichier de la chaîne.
**INTERDIT** : Lancer les deux recherches (passe> et recieve>) dans deux commandes séquentielles séparées.
