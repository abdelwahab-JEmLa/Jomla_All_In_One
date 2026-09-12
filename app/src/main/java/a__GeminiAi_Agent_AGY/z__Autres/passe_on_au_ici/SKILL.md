---
name: passe_on_au_ici
description: Resolves TODO comments tagged with <passe_on todo.relative or <a_ici to automatically wire an on_ callback (lambda) from a child composable up through the Jetpack Compose hierarchy to the parent that owns/handles it. Triggered by <passe_on todo.relative, passe_on_au_ici, on_a_ici_, passe_on_ici.
---

# Skill — passe_on_au_ici (`<passe_on todo.relative`)

Ce skill résout les TODOs taggués `<passe_on todo.relative` ou `<a_ici` **pour les callbacks `on_`**.  
Son rôle : **câbler automatiquement** un callback lambda (`on_<Param>: () -> Unit` ou `on_<Param>: (Type) -> Unit`) depuis le composable **enfant** (déclencheur) jusqu'au composable **parent** (gestionnaire), en traversant toute la chaîne intermédiaire.

> **Différence clé avec `passe_paramater_a_ici`** :
> - `passe_paramater_a_ici` → passe un **état/valeur** vers le **bas** (parent → enfant)
> - `passe_on_au_ici` → passe un **callback `on_`** vers le **haut** (enfant → parent via lambda)

---

## Trigger Phrases
- `<passe_on todo.relative`
- `passe_on_au_ici`
- `on_a_ici_`
- `passe_on_ici`
- `<a_ici on_`

---

## Format du TODO ciblé

```kotlin
//TODO <passe_on todo.relative <a_ici on_<NomAction> depuis <NomEnfant> vers <NomParent>
//TODO <passe_on todo.relative <a_ici on_<NomAction>: (<Type>) -> Unit de <NomEnfant> a <NomParent>
```

Les infos clés extraites :
- **`<NomAction>`** : le nom du callback sans le préfixe `on_` (ex: `valide`, `delete`, `itemSelected`)
- **`<Type>`** : le type de la valeur émise par le callback (ex: `Unit`, `String`, `Int`, `M3CouleurProduitInfos`)
- **`<NomEnfant>`** : le composable qui **déclenche** le callback (ex: `BoutonValider`, `ColorCard`)
- **`<NomParent>`** : le composable qui **gère** le callback (ex: `MyScreen`, `ParentDialog`)

---

## Étapes d'exécution

### Étape 0 — Localiser et parser les TODOs

```powershell
$root = "<WorkspaceRoot>\app\src\main"
Get-ChildItem -Recurse -Include "*.kt" $root `
  | Select-String -Pattern "TODO.*(<passe_on|on_a_ici)" `
  | Select-Object Filename, LineNumber, Line | Format-Table -AutoSize -Wrap
```

Pour chaque TODO trouvé, extraire :
- `on_<NomAction>` → nom complet du callback
- `<Type>` → type de la valeur propagée (`Unit` si aucune valeur)
- `<NomEnfant>` → composable déclencheur (ajout du callback dans la signature)
- `<NomParent>` → composable gestionnaire (implémentation du lambda)

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

### Étape 3 — Batch edit (un `multi_replace_file_content` par fichier)

**Dans le fichier enfant (`<NomEnfant>`) et les INTERMÉDIAIRES** — ajouter le callback reçu :
```kotlin
// Avant
fun <NomEnfant>(
    // ...autres params
) {

// Après
fun <NomEnfant>(
    // ...autres params
    on_<NomCallback>: (<Type>) -> Unit = {},
) {
    // Dans les intermédiaires, relayer au site d'appel suivant :
    <NomSuivant>(
        on_<NomCallback> = on_<NomCallback>
    )
    // ...
    // Appel au moment du déclenchement (ex: clic, sélection)
    Button(onClick = { on_<NomAction>(<valeur>) }) { ... }
}
```

**Modèle de Synchronisation d'État (State Sync Pattern)** :
Si le callback met à jour un état qui doit également être reflété localement dans le composable enfant :
1. Déclarer le state local dans l'enfant en utilisant `remember(parentParam)` pour qu'il se synchronise automatiquement si le parent change la valeur de l'extérieur :
   `val localState = remember(parentParam) { mutableStateOf(parentParam) }`
