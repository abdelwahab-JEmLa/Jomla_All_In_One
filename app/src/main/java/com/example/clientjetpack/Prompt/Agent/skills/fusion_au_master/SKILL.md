---
name: fusion_au_master
description: Fusionne automatiquement les modifications de la branche actuelle vers la branche master/main, gère les commits si nécessaire, pousse les changements, conserve la branche source intacte pour continuer le travail, et affiche un résumé complet des fichiers touchés depuis la création de la branche.
---

# Skill - Fusionner vers Master (fusion_au_master)

Ce skill instruit l'assistant sur la manière de fusionner proprement la branche Git actuelle vers la branche principale (`master` ou `main`), **sans supprimer la branche source**, et en affichant un résumé détaillé de tout ce qui a été fait.

## Déclencheurs (Triggers)
- `fusion_au_master`
- `fusion`
- `merge_master`

---

## Étapes d'exécution

### 1. Identifier la branche de travail actuelle
Exécuter `git branch --show-current` pour noter le nom exact de la branche en cours (ex: `feature/video-player-fix`).

### 2. Vérifier l'état de l'arbre de travail
S'assurer que toutes les modifications locales sont commitées.
```powershell
git status --short
```
Si des fichiers non commités existent, générer automatiquement un commit avec un message descriptif (résumant les fichiers modifiés) avant de continuer.

### 3. Générer le résumé "Depuis la création de la branche"
Avant de fusionner, collecter toutes les informations de la branche :

```powershell
# Point de divergence avec master
$mergeBase = git merge-base HEAD master

# Tous les commits depuis la création
git log "$mergeBase..HEAD" --oneline

# Tous les fichiers touchés depuis la création (liste unique)
git diff --name-only "$mergeBase" HEAD
```

Stocker ces informations pour les afficher dans le rapport final.

### 4. Récupérer les dernières mises à jour (fetch)
Fetcher depuis **tous les remotes** disponibles :
```powershell
git remote | ForEach-Object { git fetch $_ }
```

### 5. Identifier la branche principale
```powershell
git branch -r
```
Déterminer si le projet utilise `master` ou `main`.

### 6. Basculer sur la branche principale et mettre à jour
```powershell
git checkout master   # ou main
git remote | ForEach-Object { git pull $_ master }
```
> **Note** : Puller depuis tous les remotes garantit que la branche principale est à jour par rapport à `origin`, `github`, etc.

### 7. Fusionner la branche de travail
```powershell
git merge <nom_de_la_branche_de_travail>
```
- ✅ **Pas de conflits** → continuer
- ⚠️ **Conflits détectés** → afficher les fichiers en conflit, aider à les résoudre fichier par fichier avec les outils d'édition, puis commiter la résolution (`git commit -m "Merge: resolve conflicts"`)

### 8. Pousser les changements vers tous les dépôts distants
Itérer sur **tous les remotes** et pousser sur chacun d'eux :
```powershell
git remote | ForEach-Object { git push $_ master }   # ou main
```
> ⚠️ **Important** : Ne pas pousser uniquement vers `origin`. Ce projet (et similaires) possède plusieurs remotes (`origin`, `github`, etc.). Cette commande dynamique garantit que tous sont synchronisés.

### 9. Basculer sur la branche principale à la fin
La branche de travail **doit rester intacte**, mais le processus doit se terminer sur la branche principale (`master` ou `main`).

```powershell
# S'assurer d'être sur la branche principale pour terminer le processus
git checkout master   # ou main
```

### 10. Afficher le rapport final complet

À la fin, afficher un résumé structuré sous cette forme :

```
✅ Fusion réussie : <branche> → master

📋 Résumé de ce qui a été fait depuis la création de la branche :
──────────────────────────────────────────────────────
🔸 Commits :
  - <hash> <message du commit>
  - <hash> <message du commit>
  ...

📁 Fichiers touchés (<N> fichiers) :
  - chemin/vers/fichier1.kt   [modifié]
  - chemin/vers/fichier2.kt   [modifié]
  ...

💡 Branche "<branche>" conservée intacte.
   Branche principale (master/main) active.
──────────────────────────────────────────────────────
```
