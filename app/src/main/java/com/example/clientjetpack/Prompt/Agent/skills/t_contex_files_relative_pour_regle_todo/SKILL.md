---
name: t_contex_files_relative_pour_regle_todo
description: Extracts the context of a TODO comment in the code and generates a rich, self-sufficient Markdown context file.
---

# Skill - TODO Context Exporter

Ce skill instruit l'assistant sur la façon d'extraire automatiquement le contexte d'un commentaire `TODO` dans le code et de générer un fichier Markdown **riche et auto-suffisant** (`contex.md`). Ce fichier DOIT contenir **tout ce qu'une IA "vierge" (nouvelle session/conversation) a besoin** pour résoudre le TODO sans avoir à re-lire les fichiers sources. L'IA doit pouvoir agir immédiatement après avoir lu UNIQUEMENT ce fichier de contexte.

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

### 2. Deep-Read — Comprendre TOUT le contexte avant d'écrire

**🚨 CETTE ÉTAPE EST LA PLUS IMPORTANTE. Ne bâclez pas. Lisez TOUT en profondeur.**

Pour le `TODO` ciblé, l'assistant doit :

1. **Lire intégralement le fichier principal** contenant le TODO (pas juste 15 lignes autour — la fonction/composable ENTIÈRE).
2. **Identifier TOUS les fichiers relatifs** (Models, ViewModels, Interfaces, composables parents, ActiveDatas, etc.) impliqués dans le flux d'exécution du TODO. **Ne ratez AUCUN fichier dépendant** car s'il n'est pas listé, il sera ignoré et masqué par `read_ingor_` !
3. **Lire chaque fichier relatif** pour en extraire :
   - Les **champs/propriétés** pertinents avec leur type exact
   - Les **fonctions/méthodes** utiles avec leur signature complète et leur corps
   - Les **imports** qui seront nécessaires pour le fix
4. **Comprendre le flux** : comment les données circulent du ViewModel → ActiveDatas → Composable → UI.
5. **CRITIQUE — Si le TODO contient `t_copiePattersApp`** : Vous DEVEZ obligatoirement chercher le pattern original dans le projet source `ClientJetPack` (chemin : `D:\AndroidStudioProjects\ClientJetPack`). Utilisez `grep_search` dans ce répertoire pour trouver les composants et fichiers équivalents. Vous devez extraire et inclure le code source original **avec des commentaires explicatifs ligne par ligne**.
6. **Rappel :** N'appliquez AUCUN correctif. NE SUPPRIMEZ PAS le TODO. Contentez-vous d'analyser.

### 3. Generate the Context File (Format ENRICHI)

