---
name: fusion_app_tahfide
description: Fusionne automatiquement les modifications de la branche actuelle vers la branche de production/tahfid (app_tahfid_quran), gère les commits si nécessaire, pousse les changements, conserve la branche source intacte, et affiche un résumé complet des modifications depuis le point de divergence.
---

# Skill - Fusionner vers app_tahfid_quran (fusion_app_tahfide)

Ce skill instruit l'assistant sur la manière de fusionner proprement la branche Git actuelle vers la branche de production `app_tahfid_quran`, **sans supprimer la branche source**, et en affichant un résumé détaillé de tout ce qui a été fait.

## Déclencheurs (Triggers)
- `fusion_app_tahfide`
- `fusion_tahfid`
- `merge_tahfid`
- `fusion_a_tahfid`

---

## Étapes d'exécution

### 1. Identifier la branche de travail actuelle
Exécuter `git branch --show-current` pour noter le nom exact de la branche en cours (ex: `todo/fix-fab-play-video-and-central-params`).

### 2. Vérifier l'état de l'arbre de travail
S'assurer que toutes les modifications locales sont commitées.
```powershell
git status --short
```
Si des fichiers non commités existent, générer automatiquement un commit avec un message descriptif (résumant les fichiers modifiés) avant de continuer.

### 3. Générer le résumé "Depuis la création de la branche"
Avant de fusionner, collecter toutes les informations de la branche :

```powershell
# Point de divergence avec app_tahfid_quran
$mergeBase = git merge-base HEAD app_tahfid_quran

# Tous les commits depuis la création
git log "$mergeBase..HEAD" --oneline

# Tous les fichiers touchés depuis la création (liste unique)
git diff --name-only "$mergeBase" HEAD
```

Stocker ces informations pour les afficher dans le rapport final.

### 4. Récupérer les dernières mises à jour (fetch)
```powershell
git fetch origin
```

### 5. Basculer sur la branche app_tahfid_quran et mettre à jour
```powershell
git checkout app_tahfid_quran
git pull origin app_tahfid_quran
```
*Note : Si Git affiche des avertissements non bloquants lors du basculement (ex: `warning: unable to rmdir 'android_skills_temp': Directory not empty`), ignorez-les si la commande se termine avec succès.*

### 6. Fusionner la branche de travail
```powershell
git merge <nom_de_la_branche_de_travail>
```
- ✅ **Pas de conflits** → continuer
- ⚠️ **Conflits détectés** → afficher les fichiers en conflit, aider à les résoudre fichier par fichier avec les outils d'édition, puis commiter la résolution (`git commit -m "Merge: resolve conflicts"`)

### 7. Pousser les changements vers le dépôt distant
```powershell
git push origin app_tahfid_quran
```

### 8. Basculer sur la branche app_tahfid_quran à la fin
La branche de travail **doit rester intacte**, mais le processus doit se terminer sur la branche de production `app_tahfid_quran`.

```powershell
# S'assurer d'être sur la branche app_tahfid_quran pour terminer le processus
git checkout app_tahfid_quran
```

### 9. Afficher le rapport final complet

À la fin, afficher un résumé structuré sous cette forme :

```
✅ Fusion réussie : <branche> → app_tahfid_quran

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
   Branche de production (app_tahfid_quran) active.
──────────────────────────────────────────────────────
```
