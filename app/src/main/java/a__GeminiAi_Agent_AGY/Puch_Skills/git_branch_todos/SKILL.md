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
- `t_bra`

## Étapes obligatoires
1. **Rechercher les TODOs dans le projet** :
   Utiliser la commande PowerShell de recherche des TODOs avec un filtre strict (ex: `-Pattern "(?i)//\s*TODO"`) pour éviter de capturer par erreur d'autres occurrences du mot "todo" (comme les méthodes `toDouble()`).
2. **Générer le nom de branche** :
   Créer un nom de branche court et "slugifié" (ex: `todo/fix-unresolved-ref-image-displaye`) à partir du résumé des TODOs trouvés.
3. **Créer et basculer sur la branche** :
   Exécuter `git checkout -b <nom_de_branche>`.
4. **Définir la description de la branche Git** :
   Enregistrer la liste des TODOs détaillés (fichiers, lignes, descriptions) dans la configuration Git de la branche via :
   `git config branch.<nom_de_branche>.description "<liste_des_todos>"`
5. **Confirmer à l'utilisateur** la création et afficher les détails.
