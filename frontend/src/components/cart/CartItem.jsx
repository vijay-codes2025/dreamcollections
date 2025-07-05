import React, { useState } from 'react';
import { useCart } from '../../context/CartContext';

const CartItem = ({ item }) => {
  const [loading, setLoading] = useState(false);
  const { updateQuantity, removeFromCart } = useCart();

  const handleQuantityChange = async (newQuantity) => {
    if (newQuantity < 0) return;
    
    setLoading(true);
    try {
      if (newQuantity === 0) {
        await removeFromCart(item.productVariantId);
      } else {
        await updateQuantity(item.productVariantId, newQuantity);
      }
    } catch (error) {
      console.error('Failed to update cart item:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async () => {
    setLoading(true);
    try {
      await removeFromCart(item.productVariantId);
    } catch (error) {
      console.error('Failed to remove cart item:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center py-4 border-b border-gray-200">
      <img
        src={item.productImageUrl || 'https://via.placeholder.com/80'}
        alt={item.productName}
        className="w-20 h-20 object-cover rounded-md"
      />
      <div className="flex-1 ml-4">
        <h3 className="text-lg font-medium text-gray-900">{item.productName}</h3>
        <p className="text-sm text-gray-500">Size: {item.variantSize}</p>
        <p className="text-lg font-semibold text-blue-600">
          ${item.unitPrice ? parseFloat(item.unitPrice).toFixed(2) : '0.00'}
        </p>
      </div>
      <div className="flex items-center space-x-2">
        <button
          onClick={() => handleQuantityChange(item.quantity - 1)}
          disabled={loading}
          className="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center disabled:opacity-50"
        >
          -
        </button>
        <span className="w-12 text-center">{item.quantity}</span>
        <button
          onClick={() => handleQuantityChange(item.quantity + 1)}
          disabled={loading}
          className="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center disabled:opacity-50"
        >
          +
        </button>
        <button
          onClick={handleRemove}
          disabled={loading}
          className="ml-4 text-red-600 hover:text-red-800 disabled:opacity-50"
        >
          Remove
        </button>
      </div>
      <div className="ml-4 text-right">
        <p className="text-lg font-semibold">
          ${item.subtotal ? parseFloat(item.subtotal).toFixed(2) : '0.00'}
        </p>
      </div>
    </div>
  );
};

export default CartItem;