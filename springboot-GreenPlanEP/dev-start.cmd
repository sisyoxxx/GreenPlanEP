@echo off
setlocal

set PORT=%1
if "%PORT%"=="" set PORT=8080

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0dev-start.ps1" -Port %PORT%
