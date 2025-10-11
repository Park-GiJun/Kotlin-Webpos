# Refresh PowerShell PATH without restarting
# Run this script if k6 is installed but not found

Write-Host "Refreshing PATH environment variable..." -ForegroundColor Yellow

$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "PATH refreshed!" -ForegroundColor Green
Write-Host ""
Write-Host "Testing k6..." -ForegroundColor Cyan

try {
    $k6Version = k6 version
    Write-Host "✓ k6 found: $k6Version" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now run k6 tests:" -ForegroundColor Cyan
    Write-Host "  k6 run tests\smoke-test.js" -ForegroundColor White
} catch {
    Write-Host "✗ k6 still not found. Please restart PowerShell." -ForegroundColor Red
    Write-Host ""
    Write-Host "If restarting doesn't work, try:" -ForegroundColor Yellow
    Write-Host "1. Uninstall: winget uninstall k6" -ForegroundColor White
    Write-Host "2. Reinstall: winget install k6" -ForegroundColor White
    Write-Host "3. Restart PowerShell" -ForegroundColor White
}
