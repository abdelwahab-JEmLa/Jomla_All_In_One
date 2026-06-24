# Project Rules for ClientJetPack

## Koin Module Lifecycle & Compose Navigation
- The project dynamically loads/unloads heavy Koin modules (`heavyModules`) to conserve memory.
- To prevent `NoBeanDefFoundException` crashes during screen composition and navigation transitions:
  - **Load Before Use**: Always ensure `GlobalContext.get().loadModules(heavyModules)` is called before navigating to or rendering screens that depend on these modules.
  - **Observe Readiness State**: Never compose the destination screen directly if it injects dynamic dependencies. Instead, observe the readiness state (e.g., `heavyReady` Flow/State) and render a loading placeholder until it is `true`.
  - **Avoid Premature Unloading**: Do not unload modules prematurely while transitions or animations to/from the screen are still active, as recomposition may still request the dependencies.
  - **Scoping Dynamic Injections**: In routing components or parent screens (e.g. `MainActivity` or layout hosts containing multiple AppTypes), never invoke `koinInject()` at the root level for dynamic or conditionally loaded dependencies. Defer their injection directly into the conditional branches or child composables that are only rendered when the module is loaded.

## Room Database Versioning
- Whenever an entity model is updated (such as adding or modifying properties/fields in schemas like `M3CouleurProduitInfos`), always:
  - Increment the database version in `AppDatabase.kt`.
  - Document the exact changes in the version comment (e.g., `// Bumped from X → Y: added <field_name> to <entity_name>`).

## Kotlin Performance Best Practices
- **Enum Entries**: Prefer using `Enum.entries` instead of `Enum.values()` when iterating or filtering enums in Kotlin to avoid unnecessary array allocations on each invocation.

## Project References Resolution
- Le mot-clé/déclencheur `ref_json` ou `references` correspond à la configuration globale des chemins de projet dans [references.json](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/references.json).
- Utilisez toujours les chemins définis dans ce fichier pour cibler les opérations sur d'autres applications.

## Résolution des Noms de Skills Globaux
- Toute référence à `h_` (ou `help_skill`/`help_skills`) ou `b_c` (ou `build_client`) désigne respectivement les répertoires de skills globaux [h_](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/h_) et [build_client](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/build_client).

## Conventions de Compilation & Déploiement (`build_client`)
- Pour compiler la version cliente, utilisez le skill global `build_client` (`b_c`, `bc_` ou `b_c_all`).
- **Conservation de l'horodatage** : L'incrémentation de build dans [build.gradle.kts](file:///D:/AndroidStudioProjects/ClientJetPack/app/build.gradle.kts) doit conserver le timestamp existant (ex : `_24.20:22`) et ne pas le remplacer par l'heure de compilation active.
- **Nom de l'archive ZIP** : Compressez toujours l'export sous le nom de fichier `Client_V_<VERSION>.zip` (où `<VERSION>` est la version propre sans timestamp).
- **Restauration de configuration** : Assurez-vous de restaurer immédiatement [M00CentralParametresOfAllApps.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M00CentralParametresOfAllApps.kt) après le build pour garder Git propre.
