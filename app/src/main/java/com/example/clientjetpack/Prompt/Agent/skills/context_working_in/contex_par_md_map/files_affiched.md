# files_affiched.md

Voici l'arborescence des dossiers et des fichiers du contexte actif :

Racine commune masquée : `.` 

```diff
  ├── app/
  │   ├── build.gradle.kts
  │   └── src/
  │       └── main/
  │           └── java/
  │               ├── EntreApps/
+ │               │   └── Shared/            ++
+ │               │       ├── Models/
  <!--
+ │               │       │   ├── Components/
+ │               │       │   │   ├── DisponibilityEtates.kt
+ │               │       │   │   ├── Ousstad_Tahfid.kt
+ │               │       │   │   └── Prioriter.kt
+ │               │       │   ├── Home/
+ │               │       │   │   └── ActiveCentralValues.kt
+ │               │       │   ├── M00CentralParametresOfAllApps.kt
+ │               │       │   ├── M09AppCompt.kt
+ │               │       │   ├── Relative_Produits/
+ │               │       │   │   └── Models/
+ │               │       │   │       ├── M01Produit.kt
+ │               │       │   │       ├── M16CategorieProduit.kt
+ │               │       │   │       ├── M21CataloguesCategorie.kt
+ │               │       │   │       └── M3CouleurProduitInfos.kt
+ │               │       │   ├── Relative_Vents/
+ │               │       │   │   └── Models/
+ │               │       │   │       ├── M10OperationVentCouleur.kt
+ │               │       │   │       ├── M13TarificationInfos.kt
+ │               │       │   │       ├── M14VentPeriode.kt
+ │               │       │   │       ├── M15Grossist.kt
+ │               │       │   │       ├── M2Client.kt
+ │               │       │   │       └── M8BonVent.kt
+ │               │       │   └── Screen.kt
  -->
+ │               │       ├── Modules/
  <!--
+ │               │       │   ├── Base/
+ │               │       │   │   ├── AppDatabase.kt
+ │               │       │   │   ├── SQL/
+ │               │       │   │   │   ├── Dao13TarificationInfos.kt
+ │               │       │   │   │   ├── Dao14VentPeriode.kt
+ │               │       │   │   │   ├── Dao_M03CouleurProduitInfos.kt
+ │               │       │   │   │   ├── Dao_M10OperationVentCouleur.kt
+ │               │       │   │   │   ├── Dao_M16CategorieProduit.kt
+ │               │       │   │   │   ├── Dao_M1Produit.kt
+ │               │       │   │   │   ├── Dao_M2Client.kt
+ │               │       │   │   │   ├── Dao_M8BonVent.kt
+ │               │       │   │   │   └── Dao_M9AppCompt.kt
+ │               │       │   │   └── Setter_LongDatas.kt
+ │               │       │   ├── PermissionHandler.kt
+ │               │       │   └── Uis/
+ │               │       │       └── Ui/
+ │               │       │           ├── StoragePermissionDialog.kt
+ │               │       │           └── SyncProgressIndicator.kt
  -->
+ │               │       └── Ui/
+ │               │           ├── Dialog/
  <!--
+ │               │           │   ├── A_PressistatntMainActivityButtons_App4.kt
+ │               │           │   ├── But_4_DisconnectFAB.kt
+ │               │           │   ├── But_4_FloatingSearchFAB.kt
+ │               │           │   ├── ButtonID7/
+ │               │           │   │   └── Action/
+ │               │           │   │       ├── But7_Cree_Images_Bons.kt
+ │               │           │   │       ├── Datas.kt
+ │               │           │   │       ├── Module/
+ │               │           │   │       │   ├── A_PrintReceiptHandler_ProMai.kt
+ │               │           │   │       │   ├── B_Generateur_ProMai.kt
+ │               │           │   │       │   ├── C_PdfPrintHandler.kt
+ │               │           │   │       │   ├── Pdf/
+ │               │           │   │       │   │   ├── A_PdfTableBuilder.kt
+ │               │           │   │       │   │   ├── PdfContentBuilder.kt
+ │               │           │   │       │   │   ├── PdfFileNamingUtils.kt
+ │               │           │   │       │   │   ├── PdfFormatterUtils.kt
+ │               │           │   │       │   │   ├── PdfGeneratorCore.kt
+ │               │           │   │       │   │   ├── PdfSaverUtility.kt
+ │               │           │   │       │   │   ├── PdfType.kt
+ │               │           │   │       │   │   └── UploadHandler.kt
+ │               │           │   │       │   └── WindowsShareHandler.kt
+ │               │           │   │       └── initiateBackgroundPdfCreation.kt
+ │               │           │   └── ButtonID8/
+ │               │           │       └── Action/
+ │               │           │           ├── Button_8_Imgs_Send_whatsappBuisness_Stored_Bon.kt
+ │               │           │           ├── findBonJpgsFromMediaStore.kt
+ │               │           │           └── sendImgsViaWhatsAppBusiness.kt
  -->
+ │               │           └── Views/
- │               │               ├── FastEdite_OutlinedTextField.kt            --
- │               │               └── FastEdite_OutlinedTextField_2.kt            --
  │               ├── com/
  │               │   └── example/
  │               │       └── light_app_controles/
  │               │           ├── A/
  │               │           │   └── Main/
  │               │           │       ├── MainActivity.kt
  │               │           │       ├── MyApplication.kt
  │               │           │       └── Prompt/
  │               │           │           └── Ai/
  │               │           │               └── Instruction_Str_Replace.md
  │               │           ├── B/
  │               │           │   └── Screens/
  │               │           │       ├── MainScreen.kt
  │               │           │       └── Z/
  │               │           │           └── Screens/
  │               │           │               └── Test/
  │               │           │                   └── ID1/
  │               │           │                       └── Client_Map/
  │               │           │                           └── App/
  │               │           │                               └── Bon_Vent_Etate/
  │               │           │                                   └── View/             ??
  │               │           │                                       ├── ID1/
  │               │           │                                       │   └── FloatingMenu_Plus_RoomCsvBigDatas/
  │               │           │                                       │       └── Feature/
  │               │           │                                       │           └── Options/
  │               │           │                                       │               ├── M1/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       ├── Action/
  │               │           │                                       │               │       │   ├── But1_Export_M1_Room_To_Csv.kt
  │               │           │                                       │               │       │   ├── But2_Export_M1_Csv_To_FireBase.kt
  │               │           │                                       │               │       │   ├── But3_Import_M1Csv_To_Room.kt
  │               │           │                                       │               │       │   ├── But6_Import_M1_FireBase_To_Csv.kt
  │               │           │                                       │               │       │   ├── But8_DeleteAll_M1_Room.kt
  │               │           │                                       │               │       │   └── But9_Import_M1_FireBase_To_Room.kt
  │               │           │                                       │               │       └── M01_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M10/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       └── M10_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M13/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       └── M13_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M14/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       └── M14_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M2Client_Operations_FragMap_DropdownMenu/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       ├── Action/
  │               │           │                                       │               │       │   ├── But1_Export_M2_Room_To_Csv.kt
  │               │           │                                       │               │       │   ├── But2_Export_M2_Csv_To_FireBase.kt
  │               │           │                                       │               │       │   ├── But3_Import_M2Csv_To_Room.kt
  │               │           │                                       │               │       │   ├── But6_Import_M2_FireBase_To_Csv.kt
  │               │           │                                       │               │       │   ├── But8_DeleteAll_M2_Room.kt
  │               │           │                                       │               │       │   └── But9_Import_M2_FireBase_To_Room.kt
  │               │           │                                       │               │       └── M2Client_Operations_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M3/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       ├── Action/
  │               │           │                                       │               │       │   ├── But1_Export_M8_Room_To_Csv.kt
  │               │           │                                       │               │       │   ├── But2_Export_M8_Csv_To_FireBase.kt
  │               │           │                                       │               │       │   ├── But3_Import_M8Csv_To_Room.kt
  │               │           │                                       │               │       │   ├── But6_Import_M8_FireBase_To_Csv.kt
  │               │           │                                       │               │       │   ├── But8_DeleteAll_M8_Room.kt
  │               │           │                                       │               │       │   └── But9_Import_M8_FireBase_To_Room.kt
  │               │           │                                       │               │       └── M03_Operations_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M8Bon_Operations_FragMap_DropdownMenu/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       ├── Action/
  │               │           │                                       │               │       │   ├── But1_Export_M8_Room_To_Csv.kt
  │               │           │                                       │               │       │   ├── But2_Export_M8_Csv_To_FireBase.kt
  │               │           │                                       │               │       │   ├── But3_Import_M8Csv_To_Room.kt
  │               │           │                                       │               │       │   ├── But6_Import_M8_FireBase_To_Csv.kt
  │               │           │                                       │               │       │   ├── But8_DeleteAll_M8_Room.kt
  │               │           │                                       │               │       │   └── But9_Import_M8_FireBase_To_Room.kt
  │               │           │                                       │               │       └── M8Bon_Operations_FragMap_DropdownMenu.kt
  │               │           │                                       │               ├── M9/
  │               │           │                                       │               │   └── Actions/
  │               │           │                                       │               │       ├── Action/
  │               │           │                                       │               │       │   ├── But1_Export_M9_Room_To_Csv.kt
  │               │           │                                       │               │       │   ├── But2_Export_M9_Csv_To_FireBase.kt
  │               │           │                                       │               │       │   ├── But3_Import_M9Csv_To_Room.kt
  │               │           │                                       │               │       │   ├── But6_Import_M9_FireBase_To_Csv.kt
  │               │           │                                       │               │       │   ├── But8_DeleteAll_M9_Room.kt
  │               │           │                                       │               │       │   └── But9_Import_M9_FireBase_To_Room.kt
  │               │           │                                       │               │       └── M09_FragMap_DropdownMenu.kt
  │               │           │                                       │               └── a/
  │               │           │                                       │                   └── Main/
  │               │           │                                       │                       ├── BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.kt
  │               │           │                                       │                       ├── MultiOperations_FragMap_DropdownMenu.kt
  │               │           │                                       │                       └── ViewModel/
  │               │           │                                       │                           └── FeatreID1_ViewModel.kt
  │               │           │                                       ├── ID2/
  │               │           │                                       │   └── CaptureListItems/
  │               │           │                                       │       └── Feature/
  │               │           │                                       │           └── Capture/
  │               │           │                                       │               ├── Afficheur_locale_Image_Captured.kt
  │               │           │                                       │               └── CapturableLayer_FINAL.kt
  │               │           │                                       ├── Z/
  │               │           │                                       │   └── Components/
  │               │           │                                       │       ├── AvertissementDialog.kt
  │               │           │                                       │       ├── EditableAmountField.kt
  │               │           │                                       │       ├── PubAbdelwahabElectroGroStore.kt
  │               │           │                                       │       └── Y_DatesHandler.kt
+ │               │           │                                       └── a/            ++
  <!--
+ │               │           │                                           └── Screens/
+ │               │           │                                               └── a/
+ │               │           │                                                   └── Vendeur_Boutiqe/
+ │               │           │                                                       └── Screen/
+ │               │           │                                                           ├── View/
+ │               │           │                                                           │   ├── A_Item_Produit_App4.kt
+ │               │           │                                                           │   ├── Components/
+ │               │           │                                                           │   │   ├── A_Header/
+ │               │           │                                                           │   │   │   └── View/
+ │               │           │                                                           │   │   │       ├── A_Compact_Header_App4.kt
+ │               │           │                                                           │   │   │       ├── DeleteProductHeader.kt
+ │               │           │                                                           │   │   │       ├── EditableDoubleInfoCard.kt
+ │               │           │                                                           │   │   │       ├── EditableInfoCard.kt
+ │               │           │                                                           │   │   │       ├── FastInit_Double_Outlined_Edite_Modulable_Proto4.kt
+ │               │           │                                                           │   │   │       ├── FastInit_Outlined_Int_Edite_Modulable_Proto4.kt
+ │               │           │                                                           │   │   │       ├── InfoCard.kt
+ │               │           │                                                           │   │   │       └── M16Categorie/
+ │               │           │                                                           │   │   │           └── CategoryBadge.kt
+ │               │           │                                                           │   │   ├── Big_Principale_FragID3.kt
+ │               │           │                                                           │   │   └── SubColorCard_WithButton.kt
+ │               │           │                                                           │   └── ViewS/
+ │               │           │                                                           │       └── Views/
+ │               │           │                                                           │           ├── Image_Displaye.kt
+ │               │           │                                                           │           └── Lenceur_Vent_Handler/
+ │               │           │                                                           │               └── View/
+ │               │           │                                                           │                   ├── CartonVentHandler_App4.kt
+ │               │           │                                                           │                   ├── Lenceur_Vent_Handler_App4.kt
+ │               │           │                                                           │                   ├── Pricipale_Tariffs_Vendeurs.kt
+ │               │           │                                                           │                   ├── TariffItem.kt
+ │               │           │                                                           │                   ├── Tariffs_MainList.kt
+ │               │           │                                                           │                   └── Z_EntreParEcriture_Tariff.kt
+ │               │           │                                                           ├── ViewModel/
+ │               │           │                                                           │   ├── A_ViewModel_NewProtoPatterns.kt
+ │               │           │                                                           │   ├── ActiveDatasFragNewProto.kt
+ │               │           │                                                           │   ├── Filter_Affichage_Mode_Proto.kt
+ │               │           │                                                           │   ├── Initializer_ViewModel.kt
+ │               │           │                                                           │   ├── ProductListFilterLogic.kt
+ │               │           │                                                           │   ├── Setter_ViewModel.kt
+ │               │           │                                                           │   └── UiState_NewProtoPatterns.kt
+ │               │           │                                                           ├── a_Compact_Presentoire_App_Produits_FragID4.kt
+ │               │           │                                                           └── b_List_LazyColumnList_App4.kt
  -->
  │               │           └── ui/
  │               │               └── theme/
  │               │                   ├── Color.kt
  │               │                   ├── Theme.kt
  │               │                   └── Type.kt
  │               └── skill_agent/             ??
  │                   ├── agy_to_project.md
  │                   ├── as_click_run.md
  │                   ├── build.md
  │                   ├── build_.md
  │                   ├── consize_comments.md
  │                   ├── contexTrensefert/
  │                   │   ├── conTr_.md
  │                   │   └── conversationsContext/
  │                   │       ├── 05e36d13-b928-4de3-b71f-30bff8eb3abd_agy.md
  │                   │       ├── 0fb9c547-7b47-4758-ab44-eede3ea62aa2_agy.md
  │                   │       ├── 40972635-105c-491a-b564-8de91ada4b5b_agy.md
  │                   │       ├── a2e48ae9-e2f1-4acc-b053-11c981a0802f_agy.md
  │                   │       └── f96cf255-aa97-44e3-82b7-4c6e83c2df8f_agy.md
  │                   ├── context_working_in/
  │                   │   ├── contex_par_md_map/
  │                   │   │   ├── apply_map.py
  │                   │   │   ├── color_map.py
  │                   │   │   ├── contex_par_md_ma.md
  │                   │   │   ├── files_affiched.md
  │                   │   │   ├── generate_map.py
  │                   │   │   └── refresh_map.py
  │                   │   └── context_working_in.md
  │                   ├── cop_last/
  │                   │   ├── copy_.md
  │                   │   └── list_copied_files.md
  │                   ├── cop_last.md
  │                   ├── csv_d/
  │                   │   ├── csv_d.md
  │                   │   └── last_query.md
  │                   ├── fb_db/
  │                   │   ├── fb_d.md
  │                   │   ├── last_query.md
  │                   │   └── last_query_m13_m3.md
  │                   ├── h_.md
  │                   ├── help_skill.md
  │                   ├── launch_preview.md
  │                   ├── log_.md
  │                   ├── log_f.md
  │                   ├── push_tagged.md
  │                   ├── room_d/
  │                   │   ├── last_query.md
  │                   │   ├── query_room.py
  │                   │   └── room_d.md
  │                   ├── screenshot.md
  │                   ├── sem_/
  │                   │   ├── format_semantics.py
  │                   │   ├── last_sem_d.md
  │                   │   └── last_semantics.md
  │                   ├── sem_.md
  │                   ├── t_.md
  │                   ├── t_appClient_chain_todo.md
  │                   ├── t_copiePattersApp.md
  │                   ├── tap/
  │                   │   ├── Schedule_agy_task_tap_l.md
  │                   │   ├── active_schedule.txt
  │                   │   ├── last_tap.txt
  │                   │   ├── tap.md
  │                   │   ├── tap_fast.py
  │                   │   └── wait_and_tap.py
  │                   └── todo_bubelle.md
+ └── build.gradle.kts            ++
```