Créez le dossier `contexs/` s'il n'existe pas, puis créez (ou écrasez s'il existe) le fichier de contexte à :
`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\t_\contexs\<conversation-id>_contex.md`

**⚠️ Le fichier DOIT être assez descriptif pour qu'une IA vierge puisse résoudre le TODO SANS relire les fichiers sources.** Chaque section ci-dessous est OBLIGATOIRE et doit être remplie de manière exhaustive.

```md
# Contexte d'Isolation du TODO

**Fichier Principal :** `[Chemin absolu complet]`
**Cible :** `[Le texte EXACT du TODO, copié tel quel]`
**Projet :** Kotlin / Jetpack Compose / Android
**Composable parent :** `[Nom de la fonction @Composable qui contient le TODO]`

---

## 1. Résumé exécutif (pour IA vierge)
[2-4 phrases qui résument en langage clair :
- Quel est le composable/écran concerné et à quoi il sert
- Quel est le TODO et ce qu'il demande de faire concrètement
- Quel est le résultat visuel/fonctionnel attendu après le fix]

## 2. Plan d'implémentation détaillé (Step-by-step)
[L'assistant DOIT fournir un plan numéroté et concret des étapes à suivre pour résoudre le TODO. Pas de langage vague — chaque étape doit mentionner les noms de variables/fonctions/types exacts à utiliser.]

Exemple de format :
1. Récupérer `activeDatas.activeOnVent_M2Client` (type: `M2Client?`)
2. Si non-null, appeler `client.getNomAffichage()` → retourne `String`
3. Créer un composable `Text(...)` avec le résultat
4. etc.

## 3. Variables et types accessibles dans le scope du TODO
[Tableau Markdown listant TOUTES les variables déjà disponibles dans le scope du composable au point du TODO, avec leur type exact et comment y accéder.]

| Variable | Type | Comment y accéder | Description |
|---|---|---|---|
| `activeDatas` | `ActiveDatasFragNewProto` | paramètre du composable | Container des états actifs |
| `activeDatas.activeOnVent_M2Client` | `M2Client?` | `derivedStateOf` | Client actif lié au bon de vente courant |
| etc. | | | |

## 4. Fichiers Relatifs Impliqués
[Pour CHAQUE fichier relatif, inclure :]

### 4.1. `[NomDuFichier.kt]`
- **Chemin :** `[chemin absolu]`
- **Rôle dans le TODO :** [explication en 1-2 phrases]
- **Champs pertinents :**
  ```kotlin
  var nom: String = ""
  var numTelephone: String = ""
  var nomPrenomArabe: String = ""
  // etc.
  ```
- **Fonctions pertinentes (signature + corps complet) :**
  ```kotlin
  fun getNomAffichage(): String {
      return nomPrenomArabe.takeIf { it.isNotBlank() } ?: nom
  }
  ```

### 4.2. `[AutreFichier.kt]`
[même format...]

## 5. Code environnant LARGE (30+ lignes avant/après le TODO)
[Inclure un LARGE bloc de code autour du TODO — au minimum 30 lignes avant et 30 lignes après — pour que l'IA comprenne la structure du composable, les variables déclarées en amont, et la suite logique du code.]

```kotlin
// [Commencer suffisamment tôt pour montrer les déclarations de variables utilisées]
// ...

// ---> TODO: [texte exact] <---

// ...
// [Continuer suffisamment longtemps pour montrer le contexte suivant]
```

## 6. Imports déjà présents dans le fichier principal
[Liste exacte des imports du fichier principal. L'IA en aura besoin pour savoir quels composants sont déjà importés et lesquels ajouter.]

```kotlin
import androidx.compose.foundation.background
import androidx.compose.material3.Text
// etc. — TOUS les imports du fichier
```

## 7. Imports supplémentaires potentiellement nécessaires pour le fix
[Si le fix va probablement nécessiter de nouveaux imports (ex: `remember`, `mutableStateOf`, `clickable`, etc.), listez-les ici.]

```kotlin
// Probablement nécessaires :
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
```

## 8. PATTERN À COPIER (si t_copiePattersApp)
[Si applicable — inclure le code source du projet ClientJetPack avec des commentaires explicatifs en français sur chaque bloc logique.]

```kotlin
// === DÉBUT DU PATTERN (depuis ClientJetPack) ===
// Fichier source : [chemin]

// Étape 1 : Récupération des données client
val nomClient = getter.activeOnVent_M2Client?.nom ?: ""
// ^ Récupère le nom du client actif, "" si aucun client

// Étape 2 : Formatage du numéro de téléphone
val phoneDisplay = formatPhoneDisplay(...)
// ^ Note : cette fonction n'existe PAS dans le projet local,
//   il faudra la recréer inline ou en adapter la logique

// etc.
// === FIN DU PATTERN ===
```

**Adaptations nécessaires :** [L'assistant DOIT lister ici les différences entre le projet source et le projet local : fonctions manquantes, noms de variables différents, types différents, etc.]

## 9. Contraintes et pièges à éviter
[L'assistant DOIT lister ici tout ce que l'IA suivante doit savoir pour NE PAS faire d'erreur :]
- Fonctions qui existent dans ClientJetPack mais PAS dans le projet local
- Variables qui ont des noms différents entre les deux projets
- Nullabilité : quelles variables sont nullable et doivent être vérifiées
- Toute autre information critique

## 10. Règles d'Isolation (Ignore Rules)
```text
*
!*/
!*skill_agent/**
!*[Nom du Fichier Principal.kt]
!*[Nom du Fichier Relatif 1.kt]
!*[Nom du Fichier Relatif 2.kt]
// etc.
```
```

### 4. Report Success
Une fois le fichier généré, informez l'utilisateur avec un message de succès et un lien cliquable vers le fichier généré :
[<conversation-id>_contex.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/contexs/<conversation-id>_contex.md)

**Affichez également le nombre total de lignes de ce fichier de contexte généré.**

L'utilisateur pourra alors facilement lancer `read_ingor_last` dans une nouvelle session pour régler le TODO avec un contexte riche et complet.
