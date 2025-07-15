# Test the new OTP authentication system

Write-Host "=== Testing OTP Authentication System ===" -ForegroundColor Green
Write-Host ""

# Test 1: Send OTP
Write-Host "Test 1: Send OTP for login" -ForegroundColor Yellow
$otpRequest = @{
    phoneNumber = '+919876543999'
    email = 'test@example.com'
    type = 'LOGIN'
} | ConvertTo-Json

try {
    $response1 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/otp/send' -Method POST -Body $otpRequest -ContentType 'application/json'
    Write-Host "✅ SUCCESS: OTP Send - Status $($response1.StatusCode)" -ForegroundColor Green
    $responseData1 = $response1.Content | ConvertFrom-Json
    Write-Host "Message: $($responseData1.message)" -ForegroundColor Green
    Write-Host "Masked Phone: $($responseData1.maskedPhone)" -ForegroundColor Green
    Write-Host "Expires in: $($responseData1.expiresInSeconds) seconds" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 2: Test registration fix
Write-Host "Test 2: Test registration with phone number" -ForegroundColor Yellow
$registerRequest = @{
    username = 'testuser_' + (Get-Random -Maximum 9999)
    email = 'testuser' + (Get-Random -Maximum 9999) + '@example.com'
    phoneNumber = '+91987654' + (Get-Random -Minimum 1000 -Maximum 9999)
    firstName = 'Test'
    lastName = 'User'
    password = 'password123'
} | ConvertTo-Json

try {
    $response2 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signup' -Method POST -Body $registerRequest -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Registration - Status $($response2.StatusCode)" -ForegroundColor Green
    $responseData2 = $response2.Content | ConvertFrom-Json
    Write-Host "Message: $($responseData2.message)" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 3: Test existing admin login
Write-Host "Test 3: Test existing admin login" -ForegroundColor Yellow
$loginRequest = @{
    loginId = 'admin@dreamcollections.com'
    password = 'password123'
} | ConvertTo-Json

try {
    $response3 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginRequest -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Admin Login - Status $($response3.StatusCode)" -ForegroundColor Green
    $responseData3 = $response3.Content | ConvertFrom-Json
    Write-Host "User: $($responseData3.username), Role: $($responseData3.roles -join ', ')" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== OTP System Testing Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "📱 OTP will be displayed in the Identity Service console logs" -ForegroundColor Cyan
Write-Host "🔗 Frontend URL: http://localhost:5175" -ForegroundColor Yellow
