---
name: p_skills
description: Synchronise les skills entre AGY global et le projet Android. Vide la destination puis copie l'ensemble des dossiers par section (Mode direct: p_sk / Mode inverse: revers_p_skill). Triggered by p_skills, puch_skills, push_skills, p_ski, p_sk, revers_p_skill, reverse_p_skills, r_p_sk, rev_p_sk.
---

# Skill — Synchronisation des Skills AGY (p_skills / revers_p_skill)

Ce skill gère la synchronisation bidirectionnelle des skills entre le répertoire global des skills AGY (`antigravity-cli\skills` et `config\skills`) et le répertoire de référence du projet Android `a__GeminiAi_Agent_AGY`.

## Déclencheurs (Triggers)

### Mode Direct (AGY Global ➡️ Android)
- `p_skills`
- `puch_skills`
- `push_skills`
- `p_ski`
- `p_sk`

### Mode Inverse (Android ➡️ AGY Global)
- `revers_p_skill`
- `reverse_p_skills`
- `r_p_sk`
- `rev_p_sk`

---

## Emplacements

- **Source / Destination Globale AGY** :
  - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills`
  - `C:\Users\Abou Mohamed\.gemini\config\skills`
- **Source / Destination Projet Android** : `D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY\`

---

## Étapes d'exécution

### 1. Mode Direct (`p_skills` / `p_sk` / `push_skills`)

#### Étape A : Vider le dossier destination Android
```powershell
$dest = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"
if (Test-Path $dest) {
    Get-ChildItem -Path $dest | Remove-Item -Recurse -Force
    Write-Host "🗑️ Dossier destination Android vidé."
} else {
    New-Item -ItemType Directory -Path $dest -Force | Out-Null
    Write-Host "📁 Dossier destination Android créé."
}
```

#### Étape B : Copier depuis AGY Global vers Android
```powershell
$source = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills"
$dest   = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"

if (Test-Path $source) {
    $items = Get-ChildItem -Path $source
    foreach ($item in $items) {
        Copy-Item -Path $item.FullName -Destination (Join-Path $dest $item.Name) -Recurse -Force
        Write-Host "✅ $($item.Name) copié."
    }
    Write-Host "🎉 Synchronisation Directe terminée vers : $dest"
} else {
    Write-Host "⚠️ Source introuvable : $source"
}
```

---

### 2. Mode Inverse (`revers_p_skill` / `reverse_p_skills` / `r_p_sk`)

#### Étape A : Vider les répertoires de skills AGY Global
```powershell
$globalCli    = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills"
$globalConfig = "C:\Users\Abou Mohamed\.gemini\config\skills"

foreach ($gDest in @($globalCli, $globalConfig)) {
    if (Test-Path $gDest) {
        Remove-Item -Path "$gDest\*" -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "🗑️ Dossier global AGY skills vidé : $gDest"
    } else {
        New-Item -ItemType Directory -Path $gDest -Force | Out-Null
        Write-Host "📁 Dossier global AGY skills créé : $gDest"
    }
}
```

#### Étape B : Copier les dossiers de section depuis Android (`a__GeminiAi_Agent_AGY`) vers AGY Global
```powershell
$source = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"
$globalCli    = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills"
$globalConfig = "C:\Users\Abou Mohamed\.gemini\config\skills"

if (Test-Path $source) {
    $items = Get-ChildItem -Path $source
    foreach ($item in $items) {
        Copy-Item -Path $item.FullName -Destination (Join-Path $globalCli $item.Name) -Recurse -Force
        Copy-Item -Path $item.FullName -Destination (Join-Path $globalConfig $item.Name) -Recurse -Force
        Write-Host "✅ Section copiée : $($item.Name)"
    }
    Write-Host "🎉 Synchronisation Inverse terminée vers AGY Global."
} else {
    Write-Host "⚠️ Source introuvable : $source"
}
```

---

### 3. Confirmation
Afficher à l'utilisateur un résumé clair de l'opération de synchronisation (mode direct ou inverse) et la liste des dossiers copiés.

