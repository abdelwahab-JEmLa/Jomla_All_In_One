---
name: git_branch_todos
description: Scanne le codebase pour detecter les TODOs, cree une branche Git au nom resume des TODOs et associe la liste des TODOs en description de branche.
---

# Skill - Git Branch from TODOs (`git_branch_todos`)

Ce skill permet d'automatiser l'isolation des tâches de correction Git basées sur les TODO restants.

## Triggers
- `git_branch_todos`
- `creer_sub_branche_todo`
- `b_t`
- `gbt_`
- `sub_branch_todo`
- `improv_ git_branch_todos`
- `t_bra`

## Étapes obligatoires

### 0. 🚀 Push de la branche actuelle (avant de créer la nouvelle)
Avant toute opération, pousser les changements de la branche courante pour ne rien perdre.

Exécuter le skill `p_` (push_tagged) en séquence :
```powershell
git status
git tag --list
```
- Si des modifications non commitées existent → commit + tag + push (`p_` complet).
- Si la branche est déjà propre/synchronisée → simple `git push github <branche_actuelle>` pour s'assurer que le remote est à jour.

> ⚠️ Ne pas continuer si le push échoue. Afficher l'erreur et demander confirmation.

---

### 1. Rechercher les TODOs dans le projet
Utiliser la commande PowerShell suivante pour inclure le chemin complet (`Path`) dès la première recherche, afin d'éviter un second appel de localisation :

```powershell
Get-ChildItem -Recurse -Include "*.kt","*.java","*.xml" "app\src" |
  Select-String -Pattern "(?i)//\s*TODO" |
  Select-Object Path, LineNumber, Line |
  Format-Table -AutoSize -Wrap
```

> ⚠️ Utiliser `Path` (chemin absolu complet) et non `Filename` (nom seul) pour éviter un lookup supplémentaire.
> ⚠️ Le filtre strict `(?i)//\s*TODO` évite de capturer les méthodes comme `toDouble()`.

---

### 2. Lire le contexte autour de chaque TODO
Pour chaque TODO trouvé, lire les ~10 lignes autour via `view_file` (StartLine - 5 / EndLine + 5) afin de :
- Comprendre le problème réel
- Rédiger une description de branche riche et précise

---

### 3. Générer le nom de branche
Créer un nom de branche court et "slugifié" à partir du résumé des TODOs trouvés.

**Règles de slugification :**
- Minuscules uniquement
- Remplacer espaces, underscores et caractères spéciaux par `-`
- Supprimer les accents (ex: `éèà` → `eea`)
- Maximum 60 caractères au total
- Préfixe obligatoire : `todo/`

Exemple : `todo/fix-switch-devmode-state-not-following`

---

### 4. Vérifier que la branche n'existe pas déjà
```powershell
git branch --list "todo/<nom>"
```
- Si elle **n'existe pas** → passer à l'étape suivante.
- Si elle **existe déjà** → ajouter un suffixe numérique (ex: `-2`, `-3`) ou demander à l'utilisateur.

---

### 5. Créer et basculer sur la branche
```powershell
git checkout -b <nom_de_branche>
```

---

### 6. Définir la description de la branche Git
Enregistrer la liste des TODOs détaillés dans la configuration Git de la branche via :
```powershell
git config branch.<nom_de_branche>.description "<liste_des_todos>"
```

La description doit inclure pour chaque TODO :
- Fichier (chemin relatif court)
- Numéro de ligne
- Texte du TODO
- Contexte métier en 1 phrase (déduit des lignes voisines lues à l'étape 2)

---

### 7. 🚀 Push de la nouvelle branche vers le remote
Après création de la branche et enregistrement de la description, publier immédiatement la branche sur le remote :
```powershell
git push github <nom_de_branche>
```
- Si le push réussit → continuer vers la confirmation.
- Si le push échoue → afficher l'erreur, mais ne pas bloquer : indiquer que la branche existe localement et que le push peut être retentée avec `p_`.

---

### 8. Confirmer à l'utilisateur
Afficher un tableau récapitulatif avec :
- ✅ **Push initial** : statut du push de la branche source (étape 0)
- 🌿 **Nom de la branche créée**
- 📋 **Liste des TODOs** (fichier cliquable, ligne, description)
- 📝 **Description Git enregistrée**
- ✅ **Push final** : statut du push de la nouvelle branche (étape 7)
