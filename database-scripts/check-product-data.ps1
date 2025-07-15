# Check product data through API

Write-Host "=== Checking Product Data via API ===" -ForegroundColor Green
Write-Host ""

# Get all products to see category information
Write-Host "Fetching products..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/products?page=0&size=20' -Method GET
    $products = ($response.Content | ConvertFrom-Json).content
    
    Write-Host "Found $($products.Count) products" -ForegroundColor Green
    Write-Host ""
    
    # Show products with their categories
    Write-Host "Products with Categories:" -ForegroundColor Yellow
    foreach ($product in $products) {
        $categoryName = if ($product.category) { $product.category.name } else { "No Category" }
        $subcategoryName = if ($product.subcategory) { $product.subcategory.name } else { "No Subcategory" }
        
        Write-Host "ID: $($product.id) | Name: $($product.name)" -ForegroundColor White
        Write-Host "  Category: $categoryName" -ForegroundColor Cyan
        Write-Host "  Subcategory: $subcategoryName" -ForegroundColor Cyan
        Write-Host ""
    }
    
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "=== Product Data Check Complete ===" -ForegroundColor Green
