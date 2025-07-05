import React, { useState, useEffect } from 'react';
import categoryService from '../../services/categoryService';
import CategoryItem from './CategoryItem';

// onCategorySelect is passed down from parent page (HomePage, CategoryPage)
// and will be used by CategoryItem to trigger navigation.
const CategoryList = ({ onCategorySelect }) => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setLoading(true);
        const topLevelCategories = await categoryService.getTopLevelCategories();
        setCategories(topLevelCategories || []);
        setError(null);
      } catch (err) {
        console.error("Error fetching categories:", err);
        setError(err.message || 'Failed to fetch categories');
        setCategories([]);
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  // The actual navigation is now handled inside CategoryItem using useNavigate,
  // triggered by the onCategorySelect passed down to it.
  // This component just ensures that prop is passed along.

  if (loading) {
    return <div className="p-4 text-center">Loading categories...</div>;
  }

  if (error) {
    return <div className="p-4 text-center text-red-500">Error: {error}</div>;
  }

  if (!categories.length) {
    return <div className="p-4 text-center text-gray-500">No categories found.</div>;
  }

  return (
    <div className="p-4 bg-white shadow-md rounded-lg">
      {/* The h2 title is now typically part of the Page component (HomePage, CategoryPage) */}
      {/* <h2 className="text-xl font-semibold mb-3">Categories</h2> */}
      <ul className="list-none">
        {categories.map((category) => (
          <CategoryItem
            key={category.id}
            category={category}
            onSelectCategory={onCategorySelect} // Pass the navigation handler down
            level={0}
          />
        ))}
      </ul>
    </div>
  );
};

export default CategoryList;
