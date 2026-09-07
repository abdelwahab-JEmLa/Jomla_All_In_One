---
name: smart_build_router
description: Router skill that intercepts build commands (such as b_v+1, b_+1, b+1, with or without +z suffix) and routes them to build_apk_au_playe_store_phone (for Light App) or build_client (for Client JetPack) depending on the active workspace.
---

# Skill - Smart Build Router

This skill acts as a router for application builds. When the user requests a build trigger (such as `b_v+1`, `b_+1`, `b+1` or their zip-enabled versions like `b+1+z`, `b_+1+z`), it automatically detects the current workspace context and delegates the task to the correct build skill.

## Triggers
- `b_v+1`
- `b_v+1+z`
- `b_+1`
- `b_+1+z`
- `b+1`
- `b+1+z`
- `smart_build_router`

## Routing Rules
1. **Light App (`Light_App_Controles`)**:
   - If the current workspace path contains `Light_App_Controles`, delegate execution to the **`build_apk_au_playe_store_phone`** skill.
2. **Client App (`ClientJetPack`)**:
   - If the current workspace path contains `Client` or is related to the client application, delegate execution to the **`build_client`** skill.
