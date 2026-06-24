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
