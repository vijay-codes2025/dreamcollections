import React, { useState } from 'react';

const CategoryFilter = ({ categories, selectedCategory, onCategoryChange }) => {
  const [expandedCategories, setExpandedCategories] = useState(new Set()); // No categories expanded by default

  const toggleCategory = (categoryId) => {
    const newExpanded = new Set(expandedCategories);
    if (newExpanded.has(categoryId.toString())) {
      newExpanded.delete(categoryId.toString());
    } else {
      newExpanded.add(categoryId.toString());
    }
    setExpandedCategories(newExpanded);
  };

  const getCategoryIcon = (categoryName) => {
    const icons = {
      "Women's Jewelry": "👩",
      "Men's Jewelry": "👨",
      "Kids Collection": "🧒",
      "Luxury Collection": "💎",
      "Wedding Collection": "💍",
      "Necklaces": "📿",
      "Rings": "💍",
      "Earrings": "👂",
      "Bracelets": "🔗",
      "Kids Necklaces": "🌈",
      "Kids Bracelets": "🎨",
      "Kids Earrings": "🌟",
      "Kids Rings": "🦄",
      "Baby Jewelry": "👶"
    };
    return icons[categoryName] || "📦";
  };

  const getProductCount = (category) => {
    // Product counts for all categories (ideally this would come from the API)
    const counts = {
      // Main Categories
      1: 11,  // Women's Jewelry
      2: 4,   // Men's Jewelry
      3: 13,  // Kids Collection
      4: 1,   // Luxury Collection
      5: 4,   // Wedding Collection

      // Women's Jewelry Subcategories
      6: 3,   // Necklaces
      7: 3,   // Rings
      8: 3,   // Earrings
      9: 2,   // Bracelets

      // Men's Jewelry Subcategories
      10: 2,  // Men's Rings
      11: 2,  // Men's Chains
      12: 0,  // Cufflinks

      // Kids Collection Subcategories
      13: 3,  // Kids Necklaces
      14: 3,  // Kids Bracelets
      15: 3,  // Kids Earrings
      16: 2,  // Kids Rings
      17: 2,  // Baby Jewelry

      // Luxury Collection Subcategories
      18: 1,  // Diamond Collection
      19: 0,  // Gold Collection
      20: 0,  // Platinum Collection

      // Wedding Collection Subcategories
      21: 2,  // Engagement Rings
      22: 2   // Wedding Bands
    };
    return counts[category.id] || 0;
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h4 className="font-medium text-gray-900">Categories</h4>
        {selectedCategory && (
          <button
            onClick={() => onCategoryChange('')}
            className="text-xs text-blue-600 hover:text-blue-700"
          >
            Clear
          </button>
        )}
      </div>
      
      <div className="space-y-1">
        <button
          onClick={() => onCategoryChange('')}
          className={`w-full text-left px-3 py-2 rounded-md text-sm transition-colors flex items-center justify-between ${
            selectedCategory === ''
              ? 'bg-blue-100 text-blue-700 border border-blue-200'
              : 'text-gray-600 hover:bg-gray-50 border border-transparent'
          }`}
        >
          <span className="flex items-center">
            <span className="mr-2">🏪</span>
            All Categories
          </span>
        </button>
        
        {categories.map((category) => (
          <div key={category.id} className="space-y-1">
            <div className="flex items-center">
              <button
                onClick={() => onCategoryChange(category.id.toString())}
                className={`flex-1 text-left px-3 py-2 rounded-md text-sm transition-colors flex items-center justify-between ${
                  selectedCategory === category.id.toString()
                    ? 'bg-blue-100 text-blue-700 border border-blue-200'
                    : 'text-gray-600 hover:bg-gray-50 border border-transparent'
                }`}
              >
                <span className="flex items-center">
                  <span className="mr-2">{getCategoryIcon(category.name)}</span>
                  {category.name}
                </span>
                {getProductCount(category) > 0 && (
                  <span className="text-xs bg-gray-200 text-gray-600 px-2 py-1 rounded-full">
                    {getProductCount(category)}
                  </span>
                )}
              </button>
              
              {category.subCategories && category.subCategories.length > 0 && (
                <button
                  onClick={() => toggleCategory(category.id)}
                  className="ml-2 p-1 text-gray-400 hover:text-gray-600"
                >
                  <svg
                    className={`w-4 h-4 transition-transform ${
                      expandedCategories.has(category.id.toString()) ? 'rotate-90' : ''
                    }`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                  </svg>
                </button>
              )}
            </div>
            
            {category.subCategories && 
             category.subCategories.length > 0 && 
             expandedCategories.has(category.id.toString()) && (
              <div className="ml-6 space-y-1 border-l-2 border-gray-100 pl-3">
                {category.subCategories.map((subCategory) => (
                  <button
                    key={subCategory.id}
                    onClick={() => onCategoryChange(subCategory.id.toString())}
                    className={`w-full text-left px-3 py-2 rounded-md text-sm transition-colors flex items-center justify-between ${
                      selectedCategory === subCategory.id.toString()
                        ? 'bg-blue-100 text-blue-700 border border-blue-200'
                        : 'text-gray-600 hover:bg-gray-50 border border-transparent'
                    }`}
                  >
                    <span className="flex items-center">
                      <span className="mr-2">{getCategoryIcon(subCategory.name)}</span>
                      {subCategory.name}
                    </span>
                    {getProductCount(subCategory) > 0 && (
                      <span className="text-xs bg-gray-200 text-gray-600 px-2 py-1 rounded-full">
                        {getProductCount(subCategory)}
                      </span>
                    )}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default CategoryFilter;
