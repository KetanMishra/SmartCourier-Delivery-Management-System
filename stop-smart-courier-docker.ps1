#Requires -Version 5.1
<#
.SYNOPSIS
    Smart Courier - saare Docker Compose containers ek command se band.

.DESCRIPTION
    Project root par `docker compose down` chalata hai.

.PARAMETER Volumes
    Saath mein named volumes bhi hata do (MySQL data wipe). Default: nahi.

.EXAMPLE
    .\stop-smart-courier-docker.ps1

.EXAMPLE
    .\stop-smart-courier-docker.ps1 -Volumes
#>
[CmdletBinding()]
param(
    [switch]$Volumes
)

$ErrorActionPreference = 'Stop'

$Root = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
Set-Location -LiteralPath $Root

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker CLI nahi mila."
}

if (-not (Test-Path -LiteralPath (Join-Path $Root 'docker-compose.yml'))) {
    Write-Error "docker-compose.yml nahi mila: $Root"
}

# Do not use $args - it is PowerShell's automatic variable for unbound parameters.
$composeDownArgs = @('compose', 'down')
if ($Volumes) {
    $composeDownArgs += '-v'
    Write-Host "Volumes bhi hata rahe hain (-v) - MySQL data wipe ho sakta hai." -ForegroundColor Yellow
}

Write-Host "Chal raha hai: docker $($composeDownArgs -join ' ')" -ForegroundColor DarkGray
& docker @composeDownArgs
exit $LASTEXITCODE
