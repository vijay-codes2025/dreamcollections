import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import ProductDetail from '../components/products/ProductDetail';
import productService from '../services/productService';

const ProductPage = () => {
  const { productId } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const productData = await productService.getProductById(productId);
        setProduct(productData);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch product:', err);
        setError(err.response?.status === 404 ? 'Product not found' : 'Failed to load product');
        setProduct(null);
      } finally {
        setLoading(false);
      }
    };

    if (productId) {
      fetchProduct();
    }
  }, [productId]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-2xl mx-auto p-6 text-center">
        <h2 className="text-2xl font-bold mb-4 text-red-600">Error</h2>
        <p className="text-gray-700 mb-4">{error}</p>
        <Link to="/" className="text-blue-500 hover:underline">
          Go to Homepage
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto">
        <nav className="py-4 px-4">
          <Link to="/" className="text-blue-600 hover:underline">
            ← Back to Categories
          </Link>
          {product?.category && (
            <>
              <span className="mx-2 text-gray-500">/</span>
              <Link 
                to={`/category/${product.category.id}`} 
                className="text-blue-600 hover:underline"
              >
                {product.category.name}
              </Link>
            </>
          )}
        </nav>
        <ProductDetail product={product} />
      </div>
    </div>
  );
};

export default ProductPage;