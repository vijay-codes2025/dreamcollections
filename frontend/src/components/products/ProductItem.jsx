import React from 'react';

const ProductItem = ({ product }) => {
  if (!product) {
    return null;
  }

  // Fallback image if product.imageUrl is not available
  const imageUrl = product.imageUrl || 'https://via.placeholder.com/150?text=No+Image';

  return (
    <div className="border rounded-lg p-4 shadow hover:shadow-lg transition-shadow duration-200 ease-in-out">
      <img
        src={imageUrl}
        alt={product.name}
        className="w-full h-48 object-cover rounded-md mb-4"
        onError={(e) => { e.target.onerror = null; e.target.src='https://via.placeholder.com/150?text=Image+Error'; }}
      />
      <h3 className="text-lg font-semibold mb-1">{product.name}</h3>
      <p className="text-gray-600 text-sm mb-2 truncate" title={product.description}>
        {product.description || 'No description available.'}
      </p>
      <p className="text-xl font-bold text-blue-600 mb-3">${product.price ? parseFloat(product.price).toFixed(2) : 'N/A'}</p>
      {/* Display first variant's color and size if available, as an example */}
      {product.variants && product.variants.length > 0 && (
        <div className="text-xs text-gray-500">
          {product.variants[0].color && <span>Color: {product.variants[0].color} </span>}
          {product.variants[0].size && <span>Size: {product.variants[0].size} </span>}
          {product.variants[0].stockQuantity && <span>Stock: {product.variants[0].stockQuantity}</span>}
        </div>
      )}
      <button
        className="mt-2 w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded transition-colors duration-200"
        onClick={() => alert(`Added ${product.name} to cart (not implemented yet)`)}
      >
        Add to Cart
      </button>
    </div>
  );
};

export default ProductItem;
