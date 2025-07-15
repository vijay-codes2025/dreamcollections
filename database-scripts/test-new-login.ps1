# Test the new OTP-based login system

Write-Host "=== Testing New Login System ===" -ForegroundColor Green
Write-Host ""

# Test 1: Send OTP for login
Write-Host "Test 1: Send OTP for login" -ForegroundColor Yellow
$otpRequest = @{
    phoneNumber = '+919876543210'
    email = 'admin@dreamcollections.com'
    type = 'LOGIN'
} | ConvertTo-Json

try {
    $response1 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/otp/send' -Method POST -Body $otpRequest -ContentType 'application/json'
    Write-Host "SUCCESS: OTP Send for Login - Status $($response1.StatusCode)" -ForegroundColor Green
    $responseData1 = $response1.Content | ConvertFrom-Json
    Write-Host "Message: $($responseData1.message)" -ForegroundColor Green
    Write-Host "Masked Phone: $($responseData1.maskedPhone)" -ForegroundColor Green
    Write-Host "Expires in: $($responseData1.expiresInSeconds) seconds" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "Check the Identity Service console for the OTP code" -ForegroundColor Cyan
    Write-Host "The OTP will be displayed in the format: SMS OTP for +919876543210: XXXXXX" -ForegroundColor Cyan
    
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 2: Test traditional password login
Write-Host "Test 2: Test traditional password login" -ForegroundColor Yellow
$loginRequest = @{
    loginId = 'admin@dreamcollections.com'
    password = 'password123'
} | ConvertTo-Json

try {
    $response2 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginRequest -ContentType 'application/json'
    Write-Host "SUCCESS: Password Login - Status $($response2.StatusCode)" -ForegroundColor Green
    $responseData2 = $response2.Content | ConvertFrom-Json
    Write-Host "User: $($responseData2.username), Role: $($responseData2.roles -join ', ')" -ForegroundColor Green
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== New Login System Testing Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend Login URL: http://localhost:5175/login" -ForegroundColor Yellow
Write-Host "You should now see:" -ForegroundColor Cyan
Write-Host "  - Two tabs: 'Sign in with OTP' and 'Sign in with Password'" -ForegroundColor White
Write-Host "  - Demo accounts section" -ForegroundColor White
Write-Host "  - Modern, professional design" -ForegroundColor White
Write-Host "  - OTP verification flow" -ForegroundColor White
