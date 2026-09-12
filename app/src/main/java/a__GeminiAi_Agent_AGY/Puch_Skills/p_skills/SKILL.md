---
name: p_skills
description: Vide le dossier AGY Android puis copie tous les dossiers de skills depuis C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills vers D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY. Déclenché par p_skills, puch_skills, push_skills, p_ski, p_sk.
---

# Skill — Push Skills vers AGY Android (p_skills)

Ce skill **vide** d'abord le dossier de référence AGY du projet Android, puis y copie tous les dossiers de skills situés dans `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills`.

## Déclencheurs (Triggers)
- `p_skills`
- `puch_skills`
- `push_skills`
- `p_ski`
- `p_sk`

## Source copiée
`C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills`

## Destination
`D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY\`

---

## Étapes d'exécution

### 1. Supprimer le contenu existant dans la destination

```powershell
$dest = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"

# Supprimer tout le contenu existant
if (Test-Path $dest) {
    Get-ChildItem -Path $dest | ForEach-Object {
        Remove-Item -Path $_.FullName -Recurse -Force
    }
    Write-Host "🗑️ Dossier destination vidé."
} else {
    New-Item -ItemType Directory -Path $dest -Force | Out-Null
    Write-Host "📁 Dossier destination créé."
}
```

### 2. Copier les dossiers depuis la source

```powershell
$source = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills"
$dest   = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"

if (Test-Path $source) {
    $items = Get-ChildItem -Path $source
    foreach ($item in $items) {
        Copy-Item -Path $item.FullName -Destination (Join-Path $dest $item.Name) -Recurse -Force
        Write-Host "✅ $($item.Name) copié."
    }
    Write-Host ""
    Write-Host "🎉 Dossiers copiés avec succès vers : $dest"
} else {
    Write-Host "⚠️ Source introuvable : $source"
}
```

### 3. Confirmer à l'utilisateur
Afficher le résumé des dossiers copiés et le chemin destination.


