# Test Login API Script
# Chạy script này để test API login

Write-Host "=== Testing Login API ===" -ForegroundColor Cyan
Write-Host "URL: http://localhost:8080/api/v1/auth/login" -ForegroundColor Yellow
Write-Host ""

$body = @{
    email = "admin@khohoclieu.com"
    password = "admin123"
} | ConvertTo-Json

Write-Host "Request Body:" -ForegroundColor Green
Write-Host $body
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" `
        -Method POST `
        -Body $body `
        -ContentType "application/json" `
        -ErrorAction Stop

    Write-Host "✅ LOGIN THÀNH CÔNG!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Yellow
    $response | Format-List

    Write-Host ""
    Write-Host "JWT Token:" -ForegroundColor Cyan
    Write-Host $response.token

} catch {
    Write-Host "❌ LOGIN THẤT BẠI!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error:" -ForegroundColor Yellow
    Write-Host $_.Exception.Message

    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host ""
        Write-Host "Response Body:" -ForegroundColor Yellow
        Write-Host $responseBody
    }
}


