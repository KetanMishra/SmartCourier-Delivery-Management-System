#Requires -Version 5.1
<#
.SYNOPSIS
    Smart Courier - ek click: Docker stack build + start (Zipkin + SonarQube + microservices).

.DESCRIPTION
    docker compose up --build project root se. Shamil:
    - Zipkin (9411) - distributed tracing
    - SonarQube LTS Community (9000) - code quality; data persistent volumes par
    Pehli baar SonarQube 2-5 min le sakta hai. Docker Desktop ko ~4 GB+ RAM dena behtar hai.
    Tracing: TRACING_SAMPLE_PROBABILITY (0.0-1.0) compose env se.

    Sonar scan (alag terminal, SonarQube UP ke baad):
      mvn verify sonar:sonar -Dsonar.login=<token>
    Pehli login UI: admin / admin (password change zaroori). Token: My Account -> Security.

.PARAMETER Foreground
    Containers foreground mein - logs terminal par. Default: detached (-d).

.EXAMPLE
    .\run-smart-courier-docker.ps1

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .\run-smart-courier-docker.ps1

.EXAMPLE
    .\run-smart-courier-docker.ps1 -Foreground
#>
[CmdletBinding()]
param(
    [switch]$Foreground
)

$ErrorActionPreference = 'Stop'

$env:DOCKER_BUILDKIT = '1'
$env:COMPOSE_DOCKER_CLI_BUILD = '1'

$Root = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
Set-Location -LiteralPath $Root

Write-Host ""
Write-Host "Smart Courier - Docker stack (Zipkin + SonarQube)" -ForegroundColor Cyan
Write-Host "Project root: $Root" -ForegroundColor DarkGray
Write-Host ""

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker CLI nahi mila. Docker Desktop install karo aur PATH mein 'docker' ho."
}

$maxWait = 45
$waited = 0
while ($waited -lt $maxWait) {
    & docker info 1>$null 2>$null
    if ($LASTEXITCODE -eq 0) { break }
    Write-Host ('Docker daemon ka intezaar... ({0} s)' -f $waited) -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    $waited += 3
}

& docker info 1>$null 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Error "Docker daemon respond nahi kar raha. Docker Desktop kholo / running rakho, phir dubara script chalao."
}

if (-not (Test-Path -LiteralPath (Join-Path $Root 'docker-compose.yml'))) {
    Write-Error "docker-compose.yml is path par nahi mila: $Root"
}

$composeArgs = @('compose', 'up', '--build', '--remove-orphans')
if (-not $Foreground) {
    $composeArgs += '-d'
}

Write-Host "Chal raha hai: docker $($composeArgs -join ' ')" -ForegroundColor DarkGray
Write-Host ""

& docker @composeArgs
$exit = $LASTEXITCODE

if ($exit -ne 0) {
    exit $exit
}

Write-Host ""
Write-Host "Ho gaya." -ForegroundColor Green
Write-Host ""

function Wait-SonarUp {
    param([int]$MaxAttempts = 120, [int]$SleepSec = 3)
    Write-Host "SonarQube - JVM + embedded search start (pehli baar aksar 2-6 min); yeh normal hai, stuck nahi." -ForegroundColor DarkGray
    for ($i = 0; $i -lt $MaxAttempts; $i++) {
        try {
            $st = Invoke-RestMethod -Uri 'http://127.0.0.1:9000/api/system/status' -TimeoutSec 8 -ErrorAction Stop
            if ($st.status -eq 'UP') {
                return $true
            }
        }
        catch {
            # abhi port bind / migration chal rahi hai
        }
        if ($i -gt 0 -and $i % 20 -eq 0) {
            $approxSec = [int]($i * $SleepSec)
            Write-Host ('  ... SonarQube abhi bhi start ho raha hai (~{0}s) - docker compose logs -f sonarqube se dekh sakte ho' -f $approxSec) -ForegroundColor DarkGray
        }
        Start-Sleep -Seconds $SleepSec
    }
    return $false
}

function Wait-ZipkinReady {
    param([int]$MaxAttempts = 70, [int]$SleepSec = 3)
    # Zipkin 2/3: /health; agar 404 ho to UI root ya API bhi 200 dete hain jab server sun raha ho
    $uris = @(
        'http://127.0.0.1:9411/health',
        'http://127.0.0.1:9411/api/v2/services',
        'http://127.0.0.1:9411/'
    )
    for ($z = 0; $z -lt $MaxAttempts; $z++) {
        foreach ($u in $uris) {
            try {
                $zr = Invoke-WebRequest -Uri $u -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
                if ($zr.StatusCode -eq 200) {
                    return $true
                }
            }
            catch { }
        }
        Start-Sleep -Seconds $SleepSec
    }
    return $false
}

if (-not $Foreground) {
    Write-Host "Thoda ruk rahe hain (containers port bind kar rahe hain)..." -ForegroundColor DarkGray
    Start-Sleep -Seconds 10

    Write-Host "Zipkin (9411)..." -ForegroundColor DarkGray
    if (Wait-ZipkinReady) {
        Write-Host "Zipkin ready - http://localhost:9411" -ForegroundColor Green
    }
    else {
        Write-Host "Zipkin is poll par abhi UP detect nahi hua - aksar build ke turant baad hota hai." -ForegroundColor Yellow
        Write-Host "  1-2 min baad browser: http://localhost:9411   ya   docker compose logs zipkin" -ForegroundColor DarkGray
    }
    Write-Host ""

    Write-Host "SonarQube (9000)..." -ForegroundColor DarkGray
    if (Wait-SonarUp) {
        Write-Host "SonarQube UP - http://localhost:9000  (default login: admin / admin, phir naya password)" -ForegroundColor Green
    }
    else {
        Write-Host "SonarQube abhi tak UP nahi - logs: docker compose logs -f sonarqube" -ForegroundColor Yellow
        Write-Host "  Docker memory 4 GB+ dena; pehla start slow hota hai." -ForegroundColor DarkGray
    }
    Write-Host ""
}

Write-Host "URLs:" -ForegroundColor White
Write-Host "  API Gateway     http://localhost:8080"
Write-Host "  Auth Swagger    http://localhost:8081/swagger-ui.html"
Write-Host "  Delivery        http://localhost:8082/swagger-ui.html"
Write-Host "  Tracking        http://localhost:8083/swagger-ui.html"
Write-Host "  Admin           http://localhost:8084/swagger-ui.html"
Write-Host "  Zipkin UI       http://localhost:9411"
Write-Host "  SonarQube       http://localhost:9000  (code smells, coverage, security hotspots)"
Write-Host "  Eureka          http://localhost:8761"
Write-Host "  Config Server   http://localhost:8888"
Write-Host "  RabbitMQ UI     http://localhost:15672  (guest / guest)"
Write-Host "  MySQL           localhost:3306  (root / root)"
Write-Host ""

Write-Host "Sonar scan (SonarQube UP + token banane ke baad):" -ForegroundColor DarkCyan
Write-Host "  cd "$Root"" -ForegroundColor DarkGray
Write-Host "  mvn verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=YOUR_TOKEN" -ForegroundColor DarkGray
Write-Host ""

if (-not $Foreground) {
    Write-Host "Logs:  docker compose logs -f" -ForegroundColor DarkYellow
    Write-Host "Down:  docker compose down" -ForegroundColor DarkYellow
    Write-Host "Sonar data volumes: sonarqube-data (compose down se wipe nahi hota)" -ForegroundColor DarkGray
    Write-Host ""
}

exit 0