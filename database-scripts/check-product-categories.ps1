# Check product categories and subcategories

Write-Host "=== Checking Product Categories ===" -ForegroundColor Green
Write-Host ""

# Check categories
Write-Host "Categories:" -ForegroundColor Yellow
$categoriesQuery = "SELECT id, name, description FROM categories ORDER BY id;"
psql -h localhost -p 5432 -U postgres -d dreamcollections_catalog -c $categoriesQuery

Write-Host ""

# Check subcategories
Write-Host "Subcategories:" -ForegroundColor Yellow
$subcategoriesQuery = "SELECT id, name, category_id FROM subcategories ORDER BY category_id, id;"
psql -h localhost -p 5432 -U postgres -d dreamcollections_catalog -c $subcategoriesQuery

Write-Host ""

# Check products with their categories and subcategories
Write-Host "Products with Categories:" -ForegroundColor Yellow
$productsQuery = @"
SELECT 
    p.id, 
    p.name, 
    c.name as category_name,
    s.name as subcategory_name
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN subcategories s ON p.subcategory_id = s.id
WHERE p.name LIKE '%Ring%' OR p.name LIKE '%Earring%'
ORDER BY p.id;
"@

psql -h localhost -p 5432 -U postgres -d dreamcollections_catalog -c $productsQuery

Write-Host ""
Write-Host "=== Category Check Complete ===" -ForegroundColor Green
