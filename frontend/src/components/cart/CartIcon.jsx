import React, { useState } from 'react';
import { useCart } from '../../context/CartContext';
import { Link } from 'react-router-dom';
import CheckoutModal from '../checkout/CheckoutModal';

const CartIcon = () => {
  const { cart } = useCart();
  const [showCheckout, setShowCheckout] = useState(false);

  const itemCount = cart?.totalItems || 0;

  return (
    <>
      <div className="flex items-center space-x-2">
        {/* Cart Icon */}
        <Link to="/cart" className="relative p-2 text-gray-600 hover:text-blue-600 transition-colors">
          <svg
            className="w-6 h-6"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M3 3h2l.4 2M7 13h10l4-8H5.4m0 0L7 13m0 0l-1.5 6M7 13l-1.5 6m0 0h9m-9 0h9"
            />
          </svg>
          {itemCount > 0 && (
            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
              {itemCount > 99 ? '99+' : itemCount}
            </span>
          )}
        </Link>

        {/* Checkout Button - Only show if cart has items */}
        {itemCount > 0 && (
          <button
            onClick={() => setShowCheckout(true)}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
          >
            Checkout
          </button>
        )}
      </div>

      {/* Checkout Modal */}
      <CheckoutModal
        isOpen={showCheckout}
        onClose={() => setShowCheckout(false)}
      />
    </>
  );
};

export default CartIcon;