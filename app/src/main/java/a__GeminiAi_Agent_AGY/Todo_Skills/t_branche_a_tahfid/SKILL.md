---
name: t_branche_a_tahfid
description: Scanne le codebase pour détecter les TODOs, crée une branche Git au nom résumé des TODOs, crée un fichier de résumé dans app_tahfid_todos/<resume_todos>.md (ou big_todo_jomla_app/ pour ClientJetPack), commite ce fichier, pousse la branche vers le remote, et affiche un rapport détaillé.
---

# Skill - Git Branch & TODOs File for Tahfid (`t_branche_a_tahfid`)

Ce skill permet d'isoler une tâche basée sur les TODOs en créant une branche Git dédiée et en documentant automatiquement les TODOs dans un fichier de résumé au format Markdown dans `app_tahfid_todos/` (ou `big_todo_jomla_app/` si l'application active est ClientJetPack).

## Triggers
- `t_branche_a_tahfid`
- `tb_tahfid`
- `t_bt_`
- `tb_`

---

## Étapes obligatoires d'exécution

### 1. Sauvegarder et Pousser la branche actuelle (Sécurité)
Avant toute modification, s'assurer que la branche active est propre et synchronisée avec le remote :
```powershell
git status --short
```
- S'il y a des modifications locales non commitées, réaliser un commit automatique avec le skill `p_` (push_tagged) ou générer un commit avec un message descriptif.
- Pousser la branche courante vers origin.

---

### 2. Scanner les TODOs dans le projet
Exécuter la recherche de TODOs via PowerShell :
```powershell
Get-ChildItem -Recurse -Include "*.kt","*.java","*.xml" "app\src" |
  Select-String -Pattern "(?i)//\s*TODO" |
  Select-Object Path, LineNumber, Line |
  Format-Table -AutoSize -Wrap
```
*(Remarque : Privilégier le répertoire principal `app/src` ou le répertoire actif de l'espace de travail).*

Pour chaque TODO identifié :
- Lire le contexte immédiat (~10 lignes autour) à l'aide de `view_file` pour comprendre le but recherché.

---

### 3. Générer le nom de branche (Slug)
Déterminer un nom de branche concis décrivant les TODOs trouvés.
- Convertir en minuscules.
- Remplacer les caractères spéciaux, espaces et underscores par `-`.
- Supprimer les accents.
- Longueur max : 50-60 caractères.
- Préfixe requis : `todo/` (ex: `todo/resolve-tahfid-observ-dialog`).

---

### 4. Créer et basculer sur la nouvelle branche
Vérifier si la branche existe déjà, puis la créer :
```powershell
git checkout -b <nom_de_branche>
```

---

### 5. Générer le fichier de résumé des TODOs
Déterminer le dossier cible pour le résumé selon le projet actif :
- Si le projet actif est **ClientJetPack** (client app) : le dossier cible est `big_todo_jomla_app`.
- Sinon (ex: Tahfid Quran) : le dossier cible est `app_tahfid_todos`.

Créer le dossier cible s'il n'existe pas.
Générer et écrire un fichier de résumé Markdown à l'adresse suivante :
`<dossier_cible>/<nom_de_branche_sans_prefixe>.md`

Le contenu du fichier doit suivre cette structure :
```markdown
# Résumé des TODOs - Branche <nom_de_branche>

Date : <Date Actuelle>

## Liste des TODOs détectés
- [ ] **Fichier** : [NomFichier](file:///<chemin_absolu_du_fichier>#L<Ligne>) (Ligne <Ligne>)
  - **Texte** : `<Contenu du TODO>`
  - **Contexte** : <Explication courte du contexte métier déduit à l'étape 2>
```

---

### 6. Valider le fichier et commiter dans Git
```powershell
git add <dossier_cible>/
git commit -m "docs: add TODOs summary file for <nom_de_branche>"
```

---

### 7. Enregistrer la description Git de la branche
```powershell
git config branch.<nom_de_branche>.description "Résumé des TODOs sauvegardé dans <dossier_cible>/<nom_de_branche_sans_prefixe>.md"
```

---

### 8. Pousser la branche vers le remote
```powershell
git push origin <nom_de_branche>
```

---

### 9. Présenter le rapport final à l'utilisateur
Afficher un récapitulatif détaillé incluant :
- Le nom de la branche créée.
- Le lien vers le fichier de résumé généré : [Fichier de résumé](file:///<chemin_absolu_du_résumé>).
- La liste des fichiers touchés.
- Le statut du push distant.

---

### 10. Fin de l'exécution
L'exécution s'arrête après la présentation du rapport final. Le skill `t_` n'est PAS lancé automatiquement pour laisser l'utilisateur réviser la branche et le fichier de résumé créé.