2. Lors de la modification locale, mettre à jour la valeur et notifier immédiatement le parent via le callback :
   ```kotlin
   localState.value = newValue
   on_pour_update_parentParam(localState.value)
   ```

**Aux niveaux intermédiaires** — forwarder le callback :
```kotlin
// Signature — ajouter le forward
fun <NomInter>(
    // ...
    on_<NomAction>: (<Type>) -> Unit = {},
) {
    // Site d'appel enfant — forwarder
    <NomEnfant>(
        // ...
        on_<NomAction> = on_<NomAction>,
    )
}
```

**Dans le fichier parent (`<NomParent>`)** — implémenter le lambda :
```kotlin
// Signature — ajouter le paramètre si <NomParent> reçoit lui-même ce callback d'un niveau encore supérieur
// OU implémenter directement le lambda au site d'appel de <NomEnfant>/<NomInter>
<NomEnfant_ou_Inter>(
    // ...
    on_<NomAction> = { valeur ->
        // Logique du parent : mise à jour du state, navigation, etc.
        my_<NomAction>_state = valeur
    },
)
```

---

### Étape 4 — Nettoyage des TODOs

Supprimer dans tous les fichiers modifiés :
- `//TODO.*<passe_on.*`
- `//TODO.*on_a_ici.*`
- Les `//` adjacents liés à ces TODOs

### Étape 5 — Gestion des Appelants Multiples & Importations

1. **Appelants Multiples** : Si le composable enfant possède plusieurs appelants (ex: utilisé dans différents écrans ou sections) :
   - Cartographier chaque branche d'appel indépendante.
   - Propager le callback `on_<NomAction>` dans chaque branche.
   - Utiliser des signatures avec paramètres optionnels (ex: `on_<NomAction>: (<Type>) -> Unit = {}`) pour ne pas casser la compilation des branches n'ayant pas besoin de réagir immédiatement à l'action.
2. **Importations** : S'assurer d'importer les classes de modèles nécessaires dans les fichiers modifiés et nettoyer les importations inutilisées.

---

## Convention de nommage

| Rôle | Format |
|------|--------|
| Callback dans l'enfant (déclencheur) | `on_<NomAction>: (<Type>) -> Unit = {}` |
| Site d'appel du callback dans l'enfant | `on_<NomAction>(<valeur>)` ou `on_<NomAction>()` si `Unit` |
| Forwarding intermédiaire (signature) | `on_<NomAction>: (<Type>) -> Unit = {},` |
| Forwarding intermédiaire (site d'appel) | `on_<NomAction> = on_<NomAction>,` |
| Implémentation dans le parent | `on_<NomAction> = { valeur -> /* logique */ },` |

---

## Règles d'économie token (OBLIGATOIRES)

| Situation | Action |
|-----------|--------|
| Cartographier enfant + parent + intermédiaires | 1 bloc PowerShell avec les 2 recherches parallèles |
| Lire une signature | `Select-String -Context 0,15` uniquement |
| Localiser un site d'appel | `Select-String -Context 2,8` uniquement |
| Modifier signature + site d'appel | `multi_replace_file_content` avec N chunks (1 appel par fichier) |

**INTERDIT** : `view_file` entier sur un fichier > 150 lignes.  
**INTERDIT** : Appels `view_file` séparés pour chaque fichier de la chaîne.  
**INTERDIT** : Recherches séquentielles au lieu du bloc parallèle unique.

---

## Tableau comparatif des skills de propagation

| Critère | `passe_paramater_a_ici` | `passe_on_au_ici` | `passe_recieve_val` |
|---------|-------------------------|-------------------|---------------------|
| Direction | Descendant (parent → enfant) | Montant (enfant → parent) | Bidirectionnel |
| Type | State/valeur | Callback `on_` lambda | State + callback |
| Déclencheur TODO | `<a_ici` / `<passe todo.relative` | `<passe_on todo.relative` | `passe>recieve>val` |
| Nommage | Nom direct du param | `on_<NomAction>` | `on_` + `recieve_` |
| Complexité | Simple | Moyenne | Complexe |
