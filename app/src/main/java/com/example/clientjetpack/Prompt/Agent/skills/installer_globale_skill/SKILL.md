---
name: installer_globale_skill
description: Use this skill to copy a local workspace skill to the global skills directory. Trigger this whenever the user requests "installe_<skill_name>", "install global <skill>", or asks to install a specific skill globally (e.g., "installe_t_").
---

# Skill - Installer un Skill Global (`installer_globale_skill`)

Ce skill instruit l'assistant sur la manière d'installer un skill local de l'espace de travail actuel vers le répertoire global des skills d'Antigravity (AGY), de résoudre les conflits potentiels, et de nettoyer la version locale.

## Déclencheurs (Triggers)
L'utilisateur peut déclencher ce skill en tapant, par exemple :
- `installe_t_`
- `installe_<nom_du_skill>`
- `install global <nom_du_skill>`

## Étapes d'exécution obligatoires

Lorsque ce skill est déclenché par l'utilisateur :

1. **Identifier le nom du skill ciblé** :
   Déduisez le `<nom_du_skill>` à partir de la requête (par exemple, si la requête est `installe_t_`, le nom est `t_`).

2. **Demander la permission globale** :
   Utilisez l'outil `ask_permission` pour demander les permissions d'écriture (`write_file`) sur les deux répertoires globaux suivants :
   - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills`
   - `C:\Users\Abou Mohamed\.gemini\config\skills`

3. **Copier le skill au niveau global et supprimer le dossier local** :
   Utilisez l'outil de ligne de commande (`run_command` avec PowerShell) pour :
   - Supprimer au préalable les destinations globales si elles existent (pour éviter l'imbrication accidentelle de dossiers de type `t_/t_`).
   - Copier tout le dossier du skill local vers les deux répertoires globaux.
   - Supprimer le dossier local après la copie pour éviter les doublons dans l'affichage du CLI (afin que le skill apparaisse clairement sous la section "Global skills" et non pas dans "Built-in skills").

   Exemple de commande PowerShell à exécuter :
   ```powershell
   $skillName = "<nom_du_skill>"
   $source = "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\.agents\skills\$skillName"
   $dest1 = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\$skillName"
   $dest2 = "C:\Users\Abou Mohamed\.gemini\config\skills\$skillName"

   if (Test-Path $source) {
       # Nettoyage prealable des destinations pour eviter l'imbrication des dossiers
       if (Test-Path $dest1) { Remove-Item -Path $dest1 -Recurse -Force -ErrorAction SilentlyContinue }
       if (Test-Path $dest2) { Remove-Item -Path $dest2 -Recurse -Force -ErrorAction SilentlyContinue }

       # Copie propre vers le global
       Copy-Item -Path $source -Destination $dest1 -Recurse -Force
       Copy-Item -Path $source -Destination $dest2 -Recurse -Force
       Write-Host "Le skill $skillName a ete copie avec succes dans les repertoires globaux."

       # Suppression du skill local pour eviter l'affichage en doublon (Workspace / Built-in)
       Remove-Item -Path $source -Recurse -Force
       Write-Host "Le dossier local a ete supprime pour prioriser l'affichage global."
    } else {
        # Cas de création ex-nihilo (le skill n'existe pas dans le dossier local) :
        # L'assistant doit :
        # 1. Générer le contenu du SKILL.md avec un frontmatter YAML valide
        #    (inféré à partir de la description du besoin de l'utilisateur).
        # 2. Créer les répertoires et écrire les fichiers dans les deux destinations globales :
        #    - C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\<nom_du_skill>\SKILL.md
        #    - C:\Users\Abou Mohamed\.gemini\config\skills\<nom_du_skill>\SKILL.md
        # 3. Mettre à jour le fichier d'aide global h_/SKILL.md pour ajouter une entrée dans le tableau.
        Write-Host "Le skill $skillName n'existe pas localement. Creation ex-nihilo en cours..."
    }
   ```

4. **Confirmer à l'utilisateur** :
   Une fois la copie terminée, répondez de manière concise à l'utilisateur pour confirmer que le skill a bien été installé globalement, et précisez que la copie locale a été nettoyée pour un affichage propre dans `agy skills`.
