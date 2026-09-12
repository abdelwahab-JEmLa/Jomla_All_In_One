# Exemple exmp_1 — `cc_` sur un TODO réel : Chaîne d''impression Bluetooth (TODOs relatifs)

> Exemple **réel** extrait du projet ClientJetPack (26/08/2026).
> Montre comment `cc_` gère un TODO(1) principal avec plusieurs TODOs(2.C) relatifs
> dispersés dans une chaîne d''appels d''impression.

---

## 🔵 Déclencheur utilisateur

```
cc_ le todo(1) dans DropDownItem_WindowsShare_Facture_Impots.kt
```

---

## Étape 1 — TODO(1) identifié + TODOs(2.C) relatifs localisés

**TODO principal (L119)** dans `DropDownItem_WindowsShare_Facture_Impots.kt` :

```kotlin
Text(       //<--
//TODO(1): si je site regle par exmple le nom du client don le recipe tu va incle
//         cette fichie et utilise le ctrl click aller pour trouve quesque ca chnage
    text = nomFun,
    ...
)
```

**TODOs(2.C) relatifs** localisés dans la même chaîne d''appels :

| Fichier | Ligne | Emplacement |
|---------|-------|-------------|
| `DropDownItem_WindowsShare_Facture_Impots.kt` | L190 | Appel `proceedWithPrinting(...)` |
| `DropDownItem_WindowsShare_Facture_Impots.kt` | L513 | Signature `private fun proceedWithPrinting()` |
| `DropDownItem_WindowsShare_Facture_Impots.kt` | L549 | Appel `printHandler.printBluetoothOnly(...)` |
| `PrintReceiptHandler_Juil.kt` | L31 | Signature `fun printBluetoothOnly(...)` |
| `BluetoothPrintHandler.kt` | L40 | Signature `fun printBluetoothReceipt(...)` |
| `BluetoothPrintHandler.kt` | L329 | Fonction `prepareTexteToPrint()` |

> ℹ️ Ces TODOs sont des **marqueurs de navigation ctrl+click** indiquant la chaîne
> complète à modifier si on change le format du nom client dans le reçu.

---

## Étape 2 — Fichiers rassemblés (max 10)

| # | Fichier | Raison |
|---|---------|--------|
| 1 | `DropDownItem_WindowsShare_Facture_Impots.kt` | Fichier actif — contient TODO(1) + 3×TODO(2.C) |
| 2 | `PrintReceiptHandler_Juil.kt` | Intermédiaire — `printBluetoothOnly()` |
| 3 | `BluetoothPrintHandler.kt` | Cœur impression — `prepareTexteToPrint()` |
| 4 | `M8BonVent.kt` | Modèle référencé (`bonVent`) |
| 5 | `M2Client.kt` | Modèle client (`client.nom`) |

> ✅ Dossier immédiat + imports directs. Pas de recherche globale.

---

## Étape 3 — Contenu de `context_agy.md` généré

**Chemin** :
`skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/08_26 07_10 Bluetooth_Client_Nom_Recipe/context_agy.md`

```markdown
fix todo avec la facon la plus rapide (ne pas ecrire "todo resolved" ou "TODO resolved" a la fin)

TODO(1) dans `DropDownItem_WindowsShare_Facture_Impots.kt` L119 :
```kotlin
Text(       //<--
//TODO(1): si je site regle par exmple le nom du client don le recipe tu va incle
//         cette fichie et utilise le ctrl click aller pour trouve quesque ca chnage
    text = nomFun,
```

TODOs(2.C) relatifs dans la chaîne d''impression (repères ctrl+click) :
- L190 → appel `proceedWithPrinting()`
- L513 → signature `private fun proceedWithPrinting()`
- L549 → appel `printHandler.printBluetoothOnly()`
- `PrintReceiptHandler_Juil.kt` L31 → `fun printBluetoothOnly()`
- `BluetoothPrintHandler.kt` L40 → `fun printBluetoothReceipt()`
- `BluetoothPrintHandler.kt` L329 → `prepareTexteToPrint()` (logique groupBy + StringBuilder du ticket)

**Contexte architectural** :
- Le flux d''impression part du bouton dans `DropDownItem` → `proceedWithPrinting()` (private fun dans le même fichier) → `PrintReceiptHandler_Juil.printBluetoothOnly()` → `BluetoothPrintHandler.printBluetoothReceipt()` → `prepareTexteToPrint()`.
- Le nom du client est extrait via `getClientDisplayName(client)` dans `BluetoothPrintHandler` (L52).
- `printWithoutProducts: Boolean` est déjà propagé dans toute la chaîne.
- Si on change le format du nom client (ex: majuscules, translittération arabe), c''est dans `prepareTexteToPrint()` ou `getClientDisplayName()` que ça se joue.
```

---

## Étape 4 — Contenu de `hist_copie.md`

```markdown
### 🔗 [context_agy.md](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/08_26 07_10 Bluetooth_Client_Nom_Recipe/context_agy.md)

### 🔗 [DropDownItem_WindowsShare_Facture_Impots.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/V/DiviseParSections/App/_0/Navigation/Main_DropDown/FabButton_When_Its_FastVent/DropDownMenu/View/DropDownItems/View/B6/View/DropDownItem_WindowsShare_Facture_Impots.kt)

### 🔗 [PrintReceiptHandler_Juil.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/V/DiviseParSections/App/B/ClientUisView/App/FragID2/PanierFinaleDAchat/Fragment/B/View/W/Modules/PrintReceiptHandler/Module/PrintReceiptHandler_Juil.kt)

### 🔗 [BluetoothPrintHandler.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/V/DiviseParSections/App/B/ClientUisView/App/FragID2/PanierFinaleDAchat/Fragment/B/View/W/Modules/PrintReceiptHandler/Module/BluetoothPrintHandler.kt)

### 🔗 [M8BonVent.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M8BonVent.kt)

### 🔗 [M2Client.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M2Client.kt)
```

---

## Étape 5 — Exécution clipboard

```bash
"C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
```

---

## Étape 6 — Résumé affiché

| Élément | Détail |
|---------|--------|
| 📄 Fichier contexte | [context_agy.md](.../08_26 07_10 Bluetooth_Client_Nom_Recipe/context_agy.md) |
| 📁 Fichiers copiés | `DropDownItem_WindowsShare_Facture_Impots.kt`, `PrintReceiptHandler_Juil.kt`, `BluetoothPrintHandler.kt`, `M8BonVent.kt`, `M2Client.kt` |
| 📋 Clipboard | ✅ Réussi — 6 fichiers présents dans le presse-papiers |
| ⏱️ Durée | ~9 secondes |
| 🔁 Re-copier | [run_copy.bat](.../08_26 07_10 Bluetooth_Client_Nom_Recipe/run_copy.bat) |

> 💡 Une fois le fix reçu de l''IA externe, tape **`ok_`** pour l''appliquer automatiquement dans le projet.

---

## 📌 Particularité : TODOs(2.C Relative) = marqueurs de navigation

Les `TODO(2.C Relative Au Todo(1):)` **ne nécessitent pas de code** à écrire.
Ce sont des repères pour indiquer à l''IA (et au développeur) **quels fichiers sont impactés**
si le TODO(1) parent est modifié. Le skill `cc_` les liste dans le `context_agy.md`
pour que l''IA externe connaisse toute la chaîne d''appels sans avoir à la chercher.
