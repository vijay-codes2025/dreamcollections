import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import CartItem from '../components/cart/CartItem';

const CartPage = () => {
  const { cart, loading, clearCart } = useCart();

  const handleClearCart = async () => {
    if (window.confirm('Are you sure you want to clear your cart?')) {
      try {
        await clearCart();
      } catch (error) {
        console.error('Failed to clear cart:', error);
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto p-6 text-center">
        <h1 className="text-3xl font-bold mb-4">Your Cart</h1>
        <p className="text-gray-600 mb-6">Your cart is empty</p>
        <Link
          to="/"
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-6 rounded-md transition-colors duration-200"
        >
          Continue Shopping
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Your Cart</h1>
        <button
          onClick={handleClearCart}
          className="text-red-600 hover:text-red-800 text-sm"
        >
          Clear Cart
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="space-y-4">
          {cart.items.map((item) => (
            <CartItem key={item.cartItemId} item={item} />
          ))}
        </div>

        <div className="border-t pt-6 mt-6">
          <div className="flex justify-between items-center mb-4">
            <span className="text-lg font-semibold">Total Items:</span>
            <span className="text-lg">{cart.totalItemsCount}</span>
          </div>
          <div className="flex justify-between items-center mb-6">
            <span className="text-xl font-bold">Total:</span>
            <span className="text-xl font-bold text-blue-600">
              ${cart.totalPrice ? parseFloat(cart.totalPrice).toFixed(2) : '0.00'}
            </span>
          </div>

          <div className="flex space-x-4">
            <Link
              to="/"
              className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 px-6 rounded-md text-center transition-colors duration-200"
            >
              Continue Shopping
            </Link>
            <Link
              to="/checkout"
              className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-md text-center transition-colors duration-200"
            >
              Proceed to Checkout
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartPage;