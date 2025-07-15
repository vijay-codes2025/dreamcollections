# Test the new authentication system with phone/email login

Write-Host "=== Testing New Authentication System ===" -ForegroundColor Green
Write-Host ""

# Test 1: Login with email (existing user)
Write-Host "Test 1: Login with email (admin@dreamcollections.com)" -ForegroundColor Yellow
$loginBody1 = @{
    loginId = 'admin@dreamcollections.com'
    password = 'password123'
} | ConvertTo-Json

try {
    $response1 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginBody1 -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response1.StatusCode)" -ForegroundColor Green
    $responseData1 = $response1.Content | ConvertFrom-Json
    Write-Host "Token received: $($responseData1.token.Substring(0,20))..." -ForegroundColor Green
    Write-Host "User ID: $($responseData1.id), Username: $($responseData1.username)" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 2: Login with username (legacy support)
Write-Host "Test 2: Login with username (admin)" -ForegroundColor Yellow
$loginBody2 = @{
    username = 'admin'
    password = 'password123'
} | ConvertTo-Json

try {
    $response2 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginBody2 -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response2.StatusCode)" -ForegroundColor Green
    $responseData2 = $response2.Content | ConvertFrom-Json
    Write-Host "Token received: $($responseData2.token.Substring(0,20))..." -ForegroundColor Green
    Write-Host "User ID: $($responseData2.id), Username: $($responseData2.username)" -ForegroundColor Green
} catch {
    Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 3: Register new user with phone number
Write-Host "Test 3: Register new user with phone number" -ForegroundColor Yellow
$registerBody = @{
    username = 'testuser_' + (Get-Random -Maximum 9999)
    email = 'testuser' + (Get-Random -Maximum 9999) + '@example.com'
    phoneNumber = '+91987654' + (Get-Random -Minimum 1000 -Maximum 9999)
    firstName = 'Test'
    lastName = 'User'
    password = 'password123'
} | ConvertTo-Json

try {
    $response3 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signup' -Method POST -Body $registerBody -ContentType 'application/json'
    Write-Host "✅ SUCCESS: Status $($response3.StatusCode)" -ForegroundColor Green
    $responseData3 = $response3.Content | ConvertFrom-Json
    Write-Host "Registration message: $($responseData3.message)" -ForegroundColor Green
    
    # Extract the details for login test
    $newUserData = $registerBody | ConvertFrom-Json
    $newEmail = $newUserData.email
    $newPhone = $newUserData.phoneNumber
    
    Write-Host ""
    Write-Host "Test 4: Login with newly registered email" -ForegroundColor Yellow
    $loginNewEmail = @{
        loginId = $newEmail
        password = 'password123'
    } | ConvertTo-Json
    
    try {
        $response4 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginNewEmail -ContentType 'application/json'
        Write-Host "✅ SUCCESS: Login with email works! Status $($response4.StatusCode)" -ForegroundColor Green
        $responseData4 = $response4.Content | ConvertFrom-Json
        Write-Host "User ID: $($responseData4.id), Username: $($responseData4.username)" -ForegroundColor Green
    } catch {
        Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "Test 5: Login with newly registered phone number" -ForegroundColor Yellow
    $loginNewPhone = @{
        loginId = $newPhone
        password = 'password123'
    } | ConvertTo-Json
    
    try {
        $response5 = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $loginNewPhone -ContentType 'application/json'
        Write-Host "✅ SUCCESS: Login with phone works! Status $($response5.StatusCode)" -ForegroundColor Green
        $responseData5 = $response5.Content | ConvertFrom-Json
        Write-Host "User ID: $($responseData5.id), Username: $($responseData5.username)" -ForegroundColor Green
    } catch {
        Write-Host "❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ REGISTRATION FAILED: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Authentication Testing Complete ===" -ForegroundColor Green
