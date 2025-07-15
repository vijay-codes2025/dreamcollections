/**
 * Script to populate DreamCollections database via REST APIs
 * This script adds sample data including the Kids Collection with Indian pricing
 */

const API_BASE = 'http://localhost:8080/api';

// Sample data with Indian pricing and Kids Collection
const categories = [
  // Top-level categories
  { name: "Women's Jewelry", description: "Elegant jewelry collection designed for women including rings, necklaces, earrings, and bracelets", parentCategoryId: null },
  { name: "Men's Jewelry", description: "Sophisticated jewelry pieces for men including watches, rings, and chains", parentCategoryId: null },
  { name: "Kids Collection", description: "Safe and fun jewelry designed specifically for children with hypoallergenic materials", parentCategoryId: null },
  { name: "Luxury Collection", description: "Premium and luxury jewelry pieces with precious stones and metals", parentCategoryId: null },
  { name: "Wedding Collection", description: "Special jewelry for weddings including engagement rings and wedding bands", parentCategoryId: null },
];

const subcategories = [
  // Women's Jewelry subcategories (parent will be set after creation)
  { name: "Necklaces", description: "Beautiful necklaces in various styles and materials", parentName: "Women's Jewelry" },
  { name: "Rings", description: "Elegant rings for every occasion", parentName: "Women's Jewelry" },
  { name: "Earrings", description: "Stunning earrings from studs to statement pieces", parentName: "Women's Jewelry" },
  { name: "Bracelets", description: "Delicate and bold bracelets for every style", parentName: "Women's Jewelry" },
  
  // Men's Jewelry subcategories
  { name: "Men's Rings", description: "Masculine rings in various metals and styles", parentName: "Men's Jewelry" },
  { name: "Men's Chains", description: "Strong and stylish chains for men", parentName: "Men's Jewelry" },
  { name: "Cufflinks", description: "Elegant cufflinks for formal occasions", parentName: "Men's Jewelry" },
  
  // Kids Collection subcategories
  { name: "Kids Necklaces", description: "Safe and colorful necklaces for children with breakaway clasps", parentName: "Kids Collection" },
  { name: "Kids Bracelets", description: "Fun and adjustable bracelets perfect for little wrists", parentName: "Kids Collection" },
  { name: "Kids Earrings", description: "Hypoallergenic earrings designed for sensitive young ears", parentName: "Kids Collection" },
  { name: "Kids Rings", description: "Adjustable rings with fun designs for children", parentName: "Kids Collection" },
  { name: "Baby Jewelry", description: "Gentle jewelry pieces perfect for babies and toddlers", parentName: "Kids Collection" },
  
  // Luxury Collection subcategories
  { name: "Diamond Collection", description: "Exquisite diamond jewelry pieces", parentName: "Luxury Collection" },
  { name: "Gold Collection", description: "Premium gold jewelry", parentName: "Luxury Collection" },
  { name: "Platinum Collection", description: "Luxury platinum jewelry", parentName: "Luxury Collection" },
  
  // Wedding Collection subcategories
  { name: "Engagement Rings", description: "Beautiful engagement rings for proposals", parentName: "Wedding Collection" },
  { name: "Wedding Bands", description: "Perfect wedding bands for couples", parentName: "Wedding Collection" },
];

const products = [
  // Women's Necklaces
  { name: "Diamond Pendant Necklace", description: "Elegant diamond pendant necklace with 18k white gold chain. Features a brilliant cut diamond in a classic setting.", price: 74999.00, imageUrl: "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500", categoryName: "Necklaces" },
  { name: "Pearl Strand Necklace", description: "Classic freshwater pearl necklace with sterling silver clasp. Perfect for formal occasions.", price: 24999.00, imageUrl: "https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500", categoryName: "Necklaces" },
  { name: "Gold Chain Necklace", description: "Delicate 14k gold chain necklace, perfect for layering or wearing alone.", price: 16649.50, imageUrl: "https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500", categoryName: "Necklaces" },
  
  // Women's Rings
  { name: "Solitaire Diamond Ring", description: "Classic solitaire diamond ring with 1 carat diamond in platinum setting.", price: 208299.00, imageUrl: "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500", categoryName: "Rings" },
  { name: "Rose Gold Band Ring", description: "Simple and elegant rose gold band ring, perfect for everyday wear.", price: 12499.00, imageUrl: "https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500", categoryName: "Rings" },
  
  // Kids Collection
  { name: "Rainbow Butterfly Necklace", description: "Colorful butterfly pendant necklace with breakaway clasp for safety. Made with hypoallergenic materials.", price: 2082.50, imageUrl: "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500", categoryName: "Kids Necklaces" },
  { name: "Unicorn Charm Necklace", description: "Magical unicorn pendant on adjustable cord necklace. Perfect for young dreamers.", price: 1665.50, imageUrl: "https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500", categoryName: "Kids Necklaces" },
  { name: "Friendship Bracelet Set", description: "Set of 3 colorful friendship bracelets with adjustable sizing. Perfect for sharing with friends.", price: 1332.50, imageUrl: "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500", categoryName: "Kids Bracelets" },
  { name: "Flower Stud Earrings", description: "Hypoallergenic flower stud earrings perfect for sensitive ears. Available in multiple colors.", price: 1582.50, imageUrl: "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500", categoryName: "Kids Earrings" },
  { name: "Adjustable Flower Ring", description: "Cute flower ring that adjusts to fit growing fingers. Available in pink, purple, and blue.", price: 1082.50, imageUrl: "https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500", categoryName: "Kids Rings" },
  { name: "Baby's First Bracelet", description: "Gentle sterling silver bracelet designed for babies. Includes safety clasp and gift box.", price: 3332.50, imageUrl: "https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500", categoryName: "Baby Jewelry" },
];

