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
- Toute référence à `h_` (ou `help_skill`/`help_skills`), `b_c` (ou `build_client`) ou `lit_patterns` (ou `read_c_p`/`c_p`) désigne respectivement les répertoires de skills globaux [h_](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/h_), [build_client](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/build_client) et [lit_patterns](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/lit_patterns).

## Thermal Print Customizations (Print Without Products)
- When printing receipts or tickets, the Bluetooth printing module supports printing a summary receipt without listing individual products (useful for credit payments or quick totals).
- Pass `printWithoutProducts = true` to `printBluetoothReceipt` or `printBluetoothOnly` to omit detailed product lines while keeping the totals, count of items, client name, and credit details intact.


## Conventions de Compilation & Déploiement (`build_client`)
- Pour compiler la version cliente, utilisez le skill global `build_client` (`b_c`, `bc_` ou `b_c_all`).
- **Comportement Ciblé** : Si l'utilisateur cite spécifiquement l'une des applications (ex: `b_c_all`/`AllInOne`, `b_c_host`/`VendeurHost`, `b_c_presenter`/`PresenterScreen`), ne compiler que l'application demandée. Si aucune n'est citée explicitement, compiler séquentiellement les 3 applications.
- **Conservation de l'horodatage** : L'incrémentation de build dans [build.gradle.kts](file:///D:/AndroidStudioProjects/ClientJetPack/app/build.gradle.kts) doit conserver le timestamp existant (ex : `_24.20:22`) et ne pas le remplacer par l'heure de compilation active.
- **Nom de l'archive ZIP** : Compressez toujours l'export sous le nom de fichier `Client_V_<VERSION>.zip` (où `<VERSION>` est la version propre sans timestamp).
- **Restauration de configuration** : Assurez-vous de restaurer immédiatement [M00CentralParametresOfAllApps.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M00CentralParametresOfAllApps.kt) après le build pour garder Git propre.
- **Résolution de ADB** : Si la commande `adb` n'est pas reconnue globalement, utilisez le chemin absolu de l'exécutable sous `$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe` pour effectuer les opérations de push.
- **PowerShell Argument Quoting** : Lorsque vous passez des arguments contenant des préfixes de points (ex: `-PappSuffix=.a_AllInOne`) dans PowerShell, enveloppez toujours l'argument entier de double guillemets (ex: `"-PappSuffix=.a_AllInOne"`) pour éviter que PowerShell n'interprète mal la chaîne de caractères.
- **Enchaînement Git** : À la fin du processus de compilation et déploiement de `build_client` (`b_c`), lancez automatiquement le skill global `p_v` pour commiter la version, créer/écraser le tag de version spécifique (ex : `v1.14.0.12`) en y incluant la description des changements depuis la version précédente (`version - 1`), mettre à jour le tag `par_version_instaled` et tout pousser vers les dépôts distants (`github` et `origin`).


## Color Media Presentations & GIF Conversion
- **Adding Colors**: Adding a new color/variant should be a simple text-only creation operation (Type.Nom), without launching a file picker immediately.
- **Media Picking & Cleanup**: In edit panels, provide separate options for image (🖼) and video (🎥) gallery pickers. When a new file is picked, delete/erase any existing files under the previous media extension to prevent stale files, update `extensionDisponible`, and update `il_a_une_video_presentaion` accordingly.
- **GIF Conversion & Playback**: Instead of playing heavy raw video files using video players (like ExoPlayer) in presentation screens, convert the picked videos to GIFs (or render them as GIF) to display them using lightweight GIF loaders (e.g., Glide or looping animations). This limits memory footprint and avoids native media player crashes.

## Fast File Deployment (zip_colle / z_)
- **History Mapping Lookup**: When the user triggers the file copy skill (`z_` or `zip_colle`), always prioritize using the local mapping file `.zip_colle_history.json` at the root of the workspace. If the filename exists as a key, use its absolute path immediately.
- **Search Optimization**: If the file path is not found in the history mapping, restrict any recursive file searches strictly to the `app/src/` subdirectory of the workspace to avoid scanning heavy generated folders (like `.gradle`, `.git`, or `build`), which reduces search times from 20s to <0.2s.
- **History Update**: Upon successfully copying or updating a file, update or create the `.zip_colle_history.json` file in the workspace root with the new mapping `{"FileName.kt": "Absolute/Path/To/FileName.kt"}`.

## Customizations & /learn Workflow
- **Mandatory Proposal**: Whenever a `/learn` command is run or rules/skills modifications are proposed, always create a `learning_proposal.md` artifact detailing the changes.
- **Explicit Consent**: Never write, overwrite, or edit any configuration files (such as project rules, global rules, or skill Markdown files) without displaying the proposal first and obtaining the user's explicit approval to proceed.


