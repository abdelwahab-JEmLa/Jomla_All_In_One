---
name: fm_
description: Explains and manages database synchronization operations between Room, local CSV files, and Firebase in the FragMap screens.
---

# Skill - FragMap Data Flow (fm_)

This skill instructs the assistant on how to explain and manage the database synchronization operations (between Room, local CSV files, and Firebase Realtime Database) in the FragMap screens.

---

## Trigger Phrases
- "fm_"
- "fm_flow"

---

## Steps to Execute

### When "fm_flow" is triggered:

#### 1. Display the FragMap Database Sync Flow Summary
Display a clear visual ASCII or Mermaid diagram of the data flow and explain the 3 storage levels:
*   **Room (Local Cache)**: SQLite database embedded in the Android app for fast local UI queries.
*   **CSV (Phone Storage)**: Local backup CSV files stored in `TestDatas/` directory on the phone's external storage.
*   **Firebase (Cloud Database)**: Central authority Realtime Database in the cloud.

#### 2. Detail the Sync Operations
List the available actions in the FragMap dropdown menu options:
*   **But1 (Room -> CSV)**: Exports local Room database records to local CSV files.
*   **But2 (CSV -> Firebase)**: Pushes local CSV data to Firebase cloud database.
*   **But3 (CSV -> Room)**: Imports records from local CSV files into Room database.
*   **But6 (Firebase -> CSV)**: Pulls data from Firebase cloud database and saves it into local CSV files.
*   **But9 (Firebase -> Room)**: Pulls data from Firebase cloud database and loads it directly into Room database.
*   **But8 (Clear Room)**: Deletes all records from the local Room database.

#### 3. Present Clickable Links
Always present the user with clickable links to the main FragMap dropdown menu files for quick editing:
*   📱 [M2Client Dropdown Menu](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/com/example/light_app_controles/B/Screens/Z/Screens/Test/ID1/Client_Map/App/Bon_Vent_Etate/View/ID1/FloatingMenu_Plus_RoomCsvBigDatas/Feature/Options/M2Client_Operations_FragMap_DropdownMenu/Actions/M2Client_Operations_FragMap_DropdownMenu.kt)
*   📱 [M8Bon Dropdown Menu](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/com/example/light_app_controles/B/Screens/Z/Screens/Test/ID1/Client_Map/App/Bon_Vent_Etate/View/ID1/FloatingMenu_Plus_RoomCsvBigDatas/Feature/Options/M8Bon_Operations_FragMap_DropdownMenu/Actions/M8Bon_Operations_FragMap_DropdownMenu.kt)
