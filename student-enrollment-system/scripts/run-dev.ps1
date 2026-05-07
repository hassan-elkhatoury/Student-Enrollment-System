$Root = Split-Path -Parent $PSScriptRoot
$Logs = Join-Path $Root "logs"
$PidFile = Join-Path $Logs "service-pids.txt"
$Ports = 8761, 8080, 8081, 8082, 8083

New-Item -ItemType Directory -Force -Path $Logs | Out-Null

Write-Host "Stopping old project services if they are already running..."
foreach ($port in $Ports) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    foreach ($connection in $connections) {
        $process = Get-CimInstance Win32_Process -Filter "ProcessId = $($connection.OwningProcess)" -ErrorAction SilentlyContinue
        if ($process -and $process.CommandLine -like "*student-enrollment-system*") {
            Stop-Process -Id $connection.OwningProcess -Force
            Write-Host ("Stopped old process on port " + $port + " with PID " + $connection.OwningProcess)
        }
    }
}

Start-Sleep -Seconds 2

Write-Host "Building project..."
Push-Location $Root
mvn clean package -DskipTests
Pop-Location

if ($env:JAVA_HOME) {
    $Java = Join-Path $env:JAVA_HOME "bin\java.exe"
} else {
    $Java = (Get-Command java.exe).Source
}

$services = @(
    @{ Name = "discovery-server"; Jar = "discovery-server\target\discovery-server-1.0.0.jar"; Args = "" },
    @{ Name = "student-service"; Jar = "student-service\target\student-service-1.0.0.jar"; Args = " --spring.profiles.active=dev" },
    @{ Name = "course-service"; Jar = "course-service\target\course-service-1.0.0.jar"; Args = " --spring.profiles.active=dev" },
    @{ Name = "enrollment-service"; Jar = "enrollment-service\target\enrollment-service-1.0.0.jar"; Args = " --spring.profiles.active=dev" },
    @{ Name = "api-gateway"; Jar = "api-gateway\target\api-gateway-1.0.0.jar"; Args = "" }
)

$pids = @()

foreach ($service in $services) {
    $jarPath = Join-Path $Root $service.Jar
    $outLog = Join-Path $Logs ($service.Name + ".out.log")
    $errLog = Join-Path $Logs ($service.Name + ".err.log")
    $argumentLine = '-jar "' + $jarPath + '"' + $service.Args

    $process = Start-Process -FilePath $Java `
        -ArgumentList $argumentLine `
        -WorkingDirectory $Root `
        -RedirectStandardOutput $outLog `
        -RedirectStandardError $errLog `
        -WindowStyle Hidden `
        -PassThru

    $pids += ($service.Name + "=" + $process.Id)
    Write-Host ("Started " + $service.Name + " with PID " + $process.Id)
    Start-Sleep -Seconds 8
}

$pids | Set-Content -Path $PidFile

Write-Host "Services are starting in the background."
Write-Host "Open http://localhost:8080 when the gateway is ready."
Write-Host "Logs are available in the logs folder."
