---
name: supp_val_et_remplace_par_passed_paramater_depuit_parent_val
description: Supprime une propriété d'un modèle de données (data class / Room entity) et remplace tous ses usages par un paramètre passé depuis le composable parent, ou par un état local `remember { mutableStateOf(...) }` dans le composable concerné. Nettoie toute la chaîne de callbacks associée. Trigger : supp_val_, remplace_par_param_, val_to_param.
---

# Skill — Supprimer une Val et la Remplacer par un Paramètre Passé depuis le Parent (`supp_val_`)

Ce skill instruit l'assistant sur la manière d'**extraire une propriété d'un modèle persisté** (data class Room ou autre) et de la **remplacer** soit par :
- Un **paramètre Composable** passé depuis le parent (`passed parameter`)
- Ou un **état local** `var x by remember { mutableStateOf(...) }` dans le composable le plus bas de la chaîne

## Déclencheurs (Triggers)
- `supp_val_`
- `remplace_par_param_`
- `val_to_param`
- `supp_val_et_remplace_par_passed_paramater_depuit_parent_val`

---

## Quand utiliser ce skill

Ce skill est pertinent quand :
- Une propriété d'un modèle Room est utilisée uniquement comme **état UI éphémère** (toggle/affichage), sans besoin de persistance réelle.
- Le toggle/switch **ne fonctionne pas** car la recomposition est trop lente (latence async Room → Flow).
- Un callback de mise à jour du modèle est propagé inutilement sur plusieurs niveaux de composables.

---

## Détection du mode : Relative TODO `<et passe ce val`

**RÈGLE CRITIQUE** : Avant tout traitement, inspecter les commentaires adjacents au TODO principal.

Si le TODO ou un commentaire adjacent contient l'un des marqueurs suivants :
- `<et passe ce val au supprime de compos`
- `de passe le val ou suprimed`
- `passe ce val` / `passe val`

→ **Ne pas** créer un state local `remember`. À la place, **propager la valeur supprimée comme paramètre passé depuis le parent** (voir Étape 4b).

Si aucun de ces marqueurs n'est présent → stratégie par défaut : state local `remember` (voir Étape 4a).

---

## Étapes d'exécution

### 1. Scanner la propriété cible — avec contexte (économie tokens)

**RÈGLE OBLIGATOIRE** : Toujours utiliser `-Context` sur `Select-String` pour voir les lignes adjacentes sans lire le fichier entier.

```powershell
# Scan initial avec contexte 3 lignes avant + 5 après (détecte les marqueurs relatifs)
Get-ChildItem -Recurse -Include "*.kt","*.java" "<project_root>\app\src\main" `
  | Select-String -Pattern "<nom_propriete>" -Context 3,5 `
  | Format-List
```

Cela retourne pour chaque match :
- `Filename`, `LineNumber`, `Line` (la ligne correspondante)
- `Context.PreContext` (3 lignes avant — détecte les marqueurs relatifs)
- `Context.PostContext` (5 lignes après — voit comment la val est consommée)

> Ne lire le fichier entier avec `view_file` que si le contexte de 8 lignes est insuffisant pour comprendre la structure.

Lister les fichiers concernés :
- **Modèle source** : le data class où la propriété est déclarée
- **Composables feuilles** : ceux qui lisent/togglent la valeur
- **Composables intermédiaires** : ceux qui propagent le callback `on_pour_update_<prop>`
- **Call sites racines** : AppNavHost, MainScreen, etc.

### 1b. Chercher les callers d'un composable — avec `-Context` au lieu de `view_file`

Pour voir comment un composable est appelé sans lire tout son fichier :

```powershell
Get-ChildItem -Recurse -Include "*.kt" "<project_root>\app\src\main" `
  | Select-String -Pattern "<NomDuComposable>" -Context 2,10 `
  | Format-List
```

Cela fournit 2 lignes avant + 10 lignes après chaque appel — suffisant pour voir les arguments passés.

### 2. Décider de la stratégie de remplacement

| Marqueur adjacent dans le TODO | Stratégie |
|---|---|
| `<et passe ce val au supprime de compos` ou `de passe le val ou suprimed` | **Mode propagation** : passer la val comme paramètre depuis le parent (Étape 4b) |
| Aucun marqueur relatif | **Mode local** : `remember { mutableStateOf(defaultVal) }` dans le composable feuille (Étape 4a) |
| La valeur vient d'un ViewModel partagé | Déplacer le state dans le ViewModel (pas ce skill) |

### 3. Supprimer la propriété du modèle

