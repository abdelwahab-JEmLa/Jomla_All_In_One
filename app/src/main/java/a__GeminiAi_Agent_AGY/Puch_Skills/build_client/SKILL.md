---
name: build_client
description: "Compile client app, increment version (+1) keeping timestamp, export to Desktop as renamed APKs, conditionally zip and deploy via ADB (only if trigger ends with +z), and restore build.gradle.kts and M00 at the end. Supports sequential building."
---
# Skill - Fast Build, Increment Version & Deploy Client App (`build_client`)

Ce skill permet de compiler l'application cliente, d'incrémenter le numéro de build (+1) de `versionName` dans `app/build.gradle.kts` tout en conservant le timestamp, et de l'exporter sur le Bureau sous son nom propre (ex: `a_AllInOne.apk`). Si le déclencheur contient le suffixe `+z` (ex : `b_+1+z`, `b+1+z`, `b_c+z`), il compresse également l'export en ZIP et le déploie sur le téléphone connecté.

## 🔄 Comportement des Déclencheurs (Triggers)
- **Compilation Globale (Par défaut)** : Si vous lancez `b_c` / `bc_` / `build_client` **sans citer** de version dynamic, l'assistant compile séquentiellement les **3 versions d'applications** (PresenterScreen, VendeurHost, AllInOne) dans leurs dossiers respectifs.
- **Compilation Ciblée (Si précisée)** : Si vous lancez un déclencheur ciblé (ex: `b_c_all` ou `b_c_host`), l'assistant compile **uniquement** la version demandée.

### Liste des Déclencheurs :
- `b_c` / `bc_` / `build_client` (Compile les 3 versions sans déploiement)
- `b_c+z` / `bc_+z` / `build_client+z` (Compile les 3 versions ET déploie)
- `b_c_all` / `bc_all` (Compile AllInOne sans déploiement)
- `b_c_all+z` / `bc_all+z` (Compile AllInOne ET déploie)
- `b_c_host` / `bc_host` (Compile VendeurHost sans déploiement)
- `b_c_host+z` / `bc_host+z` (Compile VendeurHost ET déploie)
- `b_c_presenter` / `bc_presenter` (Compile PresenterScreen sans déploiement)
- `b_c_presenter+z` / `bc_presenter+z` (Compile PresenterScreen ET déploie)

---

## ⚡ Économie de Tokens & Performance (Token Economy)
Pour optimiser la vitesse d'exécution et réduire la consommation de tokens :
- **Pas de lecture complète** : Ne lisez jamais entièrement [build.gradle.kts](file:///D:/AndroidStudioProjects/ClientJetPack/app/build.gradle.kts) ou [M00CentralParametresOfAllApps.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M00CentralParametresOfAllApps.kt).
- **Lecture par plage** : Ciblez uniquement les lignes requises avec le paramètre `StartLine`/`EndLine` de `view_file` :
  - Pour `build.gradle.kts` : Lire entre la ligne 25 et 45.
  - Pour `M00CentralParametresOfAllApps.kt` : Lire entre la ligne 40 et 75.
- **Remplacement ciblé** : Utilisez `replace_file_content` avec la zone la plus restreinte possible.

---

## Étapes d'exécution obligatoires

### 0. Vérification préliminaire de la connexion du téléphone (ADB)
- Si le déclencheur demande ou implique un déploiement sur le téléphone (ex : se termine par `+z` ou demande de copie adb) :
  - Exécutez `adb devices` avant de démarrer le long processus de compilation.
  - Si aucun appareil connecté n'est listé en ligne (status `device`), utilisez obligatoirement l'outil `ask_question` pour poser cette question à l'utilisateur : « Est-ce que tu as connecté le téléphone ? » (avec les options d'interaction adaptées).
  - N'entamez pas le build avant d'avoir obtenu la confirmation ou d'avoir résolu le problème de connexion pour éviter de compiler inutilement.

### 1. Déterminer les versions à compiler
- Si un déclencheur ciblé est utilisé (ex: `b_c_all`, `b_c_host`, `b_c_presenter`, avec ou sans `+z`), la liste de compilation ne contient que la version choisie.
- Sinon, préparez la compilation séquentielle des 3 versions (PresenterScreen, VendeurHost, AllInOne).

### 2. Configurer et compiler chaque version de la liste

Pour chaque version sélectionnée, effectuez les configurations suivantes avant de lancer le build :

