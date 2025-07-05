import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import CategoryList from '../components/categories/CategoryList';
import ProductList from '../components/products/ProductList';
import categoryService from '../services/categoryService';

const CategoryPage = () => {
  const { categoryId } = useParams();
  const navigate = useNavigate();
  const [currentCategory, setCurrentCategory] = useState(null);
  const [loadingCategoryDetails, setLoadingCategoryDetails] = useState(true); // Start true
  const [categoryError, setCategoryError] = useState(null);

  useEffect(() => {
    if (categoryId) {
      setLoadingCategoryDetails(true);
      setCategoryError(null);
      categoryService.getCategoryById(categoryId)
        .then(data => {
          setCurrentCategory(data);
        })
        .catch(err => {
          console.error("Failed to fetch category details for ID:", categoryId, err);
          if (err.response && err.response.status === 404) {
            setCategoryError(`Category with ID ${categoryId} not found.`);
          } else {
            setCategoryError('Error loading category details.');
          }
          setCurrentCategory(null);
        })
        .finally(() => {
          setLoadingCategoryDetails(false);
        });
    } else {
      // Should not happen if route is specific like /category/:id
      // but as a fallback:
      setCategoryError("No category ID provided.");
      setLoadingCategoryDetails(false);
      setCurrentCategory(null);
    }
  }, [categoryId]);

  const handleCategoryNavigation = (category) => {
    navigate(`/category/${category.id}`);
  };

  return (
    <div className="container mx-auto p-4 flex flex-col md:flex-row gap-6">
      <aside className="w-full md:w-1/4 lg:w-1/5">
        <h2 className="text-xl font-semibold mb-3 p-2 bg-gray-200 rounded-t-lg">Categories</h2>
        <CategoryList onCategorySelect={handleCategoryNavigation} />
      </aside>

      <section className="w-full md:w-3/4 lg:w-4/5">
        {loadingCategoryDetails ? (
          <div className="text-center p-10">
            <p className="text-lg text-gray-500">Loading category information...</p>
            {/* Consider adding a spinner here */}
          </div>
        ) : categoryError ? (
          <div className="bg-white p-6 rounded-lg shadow-md text-center">
            <h2 className="text-2xl font-semibold mb-4 text-red-600">Error</h2>
            <p className="text-gray-700 mb-4">{categoryError}</p>
            <Link to="/" className="text-blue-500 hover:underline">Go to Homepage</Link>
          </div>
        ) : currentCategory ? (
          <ProductList
            key={categoryId}
            categoryId={categoryId}
            categoryName={currentCategory.name}
          />
        ) : (
           // Fallback, should ideally be covered by loading or error state
          <div className="bg-white p-6 rounded-lg shadow-md text-center">
            <p className="text-gray-700">Please select a category or verify the URL.</p>
          </div>
        )}
      </section>
    </div>
  );
};

export default CategoryPage;