Dans le data class source, supprimer la propriété et nettoyer les commentaires :
```kotlin
// AVANT
val affiche_ProduitDataBaseEdites_ComposableViews: Boolean = true,  //<--
// TODO(1): <et passe ce val au supprime de compos
/<--

// APRES
// (lignes supprimées)
```

> WARNING Room Database : Si le modèle est une Entity Room, incrémenter la version dans `AppDatabase.kt` et documenter le changement.

### 4a. Mode local — Remplacer par `remember` (pas de marqueur relatif)

Dans le composable **feuille**, remplacer la lecture depuis le modèle par un state local :

```kotlin
// AVANT
val activeCompt = viewModel.active_Datas.active_M9Compt
val showX = activeCompt?.ma_prop ?: false
// onClick { on_pour_update_ma_prop(!compt.ma_prop) }
// color = if (showX) ...

// APRES
val showX = remember { mutableStateOf(false) }
// onClick { showX.value = !showX.value }
// color = if (showX.value) ...
```

Puis supprimer tous les callbacks `on_pour_update_<prop>` dans la chaîne (voir Étape 5).

### 4b. Mode propagation — Passer la val depuis le parent (marqueur `<et passe ce val`)

Quand le marqueur relatif est détecté, la valeur supprimée du modèle doit être **fournie par le composable parent** comme paramètre.

**Dans le composable feuille** — ajouter le paramètre en entrée :
```kotlin
// AVANT (lisait depuis le modèle)
fun MonComposable(appDatabase: AppDatabase) {
    val showX = activeCompt?.ma_prop ?: false
    // ...
}

// APRES (reçoit la val du parent)
fun MonComposable(
    appDatabase: AppDatabase,
    ma_prop: Boolean = false,                    // val passée depuis le parent
    onChangeMaProp: (Boolean) -> Unit = {},      // callback vers le parent
) {
    // showX = ma_prop directement
    // onClick { onChangeMaProp(!ma_prop) }
}
```

**Dans le composable parent** — déclarer le state et le passer :
```kotlin
// AVANT (passait un callback Room)
MonComposableParent(
    on_pour_update_ma_prop = { isChecked ->
        setter.update_M9AppCompt(compt.copy(ma_prop = isChecked))
    }
)

// APRES (gère le state localement et passe la val)
var ma_prop by remember { mutableStateOf(false) }

MonComposable(
    ma_prop = ma_prop,
    onChangeMaProp = { newVal -> ma_prop = newVal }
)
```

### 5. Nettoyer la chaîne de callbacks intermédiaires

Pour chaque composable intermédiaire (entre le parent qui hold le state et le composable feuille), supprimer :
- Le **paramètre de signature** `on_pour_update_<prop>: (Boolean) -> Unit = {}`
- Le **passage de ce paramètre** dans l'appel du composable enfant

```powershell
# Vérifier les intermédiaires rapidement avec contexte
Get-ChildItem -Recurse -Include "*.kt" "<project_root>\app\src\main" `
  | Select-String -Pattern "on_pour_update_<prop>" -Context 1,3 `
  | Format-List
```

### 6. Nettoyer le call site racine (AppNavHost / MainScreen)

Supprimer le bloc qui faisait l'upsert Room depuis le callback :
```kotlin
// AVANT
MonComposable(
    on_pour_update_<prop> = { isChecked ->
        setter.update_M9AppCompt(compt.copy(<prop> = isChecked))
    }
)

// APRES
MonComposable(
    // callback supprimé (ou remplacé par le remember state si mode propagation)
)
```

### 7. Vérification finale — avec contexte ciblé

```powershell
# Vérification ciblée — pas de lecture de fichier entier
Get-ChildItem -Recurse -Include "*.kt" "<project_root>\app\src\main" `
  | Select-String -Pattern "<nom_propriete>|on_pour_update_<prop>" -Context 1,2 `
  | Format-List
```

> Note : Des références dans d'autres applications (App4, etc.) utilisant le même nom en contexte local distinct ne sont **pas** à modifier.

### 8. Rapport final

Afficher uniquement la liste des fichiers modifiés avec liens cliquables. Pas de résumé de code.

---

## Règles importantes

- **Ne pas modifier les Entity Room** sans incrémenter la version de la BDD (règle projet).
- **Ne pas toucher** les fichiers d'autres applications (App4, etc.) si le paramètre y est utilisé localement avec un sens différent.
- **Ne pas compiler** après les modifications (règle `t_`).
- **Nettoyer aussi** les commentaires `//TODO`, `//<--`, `/<--` associés.
- **`grep_search` est indisponible** sur cette machine — toujours utiliser `Select-String -Context` de PowerShell.
