import React, { useState } from 'react';
import CategoryList from '../components/categories/CategoryList';
import ProductList from '../components/products/ProductList';
import { useNavigate } from 'react-router-dom';

const HomePage = () => {
  const [selectedCategoryForProductView, setSelectedCategoryForProductView] = useState(null);
  const navigate = useNavigate();

  const handleCategoryNavigation = (category) => {
    // Navigate to a dedicated category page URL
    navigate(`/category/${category.id}`);
  };

  // This is just an example if you want to show some products on the homepage
  // For instance, featured products or products from a default category.
  // For now, let's not select a default category for products on homepage,
  // users will click a category to see products on a new "page" (route).

  return (
    <div className="container mx-auto p-4 flex flex-col md:flex-row gap-6">
      <aside className="w-full md:w-1/4 lg:w-1/5">
        <h2 className="text-xl font-semibold mb-3 p-2 bg-gray-200 rounded-t-lg">Browse Categories</h2>
        <CategoryList onCategorySelect={handleCategoryNavigation} />
      </aside>

      <section className="w-full md:w-3/4 lg:w-4/5">
        {/*
          This section could show featured products, promotions, or products from a selected category.
          If a category is selected via URL param on a different page, this content would change.
          For HomePage, we might show a welcome message or all products initially.
        */}
        <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-2xl font-semibold mb-4">Welcome to DreamCollections!</h2>
            <p className="text-gray-700">
              Select a category from the sidebar to browse our extensive collection of jewelry.
            </p>
            {/* Optionally, display a default ProductList here, e.g., all products or featured */}
            {/* <ProductList /> */}
        </div>
      </section>
    </div>
  );
};

export default HomePage;
