# Test the updated login page

Write-Host "=== Testing Updated Login Page ===" -ForegroundColor Green
Write-Host ""

Write-Host "Frontend URL: http://localhost:5175/login" -ForegroundColor Yellow
Write-Host ""
Write-Host "The login page should now show:" -ForegroundColor Cyan
Write-Host "  - Welcome to DreamCollections header" -ForegroundColor White
Write-Host "  - Demo Accounts section" -ForegroundColor White
Write-Host "  - Two tabs: 'Sign in with OTP' and 'Sign in with Password'" -ForegroundColor White
Write-Host "  - Modern, professional design" -ForegroundColor White
Write-Host "  - OTP verification flow when using OTP tab" -ForegroundColor White
Write-Host ""

# Test OTP login flow
Write-Host "Testing OTP login flow..." -ForegroundColor Yellow
$otpRequest = @{
    phoneNumber = '+919876543210'
    email = 'admin@dreamcollections.com'
    type = 'LOGIN'
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/otp/send' -Method POST -Body $otpRequest -ContentType 'application/json'
    Write-Host "SUCCESS: OTP sent for admin login - Status $($response.StatusCode)" -ForegroundColor Green
    $responseData = $response.Content | ConvertFrom-Json
    Write-Host "Message: $($responseData.message)" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "Check the Identity Service console for the OTP code" -ForegroundColor Cyan
    Write-Host "Use this OTP in the login page OTP verification step" -ForegroundColor Cyan
    
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Login Page Testing Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Please visit http://localhost:5175/login to see the updated interface!" -ForegroundColor Yellow