// Function to make API calls
async function apiCall(method, endpoint, data = null) {
  const url = `${API_BASE}${endpoint}`;
  const options = {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  if (data) {
    options.body = JSON.stringify(data);
  }
  
  try {
    const response = await fetch(url, options);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error calling ${method} ${endpoint}:`, error);
    throw error;
  }
}

// Main population function
async function populateData() {
  console.log('🚀 Starting data population...');
  
  try {
    // Step 1: Create top-level categories
    console.log('📁 Creating top-level categories...');
    const createdCategories = {};
    
    for (const category of categories) {
      const created = await apiCall('POST', '/categories', category);
      createdCategories[category.name] = created;
      console.log(`✅ Created category: ${category.name} (ID: ${created.id})`);
    }
    
    // Step 2: Create subcategories
    console.log('📂 Creating subcategories...');
    
    for (const subcategory of subcategories) {
      const parentCategory = createdCategories[subcategory.parentName];
      if (parentCategory) {
        const subcategoryData = {
          name: subcategory.name,
          description: subcategory.description,
          parentCategoryId: parentCategory.id
        };
        
        const created = await apiCall('POST', '/categories', subcategoryData);
        createdCategories[subcategory.name] = created;
        console.log(`✅ Created subcategory: ${subcategory.name} (ID: ${created.id})`);
      }
    }
    
    // Step 3: Create products
    console.log('🛍️ Creating products...');
    
    for (const product of products) {
      const category = createdCategories[product.categoryName];
      if (category) {
        const productData = {
          name: product.name,
          description: product.description,
          price: product.price,
          imageUrl: product.imageUrl,
          categoryId: category.id
        };
        
        const created = await apiCall('POST', '/products', productData);
        console.log(`✅ Created product: ${product.name} (ID: ${created.id}) - ₹${product.price}`);
        
        // Create product variants
        const variants = getVariantsForProduct(product.name);
        for (const variant of variants) {
          const variantData = {
            productId: created.id,
            size: variant.size,
            stockQuantity: variant.stock
          };
          
          await apiCall('POST', '/products/variants', variantData);
          console.log(`  ➕ Added variant: ${variant.size} (Stock: ${variant.stock})`);
        }
      }
    }
    
    console.log('🎉 Data population completed successfully!');
    console.log('');
    console.log('📊 Summary:');
    console.log(`• ${categories.length} top-level categories created`);
    console.log(`• ${subcategories.length} subcategories created`);
    console.log(`• ${products.length} products created with variants`);
    console.log('• Kids Collection with Indian pricing added ✨');
    
  } catch (error) {
    console.error('❌ Error during data population:', error);
  }
}

// Helper function to get variants for products
function getVariantsForProduct(productName) {
  const variants = {
    // Necklaces
    "Diamond Pendant Necklace": [
      { size: "16 inches", stock: 15 },
      { size: "18 inches", stock: 25 },
      { size: "20 inches", stock: 10 }
    ],
    "Pearl Strand Necklace": [
      { size: "16 inches", stock: 20 },
      { size: "18 inches", stock: 30 },
      { size: "20 inches", stock: 15 }
    ],
    "Gold Chain Necklace": [
      { size: "16 inches", stock: 40 },
      { size: "18 inches", stock: 50 },
      { size: "20 inches", stock: 25 }
    ],
    
    // Rings
    "Solitaire Diamond Ring": [
      { size: "Size 5", stock: 3 },
      { size: "Size 6", stock: 5 },
      { size: "Size 7", stock: 8 },
      { size: "Size 8", stock: 5 },
      { size: "Size 9", stock: 2 }
    ],
    "Rose Gold Band Ring": [
      { size: "Size 5", stock: 12 },
      { size: "Size 6", stock: 15 },
      { size: "Size 7", stock: 20 },
      { size: "Size 8", stock: 15 },
      { size: "Size 9", stock: 8 }
    ],
    
    // Kids Collection
    "Rainbow Butterfly Necklace": [
      { size: "12 inches", stock: 25 },
      { size: "14 inches", stock: 30 },
      { size: "16 inches", stock: 20 }
    ],
    "Unicorn Charm Necklace": [
      { size: "12 inches", stock: 35 },
      { size: "14 inches", stock: 40 },
      { size: "16 inches", stock: 25 }
    ],
    "Friendship Bracelet Set": [
      { size: "Small (5-6\")", stock: 50 },
      { size: "Medium (6-7\")", stock: 45 }
    ],
    "Flower Stud Earrings": [
      { size: "Pink", stock: 25 },
      { size: "Purple", stock: 30 },
      { size: "Blue", stock: 20 },
      { size: "Yellow", stock: 15 }
    ],
    "Adjustable Flower Ring": [
      { size: "Pink", stock: 30 },
      { size: "Purple", stock: 25 },
      { size: "Blue", stock: 20 }
    ],
    "Baby's First Bracelet": [
      { size: "Newborn (4\")", stock: 15 },
      { size: "Infant (4.5\")", stock: 20 }
    ]
  };
  
  return variants[productName] || [{ size: "One Size", stock: 10 }];
}

// Run the population
populateData();
