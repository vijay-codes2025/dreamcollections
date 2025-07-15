import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { formatPrice } from '../../utils/currency';

const ProductCard = ({ product }) => {
  const { isAuthenticated } = useAuth();
  const { addToCart } = useCart();
  const [isAddingToCart, setIsAddingToCart] = useState(false);

  if (!product) {
    return null;
  }

  // Get the first variant for display (assuming products have at least one variant)
  const firstVariant = product.variants && product.variants.length > 0 ? product.variants[0] : null;
  const price = firstVariant ? firstVariant.price : 0;
  const stockQuantity = firstVariant ? firstVariant.stockQuantity : 0;
  const isInStock = stockQuantity > 0;

  const handleAddToCart = async (e) => {
    e.preventDefault(); // Prevent navigation when clicking add to cart
    e.stopPropagation();

    if (!firstVariant) {
      alert('Product variant not available');
      return;
    }

    if (!isInStock) {
      alert('Product is out of stock');
      return;
    }

    setIsAddingToCart(true);
    try {
      await addToCart(firstVariant.id, 1);
      // Success feedback could be a toast notification
      alert('Item added to cart!');
    } catch (error) {
      console.error('Failed to add to cart:', error);
      alert('Failed to add item to cart. Please try again.');
    } finally {
      setIsAddingToCart(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
      <Link to={`/products/${product.id}`} className="block">
        {/* Product Image */}
        <div className="aspect-w-1 aspect-h-1 w-full overflow-hidden bg-gray-200">
          {product.imageUrl ? (
            <img
              src={product.imageUrl}
              alt={product.name}
              className="h-48 w-full object-cover object-center group-hover:opacity-75"
            />
          ) : (
            <div className="h-48 w-full bg-gray-200 flex items-center justify-center">
              <svg className="h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="p-4">
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
            {product.name}
          </h3>
          
          <p className="text-sm text-gray-600 mb-3 line-clamp-2">
            {product.description}
          </p>



          {/* Price and Stock */}
          <div className="flex items-center justify-between mb-3">
            <span className="text-xl font-bold text-gray-900">
              {formatPrice(price)}
            </span>
            <span className={`text-sm ${isInStock ? 'text-green-600' : 'text-red-600'}`}>
              {isInStock ? `${stockQuantity} in stock` : 'Out of stock'}
            </span>
          </div>
        </div>
      </Link>

      {/* Add to Cart Button */}
      <div className="p-4 pt-0">
        <button
          onClick={handleAddToCart}
          disabled={!isInStock || isAddingToCart}
          className={`w-full py-2 px-4 rounded-md font-medium transition-colors duration-200 ${
            isInStock && !isAddingToCart
              ? 'bg-blue-600 hover:bg-blue-700 text-white'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          }`}
        >
          {isAddingToCart ? (
            <div className="flex items-center justify-center">
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Adding...
            </div>
          ) : !isInStock ? (
            'Out of Stock'
          ) : (
            'Add to Cart'
          )}
        </button>
      </div>
    </div>
  );
};

export default ProductCard;
