---
name: p_v
description: "Commit staged changes with the build version as the commit message, tag the commit with 'par_version_instaled' (overwriting it if it exists), and push to remote."
---
# Skill - Push Version (`p_v`)

Ce skill permet de commiter les fichiers modifiés en utilisant le numéro de version du build (extrait de `app/build.gradle.kts`) comme message de commit, de poser le tag `par_version_instaled` (en écrasant l'ancien tag s'il existe), puis de pousser le commit et le tag vers le dépôt distant.

## Déclencheurs (Triggers)
- `p_v`
- `pv_`
- `pv`

## Étapes d'exécution obligatoires

### 1. Extraire la version du build actuel
- Lire les lignes 25 à 45 de [app/build.gradle.kts](file:///D:/AndroidStudioProjects/ClientJetPack/app/build.gradle.kts).
- Extraire la version (par exemple `1.14.0.12`).

### 2. Identifier et indexer les modifications
- Exécuter `git status`.
- Ajouter tous les fichiers modifiés à l'index (si nécessaire) :
  ```powershell
  git add -A
  ```

### 3. Créer le commit
- Créer un commit avec le numéro de version comme message de commit (par exemple `1.14.0.12`) :
  ```powershell
  git commit -m "<VERSION>"
  ```

### 4. Gérer le tag `par_version_instaled` et le tag de version (`v<VERSION>`)
Puisque le tag `par_version_instaled` et le tag spécifique de la version (ex: `v1.14.0.12`) doivent pointer sur le nouveau commit :
- **Calculer la version précédente** (build version - 1, ex: `1.14.0.11` si la version actuelle est `1.14.0.12`).
- **Générer le journal des modifications** depuis la version précédente :
  - Vérifier si le tag de la version précédente existe (ex: `v1.14.0.11` ou `1.14.0.11`).
  - Si le tag précédent existe, récupérer les commits depuis celui-ci :
    ```powershell
    git log v1.14.0.11..HEAD --oneline
    ```
  - Si le tag de la version précédente n'existe pas, récupérer les commits depuis `par_version_instaled` (qui pointe sur le build d'avant la compilation en cours) :
    ```powershell
    git log par_version_instaled..HEAD --oneline
    ```
  - Utiliser la liste des commits ainsi récupérés comme message d'annotation du tag de version.
- **Supprimer le tag de version s'il existe déjà** (localement et à distance) pour l'écraser :
  ```powershell
  git tag -d v1.14.0.12
  git push github :refs/tags/v1.14.0.12
  git push origin :refs/tags/v1.14.0.12
  ```
- **Créer le tag de version annoté** avec la description des changements trouvés :
  ```powershell
  git tag -a v1.14.0.12 -m "Changements depuis v1.14.0.11 : <LISTE_COMMITS>"
  ```
- **Mettre à jour le tag `par_version_instaled`** (supprimer localement/distance et recréer localement sur le commit actuel) :
  ```powershell
  git tag -d par_version_instaled
  git push github :refs/tags/par_version_instaled
  git push origin :refs/tags/par_version_instaled
  git tag par_version_instaled
  ```

### 5. Pousser le commit et les tags
- Déterminer la branche active (ex: `maste`).
- Pousser le commit vers les dépôts distants :
  ```powershell
  git push github <BRANCH>
  git push origin <BRANCH>
  ```
- Pousser les tags mis à jour :
  ```powershell
  git push github par_version_instaled --force
  git push origin par_version_instaled --force
  git push github v1.14.0.12 --force
  git push origin v1.14.0.12 --force
  ```

### 6. Rapport de réussite
Afficher au développeur :
- Le message de commit (le numéro de version du build).
- Le nom du tag de version créé/écrasé (ex : `v1.14.0.12`).
- La description des changements générée (changelog depuis version - 1).
- Le statut des commandes Git (commit, suppression/création des tags, push vers les dépôts `github` et `origin`).
