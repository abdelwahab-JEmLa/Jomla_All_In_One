# Skill - PC Cleaner (clean_pc)

This skill instructs the assistant on how to act as a PC Maintenance Specialist, cleaning up temporary files, Windows Update download caches, system logs, and the Recycle Bin on the host Windows machine to free up disk space.

---

## Trigger Phrases
- `clean_pc`
- `nettoyer_pc`
- `pc_clean`
- `cleanup_pc`
- `cl_p` (Nettoyage effectif)
- `cl_e` (Estimation/Affichage seul sans suppression)
- `cl_s` (Statut de stockage uniquement)

---

## Steps to Execute

### 1. Execute System Cleanup or Estimation
Depending on the trigger phrase:
- If the trigger phrase is `cl_e`, run the script below with `$Mode = 'estimate'` to estimate and display files to be cleaned without deleting them.
- If the trigger phrase is `cl_p`, `clean_pc`, `nettoyer_pc`, `pc_clean`, or `cleanup_pc`, run the script with `$Mode = 'clean'` to perform the actual deletion.
- If the trigger phrase is `cl_s`, run the script with `$Mode = 'status'` to display the C: drive storage status only.

```powershell
powershell -ExecutionPolicy Bypass -Command "
$Mode = 'clean'; # SET TO: 'clean' for cl_p, 'estimate' for cl_e, 'status' for cl_s

$Report = [System.Collections.Generic.List[PSCustomObject]]::new();
$global:TotalSpace = 0;

function Get-FolderSize ($Path) {
    if (Test-Path $Path) {
        $files = Get-ChildItem -Path $Path -Recurse -File -ErrorAction SilentlyContinue;
        if ($files) {
            $sum = ($files | Measure-Object -Property Length -Sum).Sum;
            if ($sum) { return $sum }
        }
    }
    return 0;
}

function Process-Directory ($Path, $Name, $Clean) {
    if (Test-Path $Path) {
        $Size = Get-FolderSize $Path;
        if ($Clean) {
            $items = Get-ChildItem -Path $Path -ErrorAction SilentlyContinue;
            foreach ($item in $items) {
                try {
                    Remove-Item $item.FullName -Recurse -Force -ErrorAction SilentlyContinue;
                } catch {}
            }
            $FinalSize = Get-FolderSize $Path;
            $Saved = $Size - $FinalSize;
            if ($Saved -lt 0) { $Saved = 0 }
            $global:TotalSpace += $Saved;
            $Report.Add([PSCustomObject]@{
                Category = $Name;
                Path     = $Path;
                SpaceMB  = [Math]::Round($Saved / 1MB, 2);
                Status   = 'Cleaned';
            });
        } else {
            $global:TotalSpace += $Size;
            $Report.Add([PSCustomObject]@{
                Category = $Name;
                Path     = $Path;
                SpaceMB  = [Math]::Round($Size / 1MB, 2);
                Status   = 'To Clean';
            });
        }
    }
}

if ($Mode -eq 'status') {
    $Drive = Get-PSDrive C;
    Write-Output '=== C: DRIVE STORAGE STATUS ===';
    Write-Output ('Free Space: ' + [Math]::Round($Drive.Free / 1GB, 2) + ' GB');
    Write-Output ('Used Space: ' + [Math]::Round($Drive.Used / 1GB, 2) + ' GB');
    Write-Output ('Total Size: ' + [Math]::Round(($Drive.Free + $Drive.Used) / 1GB, 2) + ' GB');
    exit;
}

# Recycle Bin handling
try {
    $shell = New-Object -ComObject Shell.Application;
    $bin = $shell.Namespace(0x0a);
    $binSize = 0;
    foreach ($item in $bin.Items()) {
        $binSize += $item.Size;
    }
    if ($Mode -eq 'clean') {
        Clear-RecycleBin -Force -ErrorAction SilentlyContinue;
        $global:TotalSpace += $binSize;
        $Report.Add([PSCustomObject]@{
            Category = 'Recycle Bin';
            Path     = 'Recycle Bin';
            SpaceMB  = [Math]::Round($binSize / 1MB, 2);
            Status   = 'Cleared';
        });
    } else {
        $global:TotalSpace += $binSize;
        $Report.Add([PSCustomObject]@{
            Category = 'Recycle Bin';
            Path     = 'Recycle Bin';
            SpaceMB  = [Math]::Round($binSize / 1MB, 2);
            Status   = 'To Clean';
        });
    }
} catch {
    $Report.Add([PSCustomObject]@{
        Category = 'Recycle Bin';
        Path     = 'Recycle Bin';
        SpaceMB  = 0;
        Status   = if ($Mode -eq 'clean') { 'Skipped or empty' } else { 'Unavailable' };
    });
}

$CleanAction = ($Mode -eq 'clean');
Process-Directory $env:TEMP 'User Temp Files' $CleanAction;
Process-Directory \"$env:SystemRoot\Temp\" 'System Temp Files' $CleanAction;
Process-Directory \"$env:SystemRoot\SoftwareDistribution\Download\" 'Windows Update Cache' $CleanAction;

if (Test-Path \"$env:SystemRoot\") {
    $Logs = Get-ChildItem -Path \"$env:SystemRoot\" -Filter '*.log' -File -ErrorAction SilentlyContinue;
    $LogsSize = 0;
    foreach ($log in $Logs) {
        $LogsSize += $log.Length;
        if ($CleanAction) {
            try {
                Remove-Item $log.FullName -Force -ErrorAction SilentlyContinue;
            } catch {}
        }
    }
    $global:TotalSpace += $LogsSize;
    $Report.Add([PSCustomObject]@{
        Category = 'System Log Files';
        Path     = \"$env:SystemRoot\\*.log\";
        SpaceMB  = [Math]::Round($LogsSize / 1MB, 2);
        Status   = if ($CleanAction) { 'Cleaned' } else { 'To Clean' };
    });
}

if ($Mode -eq 'clean') {
    Write-Output '=== CLEANUP REPORT ===';
} else {
    Write-Output '=== CLEANUP ESTIMATE REPORT ===';
}
$Report | Format-Table -AutoSize | Out-String | Write-Output;

if ($Mode -eq 'clean') {
    Write-Output ('Total space saved: ' + [Math]::Round($global:TotalSpace / 1MB, 2) + ' MB');
} else {
    Write-Output ('Estimated space to save: ' + [Math]::Round($global:TotalSpace / 1MB, 2) + ' MB');
}
$Drive = Get-PSDrive C;
Write-Output ('Storage Status (C:): ' + [Math]::Round($Drive.Free / 1GB, 2) + ' GB free of ' + [Math]::Round(($Drive.Free + $Drive.Used) / 1GB, 2) + ' GB');
"
```

### 2. Format and Display Report
Parse the output of the cleanup/estimate command and display a beautifully formatted Markdown report showing:
- Cleaned/Estimated categories and their paths.
- Amount of disk space saved or that would be saved in MB.
- Total disk space reclaimed or reclaimable.
- A summary of system cleanliness.

---

## 🔗 Direct Links
* 🧹 [PC Cleaner Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/clean_pc.md)
* 💻 [PC Specialist Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/skill_pc.md)
* 📄 [Help Hardware (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md)
