# Test with original admin credentials from the database

Write-Host "=== Testing Original Admin Credentials ===" -ForegroundColor Green
Write-Host ""

# Test 1: Login with original admin username
Write-Host "Test 1: Login with original admin username" -ForegroundColor Yellow
$loginBody1 = @{
    loginId = 'admin'
    password = 'password123'
} | ConvertTo-Json

try {
    $response1 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginBody1 -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response1.StatusCode)" -ForegroundColor Green
    $responseData1 = $response1.Content | ConvertFrom-Json
    Write-Host "Token received: $($responseData1.token.Substring(0,20))..." -ForegroundColor Green
    Write-Host "User ID: $($responseData1.id), Username: $($responseData1.username), Email: $($responseData1.email)" -ForegroundColor Green
    Write-Host "Roles: $($responseData1.roles -join ', ')" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 2: Try legacy username field
Write-Host "Test 2: Login with legacy username field" -ForegroundColor Yellow
$loginBody2 = @{
    username = 'admin'
    password = 'password123'
} | ConvertTo-Json

try {
    $response2 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginBody2 -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response2.StatusCode)" -ForegroundColor Green
    $responseData2 = $response2.Content | ConvertFrom-Json
    Write-Host "Token received: $($responseData2.token.Substring(0,20))..." -ForegroundColor Green
    Write-Host "User ID: $($responseData2.id), Username: $($responseData2.username), Email: $($responseData2.email)" -ForegroundColor Green
    Write-Host "Roles: $($responseData2.roles -join ', ')" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Testing Complete ===" -ForegroundColor Green
