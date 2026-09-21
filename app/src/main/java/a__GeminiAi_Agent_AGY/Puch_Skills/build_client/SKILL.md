---
name: build_client
description: Compilation Gradle et deploiement automatique des versions ClientJetPack (PresenterScreen, VendeurHost, AllInOne) vers le Bureau et le telephone via ADB.
triggers:
  - b_c
  - bc_
  - build_client
  - b+1
  - b+1+z
  - b_c+z
  - b_cv
  - b_cv_all
  - b_c_all
  - bc_all
  - b_c_all+z
  - bc_all+z
  - b_c_host
  - bc_host
  - b_c_host+z
  - bc_host+z
  - b_c_presenter
  - bc_presenter
  - b_c_presenter+z
  - bc_presenter+z
---

# Guide de Compilation et Déploiement (`build_client`)

Ce skill gère la compilation des variantes de l'application **ClientJetPack** (PresenterScreen, VendeurHost, AllInOne), l'incrémentation de version dans `build.gradle.kts`, la configuration des paramètres centraux dans `M00CentralParametresOfAllApps.kt`, l'export local sur le Bureau (`Playe_Store`), et le déploiement sur le téléphone via ADB.

---

### Liste des Déclencheurs :
- `b_c` / `bc_` / `build_client` (Compile les 3 versions sans déploiement)
- `b+1` (Incrémente la version +1, compile les 3 versions, copie vers le Bureau, puis déploie les dossiers extraits et `9_Client_App_Last` sans ZIP via ADB, puis lance `p_v`)
- `b+1+z` / `b_c+z` (Incrémente la version +1, compile les 3 versions, copie vers le Bureau, crée le ZIP, déploie les dossiers extraits, le ZIP et `9_Client_App_Last` via ADB, puis lance `p_v`)
- `b_cv` / `b_cv_all` (Compile la version courante sans incrément, copie vers le Bureau, déploie via ADB, sans exécuter `p_v`)
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

## 📋 Étapes d'exécution obligatoires

### 1. Déterminer les versions à compiler
- Si un déclencheur ciblé est utilisé (ex: `b_c_all`, `b_c_host`, `b_c_presenter`, avec ou sans `+z`), la liste de compilation ne contient que la version choisie.
- Sinon, préparez la compilation séquentielle des 3 versions (PresenterScreen, VendeurHost, AllInOne).

### 2. Configurer et compiler chaque version de la liste

Pour chaque version sélectionnée, effectuez les configurations suivantes avant de lancer le build :

