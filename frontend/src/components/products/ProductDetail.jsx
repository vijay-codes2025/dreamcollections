import React, { useState } from 'react';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const ProductDetail = ({ product }) => {
  const [selectedVariant, setSelectedVariant] = useState(product.variants?.[0] || null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (!selectedVariant) {
      setMessage('Please select a variant');
      return;
    }

    if (selectedVariant.stockQuantity < quantity) {
      setMessage('Not enough stock available');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      await addToCart(selectedVariant.id, quantity);
      setMessage('Added to cart successfully!');
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage(error.response?.data?.message || 'Failed to add to cart');
    } finally {
      setLoading(false);
    }
  };

  if (!product) {
    return <div>Product not found</div>;
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <img
            src={product.imageUrl || 'https://via.placeholder.com/500'}
            alt={product.name}
            className="w-full h-96 object-cover rounded-lg"
          />
        </div>
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-4">{product.name}</h1>
          <p className="text-2xl font-semibold text-blue-600 mb-4">
            ${product.price ? parseFloat(product.price).toFixed(2) : '0.00'}
          </p>
          <p className="text-gray-700 mb-6">{product.description}</p>

          {product.variants && product.variants.length > 0 && (
            <div className="mb-6">
              <h3 className="text-lg font-medium mb-2">Size:</h3>
              <div className="flex flex-wrap gap-2">
                {product.variants.map((variant) => (
                  <button
                    key={variant.id}
                    onClick={() => setSelectedVariant(variant)}
                    className={`px-4 py-2 border rounded-md ${
                      selectedVariant?.id === variant.id
                        ? 'border-blue-500 bg-blue-50 text-blue-700'
                        : 'border-gray-300 hover:border-gray-400'
                    } ${variant.stockQuantity === 0 ? 'opacity-50 cursor-not-allowed' : ''}`}
                    disabled={variant.stockQuantity === 0}
                  >
                    {variant.size}
                    {variant.stockQuantity === 0 && ' (Out of Stock)'}
                  </button>
                ))}
              </div>
              {selectedVariant && (
                <p className="text-sm text-gray-600 mt-2">
                  Stock: {selectedVariant.stockQuantity} available
                </p>
              )}
            </div>
          )}

          <div className="mb-6">
            <label htmlFor="quantity" className="block text-lg font-medium mb-2">
              Quantity:
            </label>
            <input
              type="number"
              id="quantity"
              min="1"
              max={selectedVariant?.stockQuantity || 1}
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
              className="w-20 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {message && (
            <div className={`mb-4 p-3 rounded-md ${
              message.includes('success') 
                ? 'bg-green-100 text-green-700' 
                : 'bg-red-100 text-red-700'
            }`}>
              {message}
            </div>
          )}

          <button
            onClick={handleAddToCart}
            disabled={loading || !selectedVariant || selectedVariant.stockQuantity === 0}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-md transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Adding to Cart...' : 'Add to Cart'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;