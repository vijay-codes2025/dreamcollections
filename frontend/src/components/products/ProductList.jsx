import React, { useState, useEffect } from 'react';
import productService from '../../services/productService';
import ProductItem from './ProductItem';

const ProductList = ({ categoryId, categoryName }) => {
  const [productsPage, setProductsPage] = useState(null); // To store the whole page object
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const productsPerPage = 9; // Or get from backend if dynamic

  useEffect(() => {
    if (!categoryId) {
      setProductsPage(null); // Clear products if no category is selected
      return;
    }

    const fetchProducts = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await productService.getProductsByCategoryId(categoryId, currentPage, productsPerPage);
        setProductsPage(data);
      } catch (err) {
        console.error(`Error fetching products for category ${categoryId}:`, err);
        setError(err.message || 'Failed to fetch products');
        setProductsPage(null);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, [categoryId, currentPage]); // Refetch when categoryId or currentPage changes

  const handleNextPage = () => {
    if (productsPage && !productsPage.last) {
      setCurrentPage(prevPage => prevPage + 1);
    }
  };

  const handlePreviousPage = () => {
    if (productsPage && !productsPage.first) {
      setCurrentPage(prevPage => prevPage - 1);
    }
  };

  if (!categoryId) {
    return (
      <div className="p-4 text-center text-gray-500">
        Select a category to view products.
      </div>
    );
  }

  if (loading) {
    return <div className="p-4 text-center">Loading products...</div>;
  }

  if (error) {
    return <div className="p-4 text-center text-red-500">Error: {error}</div>;
  }

  if (!productsPage || !productsPage.content || productsPage.content.length === 0) {
    return (
      <div className="p-4 text-center text-gray-500">
        No products found for {categoryName || 'this category'}.
      </div>
    );
  }

  return (
    <div>
      <h2 className="text-2xl font-semibold mb-4">
        Products in {categoryName || 'Selected Category'}
        {productsPage && productsPage.totalElements > 0 && (
          <span className="text-lg text-gray-600 ml-2">({productsPage.totalElements} items)</span>
        )}
      </h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {productsPage.content.map((product) => (
          <ProductItem key={product.id} product={product} />
        ))}
      </div>
      {/* Pagination Controls */}
      {productsPage.totalPages > 1 && (
        <div className="mt-8 flex justify-center items-center space-x-4">
          <button
            onClick={handlePreviousPage}
            disabled={productsPage.first}
            className="px-4 py-2 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Previous
          </button>
          <span className="text-gray-700">
            Page {productsPage.number + 1} of {productsPage.totalPages}
          </span>
          <button
            onClick={handleNextPage}
            disabled={productsPage.last}
            className="px-4 py-2 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default ProductList;
