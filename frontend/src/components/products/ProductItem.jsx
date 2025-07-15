import React from 'react';
import { Link } from 'react-router-dom';

const ProductItem = ({ product }) => {
  if (!product) {
    return null;
  }

  const imageUrl = product.imageUrl || 'https://via.placeholder.com/300x300?text=No+Image';
  const hasStock = product.variants?.some(variant => variant.stockQuantity > 0);

  return (
    <div className="border rounded-lg p-4 shadow hover:shadow-lg transition-shadow duration-200 ease-in-out bg-white">
      <Link to={`/product/${product.id}`}>
        <img
          src={imageUrl}
          alt={product.name}
          className="w-full h-48 object-cover rounded-md mb-4 hover:opacity-90 transition-opacity"
          onError={(e) => { 
            e.target.onerror = null; 
            e.target.src='https://via.placeholder.com/300x300?text=Image+Error'; 
          }}
        />
      </Link>
      
      <Link to={`/product/${product.id}`} className="block">
        <h3 className="text-lg font-semibold mb-1 hover:text-blue-600 transition-colors">
          {product.name}
        </h3>
      </Link>
      
      <p className="text-gray-600 text-sm mb-2 line-clamp-2" title={product.description}>
        {product.description || 'No description available.'}
      </p>
      
      <p className="text-xl font-bold text-blue-600 mb-3">
        ${product.price ? parseFloat(product.price).toFixed(2) : 'N/A'}
      </p>

      {product.variants && product.variants.length > 0 && (
        <div className="text-xs text-gray-500 mb-3">
          <div className="flex flex-wrap gap-1">
            {product.variants.slice(0, 3).map((variant) => (
              <span key={variant.id} className="bg-gray-100 px-2 py-1 rounded">
                {variant.size}
              </span>
            ))}
            {product.variants.length > 3 && (
              <span className="text-gray-400">+{product.variants.length - 3} more</span>
            )}
          </div>
        </div>
      )}

      <div className="flex items-center justify-between">
        <span className={`text-sm ${hasStock ? 'text-green-600' : 'text-red-600'}`}>
          {hasStock ? 'In Stock' : 'Out of Stock'}
        </span>
        <Link
          to={`/product/${product.id}`}
          className="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded transition-colors duration-200"
        >
          View Details
        </Link>
      </div>
    </div>
  );
};

export default ProductItem;