#### A. Incrémenter le versionName dans `app/build.gradle.kts`
- Lisez uniquement les lignes 25 à 45 de [app/build.gradle.kts](file:///D:/AndroidStudioProjects/ClientJetPack/app/build.gradle.kts).
- Incrémentez de 1 le numéro de build (le dernier chiffre de la partie version, ex: `08` devient `09`) dans le littéral de `versionName`.
- **Laissez le timestamp (Jour.Heure:Minute) inchangé** (ex: conservez `24.20:22` tel quel).
- Ne modifiez pas physiquement le suffixe dans le fichier. Celui-ci sera passé via le paramètre Gradle `-PappSuffix` lors de la compilation.
- Valeurs du suffixe selon l'application :
  - Pour AllInOne : `.a_AllInOne`
  - Pour VendeurHost : `.b_JomLaElectroLivreurGrossist_VendeurHost`
  - Pour PresenterScreen : `.c_JomLaElectroLivreurGrossist_PresenterScreen`

#### B. Mettre à jour les paramètres dans `M00CentralParametresOfAllApps.kt`
Lisez les lignes 40 à 75 de [M00CentralParametresOfAllApps.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M00CentralParametresOfAllApps.kt). Appliquez la configuration correspondante :
- **itsDevMode** : Désactivez toujours le mode de développement en modifiant `itsDevMode = false` pour toutes les versions compilées.
- **AllInOne** :
  - `au_Lence_Set_Compt_Ac_KeyId` = `Compts.AbdelwahabTravailleChezGros_KeyId.keyId`
  - Configurer `its_AppType` pour renvoyer `AppType.AllInOne` (modifier l'affectation ou le bloc conditionnel).
- **VendeurHost** :
  - `au_Lence_Set_Compt_Ac_KeyId` = `Compts.AbdelwahabTravailleChezGros_KeyId.keyId`
  - Configurer `its_AppType` pour renvoyer `AppType.JomLaElectroLivreurGrossist_VendeurHost` (ou s'assurer que la branche d'évaluation par défaut renvoie ce type).
- **PresenterScreen** :
  - `au_Lence_Set_Compt_Ac_KeyId` = `Compts.Telephone_de_presentation.keyId`
  - `its_AppType` sera évalué à `AppType.JomLaElectroLivreurGrossist_PresenterScreen` grâce à la condition `if (au_Lence_Set_Compt_Ac_KeyId == Compts.Telephone_de_presentation.keyId)`.

#### C. Lancer la compilation
Exécutez le script Gradle en injectant le paramètre `"-PappSuffix"` correspondant :
```powershell
.\gradlew.bat assembleDebug "-PappSuffix=<SUFFIXE>" --offline --parallel --build-cache --configuration-cache
```
*(où `<SUFFIXE>` correspond à la valeur du suffixe, ex: `"-PappSuffix=.c_JomLaElectroLivreurGrossist_PresenterScreen"`)*

#### D. Exporter l'APK
- Créez le dossier d'export sur le Bureau : `C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0.\<DOSSIER_APP>` (où `<DOSSIER_APP>` est `a_AllInOne`, `b_JomLaElectroLivreurGrossist_VendeurHost`, ou `c_JomLaElectroLivreurGrossist_PresenterScreen`).
- Copiez-y l'APK généré `app-debug.apk` **en le renommant sous le nom propre de son application** (ex : `a_AllInOne.apk`, `b_JomLaElectroLivreurGrossist_VendeurHost.apk`, `c_JomLaElectroLivreurGrossist_PresenterScreen.apk`).

### 3. Restaurer les fichiers d'origine à la fin de tous les builds
- Restaurez le fichier [M00CentralParametresOfAllApps.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M00CentralParametresOfAllApps.kt) avec son contenu d'origine sauvegardé en mémoire.
- Le fichier `build.gradle.kts` n'a pas besoin d'être restauré car le suffixe n'a pas été écrit physiquement (il utilise par défaut le suffixe `.Dev` en l'absence de paramètre).

### 3.5. Condition de déploiement (+z)
Si le déclencheur d'origine **ne contient pas** le suffixe `+z` ou `_z` (ex: `b_+1`, `b+1`, `b_c`), **ARRÊTEZ** le processus ici. N'exécutez pas les étapes 4 (ZIP/ADB) et 5 (Git `p_v`).
Si et seulement si le déclencheur contient le suffixe `+z`, `+zip` ou `_z` (ex: `b+1+z`, `b_+1+z`, `b_c+z`), continuez avec les étapes suivantes :

### 4. Compresser le dossier d'export en ZIP et le déployer via ADB
- Zippez le dossier `0.` sous le nom `Client_V_<VERSION>.zip` (où `<VERSION>` est la version propre, ex: `1.14.0.09`).
- Poussez le ZIP ainsi que le dossier extrait sur la carte SD du téléphone avec ADB dans `/sdcard/Abdelwahab_jeMla.com/Playe_Store/<VERSION>/`.
  *(Remarque : si `adb` n'est pas configuré dans le PATH global sous Windows, utilisez l'exécutable localisé dans `$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe`)*

### 5. Exécuter le skill `p_v` (Commit, Tag et Push)
- Une fois les fichiers exportés et déployés avec succès, lancez automatiquement le skill global `p_v` (ou exécutez ses étapes) pour commiter avec le numéro de version propre (ex: `1.14.0.12`), écraser le tag `par_version_instaled` et pousser vers le dépôt distant.

### 6. Rapport de réussite
Affichez au développeur le résumé des versions compilées, les liens cliquables locaux sur le Bureau, la confirmation des transferts ADB réussis (si applicables), ainsi que la confirmation des actions Git de `p_v` (si applicables).
