---
name: p_skills
description: Synchronise les skills entre AGY global et le projet Android. Vide la destination puis copie l'ensemble des dossiers par section (Mode direct: p_sk / Mode inverse: revers_p_skill). Triggered by p_skills, puch_skills, push_skills, p_ski, p_sk, revers_p_skill, reverse_p_skills, reverse_p_sk, r_p_sk, rev_p_sk, rev_p_ski, reverse_c_sk, rev_c_sk.
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

### Mode Inverse Global (Android ➡️ AGY Global)
- `revers_p_skill`
- `reverse_p_skills`
- `reverse_p_sk`
- `r_p_sk`
- `rev_p_sk`
- `rev_p_ski`

### Mode Inverse Ciblé (Section Copy_Skills uniquement)
- `reverse_c_sk`
- `rev_c_sk`

---

## Emplacements

- **Source / Destination Globale AGY** :
  - `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills`
  - `C:\Users\Abou Mohamed\.gemini\config\skills`
- **Source / Destination Projet Android** : `D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY\`

---

## Étapes d'exécution

### 1. Mode Direct (`p_skills` / `p_sk` / `push_skills`)

```powershell
$source = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills"
$dest   = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"

if (Test-Path $source) {
    if (-not (Test-Path $dest)) {
        New-Item -ItemType Directory -Path $dest -Force | Out-Null
    }
    $sections = Get-ChildItem -Path $source -Directory
    foreach ($sec in $sections) {
        $targetSec = Join-Path $dest $sec.Name
        if (Test-Path $targetSec) {
            Get-ChildItem -Path $targetSec | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
        } else {
            New-Item -ItemType Directory -Path $targetSec -Force | Out-Null
        }
        Get-ChildItem -Path $sec.FullName | ForEach-Object {
            Copy-Item -Path $_.FullName -Destination (Join-Path $targetSec $_.Name) -Recurse -Force
        }
        Write-Host "✅ $($sec.Name) copié vers Android."
    }
    Write-Host "🎉 Synchronisation Directe terminée vers : $dest"
} else {
    Write-Host "⚠️ Source introuvable : $source"
}
```

---

### 2. Mode Inverse Global (`revers_p_skill` / `rev_p_sk` / `rev_p_ski`)

Copie l'ensemble des sections depuis Android vers les deux dossiers globaux AGY (`config\skills` et `antigravity-cli\skills`) sans risque d'imbrication de dossiers.

```powershell
$source       = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY"
$globalCli    = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills"
$globalConfig = "C:\Users\Abou Mohamed\.gemini\config\skills"

if (Test-Path $source) {
    $sections = Get-ChildItem -Path $source -Directory
    foreach ($gDest in @($globalCli, $globalConfig)) {
        if (-not (Test-Path $gDest)) {
            New-Item -ItemType Directory -Path $gDest -Force | Out-Null
        }
        foreach ($sec in $sections) {
            $targetSec = Join-Path $gDest $sec.Name
            if (Test-Path $targetSec) {
                Get-ChildItem -Path $targetSec | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
            } else {
                New-Item -ItemType Directory -Path $targetSec -Force | Out-Null
            }
            Get-ChildItem -Path $sec.FullName | ForEach-Object {
                Copy-Item -Path $_.FullName -Destination (Join-Path $targetSec $_.Name) -Recurse -Force
            }
        }
        Write-Host "✅ Synchronisé vers : $gDest"
    }
    Write-Host "🎉 Synchronisation Inverse terminée avec succès !"
} else {
    Write-Host "⚠️ Source introuvable : $source"
}
```

---

### 3. Mode Inverse Ciblé (`reverse_c_sk` / `rev_c_sk`)

Synchronise uniquement la section `Copy_Skills` depuis Android vers AGY Global.

```powershell
$sourceSec    = "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\a__GeminiAi_Agent_AGY\Copy_Skills"
$globalCli    = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\Copy_Skills"
$globalConfig = "C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills"

if (Test-Path $sourceSec) {
    foreach ($targetSec in @($globalCli, $globalConfig)) {
        if (Test-Path $targetSec) {
            Get-ChildItem -Path $targetSec | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
        } else {
            New-Item -ItemType Directory -Path $targetSec -Force | Out-Null
        }
        Get-ChildItem -Path $sourceSec | ForEach-Object {
            Copy-Item -Path $_.FullName -Destination (Join-Path $targetSec $_.Name) -Recurse -Force
        }
        Write-Host "✅ Copy_Skills synchronisé vers : $targetSec"
    }
}
```

---

### 4. Confirmation
Afficher à l'utilisateur un résumé clair de l'opération de synchronisation et la liste des dossiers mis à jour.

