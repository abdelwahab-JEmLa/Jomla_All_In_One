# Skill - PC Specialist & Tools (skill_pc)

This skill instructs the assistant on how to:
1. Act as a PC Hardware & System Specialist, executing deep diagnostics on the host Windows machine to gather OS details, CPU load, RAM usage, storage space, network status, and top running processes.
2. Export the Firebase M01Produit (M1) records from the offline SQLite database cache, format them beautifully, convert them to a styled Microsoft Excel `.xlsx` file, and save it directly to the PC Desktop.

---

## Trigger Phrases
- `skill_pc`
- `pc_spec`
- `pc_status`
- `diagnose_pc`
- `fb_m1_excel`
- `m1_excel`
- `Todo: fb_m1_excel`
- `Todo: m1_excel`

---

## Steps to Execute

### Mode A: Execute System Diagnostics (Triggers: `skill_pc`, `pc_spec`, `pc_status`, `diagnose_pc`)
Run the following PowerShell command to collect comprehensive PC performance, hardware, and network metrics:

```powershell
powershell -Command "
$os = Get-CimInstance Win32_OperatingSystem;
$cpu = Get-CimInstance Win32_Processor;
$cs = Get-CimInstance Win32_ComputerSystem;
$disks = Get-Volume | Where-Object DriveLetter;
$ip = Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '127.*' -and $_.InterfaceAlias -notlike '*Loopback*' };
$procsCPU = Get-Process | Sort-Object CPU -Descending | Select-Object -First 5;
$procsRAM = Get-Process | Sort-Object WorkingSet64 -Descending | Select-Object -First 5;

Write-Output '=== OS INFO ===';
Write-Output ('OS: ' + $os.Caption + ' (' + $os.OSArchitecture + ')');
Write-Output ('Version: ' + $os.Version);
Write-Output ('Uptime: ' + ([DateTime]::Now - $os.LastBootUpTime).ToString('d\.hh\:mm\:ss'));

Write-Output '=== CPU & RAM ===';
Write-Output ('CPU: ' + $cpu.Name);
Write-Output ('Cores: ' + $cpu.NumberOfCores + ' Cores / ' + $cpu.NumberOfLogicalProcessors + ' Threads');
Write-Output ('Total RAM: ' + [Math]::Round($os.TotalVisibleMemorySize / 1MB, 2) + ' GB');
Write-Output ('Free RAM: ' + [Math]::Round($os.FreePhysicalMemory / 1MB, 2) + ' GB (' + [Math]::Round(($os.FreePhysicalMemory / $os.TotalVisibleMemorySize) * 100, 1) + '% free)');

Write-Output '=== STORAGE ===';
$disks | ForEach-Object {
    $size = [Math]::Round($_.Size / 1GB, 2);
    $free = [Math]::Round($_.SizeRemaining / 1GB, 2);
    $used = $size - $free;
    $pct = [Math]::Round(($used / $size) * 100, 1);
    Write-Output ('Drive ' + $_.DriveLetter + ': [' + $_.FileSystemLabel + '] ' + $used + 'GB / ' + $size + 'GB used (' + $pct + '%)');
};

Write-Output '=== NETWORK ===';
$ip | ForEach-Object {
    Write-Output ($_.InterfaceAlias + ': ' + $_.IPAddress);
};

Write-Output '=== TOP CPU PROCESSES ===';
$procsCPU | ForEach-Object {
    Write-Output ($_.Name + ' (PID: ' + $_.Id + ') - CPU: ' + [Math]::Round($_.CPU, 1));
};

Write-Output '=== TOP RAM PROCESSES ===';
$procsRAM | ForEach-Object {
    Write-Output ($_.Name + ' (PID: ' + $_.Id + ') - RAM: ' + [Math]::Round($_.WorkingSet64 / 1MB, 1) + ' MB');
};
"
```

Parse the output of the command and display a beautifully formatted Markdown report with sections, tables, and system health status.

### Mode B: Export Firebase M1 to Excel (Triggers: `fb_m1_excel`, `m1_excel`, `Todo: fb_m1_excel`, `Todo: m1_excel`)
1. **Locate Dynamic TODO Comments (If triggered via Codebase)**:
   Search the codebase (`app/src/main/java`) for any dynamic comments `TODO: fb_m1_excel` or `TODO: m1_excel` (case-insensitive) to remove them after completion.
2. **Pull the Latest Database from the Device**:
   Pull the active Firebase Realtime Database cache from the connected device using ADB:
   - Ensure destination exists: `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "touch /data/local/tmp/fb_db_temp && chmod 666 /data/local/tmp/fb_db_temp"`
   - Copy to cache: `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles cp databases/abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app_default cache/fb_db_temp"`
   - Copy from cache to tmp: `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell "run-as com.example.light_app_controles cp cache/fb_db_temp /data/local/tmp/fb_db_temp"`
   - Pull locally: `& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /data/local/tmp/fb_db_temp fb_db_temp`
3. **Run the Export Script**:
   Execute the export python script: `python copy_/fb_db/run_fb_m1_excel.py`. The script will query the `fb_db_temp` file, parse the JSON `M01Produit` cache rows, create a beautifully styled Excel spreadsheet, and save it on the PC Desktop as `fb_m1_ref.xlsx`.
4. **Display Report**:
   Display a summary of the export including record counts and preview table of the first few records.

---

## 🔗 Direct Links
* 🖥 [PC Specialist Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/skill_pc.md)
* ⚙️ [PC Help Page (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md)
* 🐍 [run_fb_m1_excel.py](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/fb_db/run_fb_m1_excel.py)
* 💾 [last_fb_m1_excel.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/fb_db/last_fb_m1_excel.md)
