import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const CategoryItem = ({ category, level = 0, onSelectCategory }) => { // onSelectCategory kept for flexibility if needed elsewhere
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();

  if (!category) {
    return null;
  }

  const hasSubCategories = category.subCategories && category.subCategories.length > 0;

  const handleToggleExpand = (e) => {
    e.stopPropagation(); // Prevent navigation if we are just toggling
    if (hasSubCategories) {
      setIsOpen(!isOpen);
    }
  };

  // This function will handle the actual navigation or selection
  const handleCategoryClick = () => {
    if (onSelectCategory) { // If a specific callback is provided, use it
        onSelectCategory(category);
    } else { // Default behavior: navigate
        navigate(`/category/${category.id}`);
    }
  };

  const paddingLeft = `${level * 20}px`;

  return (
    <li className="my-1">
      <div
        className="flex items-center justify-between p-2 rounded hover:bg-gray-200 group"
        style={{ paddingLeft }}
      >
        {/* Make the category name clickable for navigation */}
        <span
            onClick={handleCategoryClick}
            className="flex-grow cursor-pointer hover:text-blue-600"
            title={`View products in ${category.name}`}
        >
          {category.name}
        </span>

        {hasSubCategories && (
          <button
            onClick={handleToggleExpand}
            className="ml-2 text-xs px-2 py-1 rounded bg-gray-300 hover:bg-gray-400 focus:outline-none"
            aria-expanded={isOpen}
            aria-controls={`subcategories-for-${category.id}`}
            title={isOpen ? `Collapse ${category.name}` : `Expand ${category.name}`}
          >
            {isOpen ? '-' : '+'}
          </button>
        )}
      </div>
      {hasSubCategories && isOpen && (
        <ul id={`subcategories-for-${category.id}`} className="ml-4 border-l border-gray-300">
          {category.subCategories.map((subCategory) => (
            <CategoryItem
              key={subCategory.id}
              category={subCategory}
              level={level + 1}
              onSelectCategory={onSelectCategory} // Pass down the selector
            />
          ))}
        </ul>
      )}
    </li>
  );
};

export default CategoryItem;
