# Last Saved Semantics Info

- **Source File**: [A_Main_Preview_BonVentEtateScreen.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack/app/src/main/java/com/example/light_app_controles/B/Screens/Z/Screens/Test/ID1/Client_Map/App/Bon_Vent_Etate/View/a/Screens/a/BonVents/Screen/A_Main_Preview_BonVentEtateScreen.kt)
- **Active Semantics Block**: Lines 273-280

## Extracted `set()` Semantics Properties:
1. **Lines 274-275**:
   ```kotlin
   set(value = active_Datas.list_M8bon?.filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID } ?: emptyList(),
       key = SemanticsPropertyKey("listM8bon_filtered"))
   ```
2. **Line 276**:
   ```kotlin
   set(value = listM8bon ?: emptyList(), key = SemanticsPropertyKey("listM8bon"))
   ```
3. **Line 277**:
   ```kotlin
   set(value = allBons, key = SemanticsPropertyKey("allBons"))
   ```
4. **Lines 278-279**:
   ```kotlin
   set(value = listM8bon?.filter { it.keyID.takeLast(4) == "7xp4" } ?: emptyList(),
       key = SemanticsPropertyKey("listM8bon_7xp4"))
   ```
