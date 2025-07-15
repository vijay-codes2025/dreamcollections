$body = @{
    username = 'admin'
    password = 'password123'
} | ConvertTo-Json

Write-Host "Request body: $body"

try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/signin' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "Response Status: $($response.StatusCode)"
    Write-Host "Response Content: $($response.Content)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        Write-Host "Status Description: $($_.Exception.Response.StatusDescription)"
    }
}
