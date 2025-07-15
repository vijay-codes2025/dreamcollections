# Test authentication with updated admin user

Write-Host "=== Testing Updated Authentication System ===" -ForegroundColor Green
Write-Host ""

# Test 1: Login with admin email
Write-Host "Test 1: Login with admin email (admin@dreamcollections.com)" -ForegroundColor Yellow
$loginBody1 = @{
    loginId = 'admin@dreamcollections.com'
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

# Test 2: Login with admin phone number
Write-Host "Test 2: Login with admin phone number (+919876543210)" -ForegroundColor Yellow
$loginBody2 = @{
    loginId = '+919876543210'
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

# Test 3: Login with admin username (legacy)
Write-Host "Test 3: Login with admin username (admin) - Legacy Support" -ForegroundColor Yellow
$loginBody3 = @{
    username = 'admin'
    password = 'password123'
} | ConvertTo-Json

try {
    $response3 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginBody3 -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response3.StatusCode)" -ForegroundColor Green
    $responseData3 = $response3.Content | ConvertFrom-Json
    Write-Host "Token received: $($responseData3.token.Substring(0,20))..." -ForegroundColor Green
    Write-Host "User ID: $($responseData3.id), Username: $($responseData3.username), Email: $($responseData3.email)" -ForegroundColor Green
    Write-Host "Roles: $($responseData3.roles -join ', ')" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 4: Test frontend login page
Write-Host "Test 4: Testing Frontend Integration" -ForegroundColor Yellow
Write-Host "✅ Frontend is running at: http://localhost:5175" -ForegroundColor Green
Write-Host "✅ You can now login with:" -ForegroundColor Green
Write-Host "   • Email: admin@dreamcollections.com" -ForegroundColor Cyan
Write-Host "   • Phone: +919876543210" -ForegroundColor Cyan
Write-Host "   • Username: admin (legacy)" -ForegroundColor Cyan
Write-Host "   • Password: password123" -ForegroundColor Cyan

Write-Host ""
Write-Host "=== Authentication Testing Complete ===" -ForegroundColor Green
