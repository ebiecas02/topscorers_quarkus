# test.ps1
Write-Host "Testing TopScorers API..." -ForegroundColor Green

$filePath = "C:\Users\LeNoVo\code-with-quarkus\TestData.csv"

# Check if file exists
if (Test-Path $filePath) {
    Write-Host "File found: $filePath" -ForegroundColor Green
} else {
    Write-Host "File not found: $filePath" -ForegroundColor Red
    exit
}

# Test 1: Upload endpoint
Write-Host "`nTesting /upload endpoint..." -ForegroundColor Yellow
$form = @{
    fileData = Get-Item $filePath
    fileName = "TestData.csv"
}
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/topscorers/upload" -Method Post -Form $form
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Cyan
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 2: Sample endpoint
Write-Host "`nTesting /sample endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/topscorers/sample" -Method Get
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Cyan
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}