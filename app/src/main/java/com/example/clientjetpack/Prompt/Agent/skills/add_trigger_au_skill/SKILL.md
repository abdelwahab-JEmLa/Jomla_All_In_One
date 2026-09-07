---
name: add_trigger_au_skill
description: Ajoute dynamiquement un trigger à n'importe quel skill global par résolution de nom partiel, l'ajoute à la liste des Trigger Phrases, renomme le dossier et met à jour le frontmatter et l'aide. Syntaxe : /add_trigger_au_skill <nom_partiel_skill> <trigger>.
---

# Skill - Add Trigger to Any Skill (`add_trigger_au_skill`)

Ce skill permet d'ajouter dynamiquement un trigger à n'importe quel skill global,
en résolvant le skill cible par nom partiel, puis en renommant son dossier pour refléter le nouveau trigger.

## Triggers
- `tig_b_t_<term>`
- `add_trigger_au_skill`
- `add_tig`

## Étapes obligatoires

1. **Parser la commande** :
   - Forme `/add_trigger_au_skill <nom_partiel_skill> <trigger>` ou `add_tig <nom_partiel_skill> <trigger>` :
     → `<nom_partiel_skill>` = identifiant partiel du skill cible (ex: `impro`, `git_b`, `zip`).
     → `<trigger>` = le mot-clé à ajouter (ex: `imp_`, `z_fast`).
   - Forme legacy `tig_b_t_<term>` :
     → skill cible = `git_branch_todos`, trigger = `<term>`.

2. **Résoudre le skill cible** :
   - Lister les dossiers dans `C:\Users\Abou Mohamed\.gemini\config\skills\`.
   - Trouver le dossier dont le nom contient `<nom_partiel_skill>` (case-insensitive, match partiel suffit).
   - Si plusieurs correspondances → afficher la liste et demander confirmation.
   - Si aucune → signaler l'erreur.

3. **Ajouter le trigger dans les 2 chemins globaux** :
   - `C:\Users\Abou Mohamed\.gemini\config\skills\<skill_résolu>\SKILL.md`
   - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\<skill_résolu>\SKILL.md` (si existe)
   - Insérer `- "<trigger>"` dans la section `## Trigger Phrases` ou `## Triggers`.
   - Mettre à jour le champ `name:` dans le frontmatter YAML pour qu'il corresponde au nouveau nom de dossier résolu (étape 4).

4. **Renommer le dossier du skill** :
   - Nouveau nom du dossier : `<ancien_nom>_<trigger>` (ex: `learn_improve_skill_depuit_convesation_imp_`).
   - Exécuter dans les 2 chemins globaux :
     ```powershell
     Rename-Item -Path "C:\Users\Abou Mohamed\.gemini\config\skills\<ancien_nom>" -NewName "<nouveau_nom>"
     Rename-Item -Path "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\<ancien_nom>" -NewName "<nouveau_nom>"
     ```
   - Note : si le trigger contient des caractères invalides pour Windows (`>`, `<`, `|`, etc.), les remplacer par `_` dans le nom de dossier.

5. **Mettre à jour le catalogue d'aide `h_`** :
   - Insérer le trigger dans `h_/SKILL.md` sur la ligne du skill cible, et mettre à jour le lien hypertexte du fichier pour qu'il pointe vers le nouveau dossier (`<nouveau_nom>/SKILL.md`).

6. **Signaler le succès** : afficher l'ancien nom, le nouveau nom de dossier, et le trigger ajouté.
