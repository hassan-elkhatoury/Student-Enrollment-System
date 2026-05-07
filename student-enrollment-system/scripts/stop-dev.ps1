$Root = Split-Path -Parent $PSScriptRoot
$PidFile = Join-Path $Root "logs\service-pids.txt"
$Ports = 8761, 8080, 8081, 8082, 8083

if (Test-Path $PidFile) {
    Get-Content $PidFile | ForEach-Object {
        $parts = $_ -split "="
        if ($parts.Length -ne 2) {
            return
        }

        $name = $parts[0]
        $pidValue = [int]$parts[1]
        $process = Get-Process -Id $pidValue -ErrorAction SilentlyContinue

        if ($process) {
            Stop-Process -Id $pidValue -Force
            Write-Host ("Stopped " + $name + " with PID " + $pidValue)
        }
    }
}

foreach ($port in $Ports) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    foreach ($connection in $connections) {
        $process = Get-CimInstance Win32_Process -Filter "ProcessId = $($connection.OwningProcess)" -ErrorAction SilentlyContinue
        if ($process -and $process.CommandLine -like "*student-enrollment-system*") {
            Stop-Process -Id $connection.OwningProcess -Force
            Write-Host ("Stopped project process on port " + $port + " with PID " + $connection.OwningProcess)
        }
    }
}