#### A. Incrémenter le versionName dans `app/build.gradle.kts` (si déclencheur avec incrément)
- Lisez uniquement les lignes 25 à 45 de [app/build.gradle.kts](file:///D:/AndroidStudioProjects/ClientJetPack/app/build.gradle.kts).
- Incrémentez le troisième chiffre de version propre (ex: `1.15.3` devient `1.15.4`) dans le format `1.15.4$appSuffix`.
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
  - Configurer `its_AppType` pour renvoyer `AppType.AllInOne`.
- **VendeurHost** :
  - `au_Lence_Set_Compt_Ac_KeyId` = `Compts.AbdelwahabTravailleChezGros_KeyId.keyId`
  - Configurer `its_AppType` pour renvoyer `AppType.JomLaElectroLivreurGrossist_VendeurHost`.
- **PresenterScreen** :
  - `au_Lence_Set_Compt_Ac_KeyId` = `Compts.Telephone_de_presentation.keyId`
  - `its_AppType` sera évalué à `AppType.JomLaElectroLivreurGrossist_PresenterScreen`.

#### C. Lancer la compilation
Exécutez le script Gradle en injectant le paramètre `"-PappSuffix"` correspondant :
```powershell
.\gradlew.bat assembleDebug "-PappSuffix=<SUFFIXE>" --offline --parallel --build-cache --configuration-cache
```

#### D. Exporter l'APK (Dossier Versionné & `9_Client_App_Last`)
1. **Dossier Versionné** :
   - Créez le dossier d'export sur le Bureau : `C:\Users\Abou Mohamed\Desktop\Playe_Store\<VERSION>\0.\<DOSSIER_APP>` (où `<DOSSIER_APP>` est `a_AllInOne`, `b_JomLaElectroLivreurGrossist_VendeurHost`, ou `c_JomLaElectroLivreurGrossist_PresenterScreen`).
   - Copiez-y l'APK généré `app-debug.apk` en le renommant sous le nom propre de son application (ex : `a_AllInOne.apk`, `b_JomLaElectroLivreurGrossist_VendeurHost.apk`, `c_JomLaElectroLivreurGrossist_PresenterScreen.apk`).

2. **Dossier `9_Client_App_Last`** :
   - Créez le dossier commun sur le Bureau : `C:\Users\Abou Mohamed\Desktop\Playe_Store\9_Client_App_Last\`
   - Copiez et renommez l'APK correspondant vers `9_Client_App_Last` selon le mappage suivant :
     - **AllInOne** (`a_AllInOne.apk`) ➔ `1 Centrale App.apk`
     - **VendeurHost** (`b_JomLaElectroLivreurGrossist_VendeurHost.apk`) ➔ `2 Vendeur App.apk`
     - **PresenterScreen** (`c_JomLaElectroLivreurGrossist_PresenterScreen.apk`) ➔ `3 Ecran Client Boutique.apk`

### 3. Restaurer les fichiers d'origine à la fin de tous les builds
- Restaurez le fichier [M00CentralParametresOfAllApps.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M00CentralParametresOfAllApps.kt) avec son contenu d'origine sauvegardé en mémoire.

### 4. Déploiement via ADB (sur Téléphone)
Si le déclencheur implique un déploiement (`b+1`, `b+1+z`, `b_c+z`, `b_cv`) :

#### A. Vérification de la connexion du téléphone (après copie sur le Bureau)
- Exécutez `adb devices`.
- Si aucun appareil connecté n'est listé en ligne (status `device`) :
  - Utilisez l'outil `ask_question` pour demander à l'utilisateur : *« Est-ce que tu as connecté le téléphone ? »*
  - Options :
    1. `(Recommended) J'ai connecté le téléphone, réessaye le transfert ADB`
    2. `Conserver uniquement l'export local sur le Bureau (passer ADB)`
    3. `Annuler le déploiement ADB`
  - Si l'utilisateur choisit de conserver l'export local ou si le téléphone n'est pas branché, sautez le transfert ADB et passez directement à l'étape 5.

#### B. Transfert ADB (si téléphone connecté)
1. **Dossier Versionné `0.`** :
   - Si format ZIP demandé (ex: `b+1+z`, `b_c+z`) : compressez `0.` sous `Client_V_<VERSION>.zip` et poussez le ZIP + `0.` sur `/sdcard/Abdelwahab_jeMla.com/Playe_Store/<VERSION>/`.
   - Si sans ZIP (ex: `b+1`, `b_cv`) : poussez directement le dossier extrait `0.` sur `/sdcard/Abdelwahab_jeMla.com/Playe_Store/<VERSION>/0./`.
2. **Dossier `9_Client_App_Last`** :
   - Créez le répertoire distant si nécessaire : `adb shell "mkdir -p /sdcard/Abdelwahab_jeMla.com/Playe_Store/9_Client_App_Last"`
   - Poussez les fichiers de `C:\Users\Abou Mohamed\Desktop\Playe_Store\9_Client_App_Last\` vers `/sdcard/Abdelwahab_jeMla.com/Playe_Store/9_Client_App_Last/` (`1 Centrale App.apk`, `2 Vendeur App.apk`, `3 Ecran Client Boutique.apk`).

### 5. Exécuter le skill `p_v` (Commit, Tag et Push)
- Si la version a été incrémentée (`b+1`, `b+1+z`, `b_c+z`), lancez automatiquement le skill global `p_v` pour commiter avec le numéro de version propre (ex: `1.15.4`), créer/écraser le tag annoté `v<VERSION>` avec la liste des commits, mettre à jour le tag `par_version_instaled` et tout pousser vers `github` et `origin`.

### 6. Rapport de réussite
Affichez au développeur :
- Le résumé des versions compilées.
- Les liens cliquables locaux sur le Bureau (dossier versionné et dossier `9_Client_App_Last`).
- La confirmation des transferts ADB réussis (`<VERSION>/0./` et `9_Client_App_Last/`) ou l'indication que l'export local a été conservé.
- La confirmation des actions Git de `p_v` (si applicables).