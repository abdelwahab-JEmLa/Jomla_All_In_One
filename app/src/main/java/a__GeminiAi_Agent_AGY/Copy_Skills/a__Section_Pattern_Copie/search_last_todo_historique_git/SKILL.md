---
name: search_last_todo_historique_git
description: Searches the Git history for a specific TODO text to display its original content and the exact time/date it was added or removed.
---

# Skill - Search Last TODO Historique Git (`search_last_todo_historique_git`)

Ce skill permet à l'assistant de fouiller l'historique local Git d'un fichier pour y retrouver un ancien TODO et sa date exacte de création/modification.

## Trigger Phrases
- "search_last_todo_historique_git"
- "t_git"

## Instructions d'exécution
1. **Identifier la cible** : Identifiez le fichier concerné et le texte (ou une partie du texte) du TODO recherché.
2. **Rechercher la date** : Utilisez la commande PowerShell suivante pour retrouver la date du TODO via Git :
   ```powershell
   git log -S "<texte_du_todo_ou_partie_du_texte>" --format="%cd" --date=iso "<chemin_du_fichier>"
   ```
3. **Rechercher le contenu (Optionnel)** : Utilisez `git log -p -S "<texte_du_todo>" "<chemin_du_fichier>"` pour lire le diff et retrouver le contenu exact du TODO si nécessaire.
4. **Affichage** : Présentez les résultats à l'utilisateur de manière claire et concise (ex: "Le TODO '...' a été écrit le Mercredi 1er Avril à 15h07").
