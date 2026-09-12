---
name: t_contex_files_relative_pour_regle_todo
description: Extracts the context of a TODO comment in the code and generates a concise, action-focused Markdown context file named after the TODO slug.
---

# Skill - TODO Context Exporter

Ce skill génère un fichier Markdown **concis et orienté fix** (`contex.md`) nommé d'après le TODO ciblé. Le fichier contient uniquement ce qu'il faut pour résoudre le TODO directement, sans sections superflues.

**RÈGLE D'OR STRICTE (CRITICAL RULE) :** Ce skill est STRICTEMENT de la lecture et de la compréhension (Read-Only). L'assistant ne doit faire **AUCUNE MODIFICATION** ni "edit" dans les fichiers du projet. Le `TODO` et le code existant doivent rester parfaitement intacts.

---

## Trigger Phrases
- "t_contex_files_relative_pour_regle_todo"
- "t_contex"
- "todo_context"
- "save_c"

---

## Steps to Execute

### 1. Locate the TODO
Identifiez le ou les commentaires `TODO` pertinents dans le code source (via `grep_search`). S'il y en a plusieurs, demandez à l'utilisateur lequel cibler ou traitez le plus évident.

### 2. Compute the File Name (Slug du TODO)

À partir du texte exact du TODO :
1. Supprimer le préfixe `TODO:` ou `TODO` s'il existe.
2. Mettre en minuscules.
3. Remplacer les espaces et caractères spéciaux par `_`.
4. Tronquer à 50 caractères maximum.
5. Ajouter le suffixe `_contex.md`.

**Exemple :** `TODO: ajoute le bouton de suppression` → `ajoute_le_bouton_de_suppression_contex.md`

Chemin du fichier final :
```
C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\t_\contexs\<todo_slug>_contex.md
```

### 3. Deep-Read — Comprendre le contexte avant d'écrire

Pour le `TODO` ciblé, l'assistant doit :

1. **Lire intégralement le fichier principal** contenant le TODO (la fonction/composable ENTIÈRE, pas juste 15 lignes).
2. **Identifier les fichiers relatifs** (Models, ViewModels, ActiveDatas, composables parents) impliqués dans le flux du TODO.
3. **Lire chaque fichier relatif** pour extraire les champs, fonctions et types pertinents.
4. **CRITIQUE — Si le TODO contient `t_copiePattersApp`** : chercher le pattern original dans `D:\AndroidStudioProjects\ClientJetPack` via `grep_search` et l'inclure dans le plan de fix.
5. **Ne pas appliquer de correctif. Ne pas supprimer le TODO.**

### 4. Generate the Context File (Format CONCIS)

Créez le dossier `contexs/` s'il n'existe pas, puis créez (ou écrasez) le fichier :

```md
# Fix TODO : [texte exact du TODO]

**Fichier :** `[chemin absolu du fichier principal]`
**Composable :** `[nom du composable parent]`

---

## Plan de fix (étapes concrètes)
1. [Étape 1 avec noms de variables/fonctions/types exacts]
2. [Étape 2...]
3. ...

---

## Scope — Variables disponibles au point du TODO
| Variable | Type | Description |
|---|---|---|
| `[var]` | `[Type]` | [description courte] |

---

## Fichiers relatifs
### [NomFichier.kt] — `[chemin absolu]`
```kotlin
// champs et fonctions pertinents seulement
```

---

## Code environnant (30+ lignes autour du TODO)
```kotlin
// [30+ lignes avant]
// ---> TODO: [texte exact] <---
// [30+ lignes après]
```

---

## Imports à ajouter
```kotlin
import ...
```

---

## Règles d'isolation
```text
*
!*/
!*skill_agent/**
!*[FichierPrincipal.kt]
!*[FichierRelatif1.kt]
!*[FichierRelatif2.kt]
```
```

### 5. Report Success

Une fois le fichier généré, informez l'utilisateur avec un message de succès et un lien cliquable :
[<todo_slug>_contex.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/contexs/<todo_slug>_contex.md)

**Affichez le nombre total de lignes du fichier généré.**

L'utilisateur pourra ensuite lancer `read_ingor_last` dans une nouvelle session pour régler le TODO.
