param(
  [int]$Port = 8080,
  [switch]$SkipKill
)

$ErrorActionPreference = 'Stop'
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

function Get-ListeningPids([int]$TargetPort) {
  $ids = @()
  try {
    $conns = Get-NetTCPConnection -LocalPort $TargetPort -State Listen -ErrorAction SilentlyContinue
    if ($conns) {
      $ids = $conns | Select-Object -ExpandProperty OwningProcess -Unique
    }
  } catch {
    $netstatLines = netstat -ano | Select-String ":$TargetPort"
    foreach ($line in $netstatLines) {
      $parts = ($line.ToString() -split '\s+') | Where-Object { $_ -ne '' }
      if ($parts.Length -ge 5 -and $parts[3] -eq 'LISTENING') {
        $ids += [int]$parts[4]
      }
    }
    $ids = $ids | Select-Object -Unique
  }
  return @($ids)
}

function Wait-PortFree([int]$TargetPort, [int]$MaxAttempts = 20) {
  for ($i = 1; $i -le $MaxAttempts; $i++) {
    $current = Get-ListeningPids -TargetPort $TargetPort
    if ($current.Count -eq 0) {
      return $true
    }
    Start-Sleep -Milliseconds 300
  }
  return $false
}

Write-Host "[dev-start] Project: $projectDir"
Write-Host "[dev-start] Target port: $Port"

if (-not $SkipKill) {
  $pids = Get-ListeningPids -TargetPort $Port
  if ($pids.Count -gt 0) {
    foreach ($procId in $pids) {
      try {
        $proc = Get-Process -Id $procId -ErrorAction Stop
        Write-Host "[dev-start] Stopping PID=$procId Name=$($proc.ProcessName)"
        Stop-Process -Id $procId -Force
      } catch {
        Write-Warning "[dev-start] Failed to stop PID=$procId. $_"
      }
    }
  } else {
    Write-Host "[dev-start] Port $Port is free"
  }
}

if (-not (Wait-PortFree -TargetPort $Port)) {
  $stillPids = Get-ListeningPids -TargetPort $Port
  throw "[dev-start] Port $Port is still occupied by PID(s): $($stillPids -join ', '). Please stop them manually or run as administrator."
}

Write-Host "[dev-start] Port $Port is ready"
Write-Host "[dev-start] Starting Spring Boot with Maven..."
& mvn -DskipTests spring-boot:run
