$downloadsDir = "C:\Users\Abou Mohamed\Downloads"
param(
    [string]$Workspace = (Get-Location).Path
)

# 1. Trouver le dernier téléchargement zip, rar, ou kt
$latest = Get-ChildItem -Path $downloadsDir | Where-Object { $_.Extension -match '^\.(zip|rar|kt)$' } | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if ($null -eq $latest) {
    Write-Host "ERROR: No downloaded .zip, .rar, or .kt file found." -ForegroundColor Red
    exit 1
}

Write-Host "Found latest download: $($latest.Name)"

$tempDir = Join-Path $downloadsDir "temp_zip_colle"
$filesToProcess = @()

if ($latest.Extension -eq ".kt") {
    $cleanName = $latest.Name -replace '\s*\(\d+\)\.kt$', '.kt'
    $filesToProcess += @{ Source = $latest.FullName; Name = $cleanName }
} else {
    if (Test-Path $tempDir) { Remove-Item -Path $tempDir -Recurse -Force }
    New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
    
    if ($latest.Extension -eq ".zip") {
        Expand-Archive -Path $latest.FullName -DestinationPath $tempDir -Force
    } elseif ($latest.Extension -eq ".rar") {
        $winrar = "C:\Program Files\WinRAR\WinRAR.exe"
        $sevenzip = "C:\Program Files\7-Zip\7z.exe"
        if (Test-Path $winrar) {
            Start-Process -FilePath $winrar -ArgumentList "x -y `"$($latest.FullName)`" `"$tempDir\`"" -Wait -NoNewWindow
        } elseif (Test-Path $sevenzip) {
            Start-Process -FilePath $sevenzip -ArgumentList "x `"$($latest.FullName)`" -o`"$tempDir`" -y" -Wait -NoNewWindow
        } else {
            Write-Host "ERROR: Neither WinRAR nor 7-Zip found." -ForegroundColor Red
            exit 1
        }
    }
    Get-ChildItem -Path $tempDir -Filter "*.kt" -Recurse | ForEach-Object {
        $filesToProcess += @{ Source = $_.FullName; Name = $_.Name }
    }
}

$historyPath = Join-Path $Workspace ".zip_colle_history.json"
$history = @{}
if (Test-Path $historyPath) {
    $history = Get-Content $historyPath | ConvertFrom-Json -AsHashtable
}

$refHistPath = Join-Path $Workspace "app/src/main/java/skill_agent/copy_context/copy_skill/references/hist_copie.md"
$refLinks = @{}
if (Test-Path $refHistPath) {
    $content = Get-Content $refHistPath
    $matches = [regex]::Matches($content, '\[([^\]]+)\]\((file:///[^\)]+)\)')
    foreach ($m in $matches) {
        $fileName = $m.Groups[1].Value
        $fileUri = $m.Groups[2].Value
        $filePath = [System.Uri]::UnescapeDataString(($fileUri -replace '^file:///', '' -replace '/', '\'))
        if (-not $refLinks.ContainsKey($fileName)) { $refLinks[$fileName] = $filePath }
    }
}

$updatedFiles = @()
foreach ($file in $filesToProcess) {
    $fileName = $file.Name
    $srcPath = $file.Source
    $destPath = $null
    
    if ($history.ContainsKey($fileName)) { $destPath = $history[$fileName] }
    if ($null -eq $destPath -and $refLinks.ContainsKey($fileName)) { $destPath = $refLinks[$fileName] }
    if ($null -eq $destPath) {
        $appSrc = Join-Path $Workspace "app/src"
        if (Test-Path $appSrc) {
            $destPath = [System.IO.Directory]::EnumerateFiles($appSrc, $fileName, [System.IO.SearchOption]::AllDirectories) | Select-Object -First 1
        }
    }
    
    if ($null -ne $destPath -and (Test-Path $destPath)) {
        Copy-Item -Path $srcPath -Destination $destPath -Force
        $normalizedDest = $destPath.Replace("\", "/")
        $history[$fileName] = $normalizedDest
        $updatedFiles += @{ Name = $fileName; Path = $normalizedDest }
    }
}

$history | ConvertTo-Json | Out-File -FilePath $historyPath -Encoding utf8 -Force
if (Test-Path $tempDir) { Remove-Item -Path $tempDir -Recurse -Force }

Write-Host "`n=== RESULTS ==="
foreach ($f in $updatedFiles) {
    $uriPath = $f.Path.Replace(" ", "%20")
    Write-Host "### 🔗 [$($f.Name)](file:///$uriPath)"
}
