# Résumé des TODOs - Branche todo/compact-header-ui-price-card-image-grid

Date : 2026-06-29

## Liste des TODOs détectés

- [ ] **Fichier** : [A_Compact_Header_FragID3.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/Application2/App/View/Pro0/Proto/ViewS/A_Compact_Header_FragID3.kt#L89) (Ligne 89)
  - **Texte** : `//TODO(1): augment taille et fait que ca soit trouge`
  - **Contexte** : Le `Text` affichant `relative_M1produit.nom` dans le header compact doit avoir une taille augmentée et une couleur rouge (au lieu de `onSurface`).

- [ ] **Fichier** : [A_Compact_Header_FragID3.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/Application2/App/View/Pro0/Proto/ViewS/A_Compact_Header_FragID3.kt#L113) (Ligne 113)
  - **Texte** : `//TODO(1): affiche ici حبة au liex U`
  - **Contexte** : Dans le `FlowRow` des info-cards, afficher "حبة" (unité en arabe) au lieu de "U" comme libellé d'unité dans les cartes d'info du produit.

- [ ] **Fichier** : [A_Compact_Header_FragID3.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/Application2/App/View/Pro0/Proto/ViewS/A_Compact_Header_FragID3.kt#L177) (Ligne 177)
  - **Texte** : `//TODO(1):enleve`
  - **Contexte** : L'`InfoCard` affichant le prix unitaire client (avec icône `AttachMoney`) doit être supprimée.

- [ ] **Fichier** : [colorImageCard_App4.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/Application2/App/View/Pro0/Proto/ViewS/colorImageCard_App4.kt#L59) (Ligne 59)
  - **Texte** : `//TODO(1): extract et fait qus si 15 de affiche 10 image au bas 5 fait que si 30 affiche da_20 et au bas 10`
  - **Contexte** : La logique d'affichage de l'image de prix (da_5, da_10, etc.) doit être étendue pour gérer les paliers : 15→da_10+5, 25→da_20+5, 30→da_20+10, 40→da_20+da_20, 50→da_50, 60→da_50+10. Extraire dans une fonction dédiée.
