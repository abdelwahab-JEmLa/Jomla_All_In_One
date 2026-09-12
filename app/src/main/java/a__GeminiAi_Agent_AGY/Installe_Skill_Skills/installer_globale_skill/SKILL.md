---
name: installer_globale_skill
description: Use this skill to copy a local workspace skill to the global skills directory. Trigger this whenever the user requests "installe_<skill_name>", "install global <skill>", or asks to install a specific skill globally (e.g., "installe_t_").
---

# Skill - Installer un Skill Global (`installer_globale_skill`)

Ce skill instruit l'assistant sur la manière d'installer un skill local de l'espace de travail actuel vers le répertoire global des skills d'Antigravity (AGY), de créer dynamiquement des sections sous `skills_sections` (`a_parSections`), de résoudre les conflits potentiels, et de nettoyer la version locale.

## Déclencheurs (Triggers)
L'utilisateur peut déclencher ce skill en tapant, par exemple :
- `installe_t_`
- `installe_<nom_du_skill>`
- `install global <nom_du_skill>`
- `installe_<nom_du_skill> au cop_s` / `install global <nom_du_skill> vers copy_skills`
- `installe_<nom_du_skill> dans la section <NomSection>` / `creer section <NomSection>`

## Étapes d'exécution obligatoires

Lorsque ce skill est déclenché par l'utilisateur :

1. **Identifier le nom du skill ciblé et les options de destination** :
   - Déduisez le `<nom_du_skill>` à partir de la requête (par exemple, si la requête est `installe_t_`, le nom est `t_`).
   - **Règle Sections (`skills_sections` / `a_parSections`)** : Vérifiez si la requête spécifie une section (ex: `cop_s`, `copy_skills` ou `dans la section <NomSection>`).

2. **Demander la permission globale** :
   Utilisez l'outil `ask_permission` pour demander les permissions d'écriture (`write_file`) sur les répertoires globaux concernés :
   - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills`
   - `C:\Users\Abou Mohamed\.gemini\config\skills`
   - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\a_parSections\<NomSection>` (si une section est spécifiée)

3. **Créer la section sous `skills_sections` (`a_parSections`) si demandée** :
   Si l'utilisateur demande de créer ou d'installer dans une section sous `skills_sections` / `a_parSections` (ex: `dans la section UI_Skills` ou `au cop_s`) :
   - Créer automatiquement le dossier de section s'il n'existe pas :
     `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\a_parSections\<NomSection>\`

4. **Copier le skill aux répertoires cibles et supprimer le dossier local** :
   Utilisez l'outil de ligne de commande (`run_command` avec PowerShell) pour :
   - Copier le skill dans `config/skills/<nom_du_skill>` et `antigravity-cli/skills/<nom_du_skill>`.
   - Si une section est spécifiée (`<NomSection>`), copier également le skill dans `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\a_parSections\<NomSection>\<nom_du_skill>\`.
   - Mettre à jour `h_/SKILL.md` pour y déclarer la nouvelle section et/ou le nouveau skill sous `## 📂 3. Skills Organisés par Sections (a_parSections)`.
   - Supprimer le dossier local après la copie pour éviter les doublons dans l'affichage du CLI.

   Exemple de commande PowerShell à exécuter :
   ```powershell
   $skillName = "<nom_du_skill>"
   $sectionName = "<NomSection>" # ex: Copy_Skills, UI_Skills, etc.
   $source = "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\.agents\skills\$skillName"
   $dest1 = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\$skillName"
   $dest2 = "C:\Users\Abou Mohamed\.gemini\config\skills\$skillName"
   $destSection = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\a_parSections\$sectionName\$skillName"

   if (Test-Path $source) {
       if (Test-Path $dest1) { Remove-Item -Path $dest1 -Recurse -Force -ErrorAction SilentlyContinue }
       if (Test-Path $dest2) { Remove-Item -Path $dest2 -Recurse -Force -ErrorAction SilentlyContinue }

       Copy-Item -Path $source -Destination $dest1 -Recurse -Force
       Copy-Item -Path $source -Destination $dest2 -Recurse -Force
       Write-Host "Le skill $skillName a ete copie avec succes dans les repertoires globaux."

       if ($sectionName) {
           $sectionDir = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\a_parSections\$sectionName"
           if (-not (Test-Path $sectionDir)) { New-Item -ItemType Directory -Path $sectionDir -Force }
           if (Test-Path $destSection) { Remove-Item -Path $destSection -Recurse -Force -ErrorAction SilentlyContinue }
           Copy-Item -Path $source -Destination $destSection -Recurse -Force
           Write-Host "Le skill $skillName a egalement ete installe dans la section a_parSections/$sectionName."
       }

       Remove-Item -Path $source -Recurse -Force
       Write-Host "Le dossier local a ete supprime pour prioriser l'affichage global."
    } else {
        Write-Host "Le skill $skillName n'existe pas localement. Creation ex-nihilo en cours..."
    }
   ```

5. **Valider et corriger le frontmatter YAML du SKILL.md** :
   Après la copie, vérifiez obligatoirement que chaque `SKILL.md` commence par un bloc frontmatter YAML valide (`name` et `description`).

6. **Confirmer à l'utilisateur** :
   Confirmer concisement l'installation globale et dans la section demandée, puis préciser la mise à jour du catalogue `h_`.
