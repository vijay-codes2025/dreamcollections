# Create a new admin user via API

Write-Host "=== Creating New Admin User ===" -ForegroundColor Green
Write-Host ""

# Create new admin user
Write-Host "Creating new admin user..." -ForegroundColor Yellow
$registerBody = @{
    username = 'admin_new'
    email = 'admin@dreamcollections.com'
    phoneNumber = '+919876543210'
    firstName = 'Admin'
    lastName = 'User'
    password = 'password123'
    role = 'ADMIN'
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signup' -Method POST -Body $registerBody -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response.StatusCode)" -ForegroundColor Green
    $responseData = $response.Content | ConvertFrom-Json
    Write-Host "Registration message: $($responseData.message)" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "Now testing login with new admin user..." -ForegroundColor Yellow
    
    # Test login with email
    $loginBody = @{
        loginId = 'admin@dreamcollections.com'
        password = 'password123'
    } | ConvertTo-Json
    
    try {
        $loginResponse = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginBody -ContentType 'application/json'
        Write-Host "✅ LOGIN SUCCESS: Status $($loginResponse.StatusCode)" -ForegroundColor Green
        $loginData = $loginResponse.Content | ConvertFrom-Json
        Write-Host "Token received: $($loginData.token.Substring(0,20))..." -ForegroundColor Green
        Write-Host "User ID: $($loginData.id), Username: $($loginData.username), Email: $($loginData.email)" -ForegroundColor Green
        Write-Host "Roles: $($loginData.roles -join ', ')" -ForegroundColor Green
        
        Write-Host ""
        Write-Host "✅ AUTHENTICATION SYSTEM IS NOW WORKING!" -ForegroundColor Green
        Write-Host "You can login with:" -ForegroundColor Cyan
        Write-Host "  • Email: admin@dreamcollections.com" -ForegroundColor Cyan
        Write-Host "  • Phone: +919876543210" -ForegroundColor Cyan
        Write-Host "  • Password: password123" -ForegroundColor Cyan
        
    } catch {
        Write-Host "❌ LOGIN FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ REGISTRATION FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorText = $reader.ReadToEnd()
        Write-Host "Error details: $errorText" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Admin Creation Complete ===" -ForegroundColor Green
