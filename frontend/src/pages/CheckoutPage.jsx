import React from 'react';
import ProtectedRoute from '../components/auth/ProtectedRoute';
import CheckoutForm from '../components/checkout/CheckoutForm';

const CheckoutPage = () => {
  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-gray-50">
        <CheckoutForm />
      </div>
    </ProtectedRoute>
  );
};

export default CheckoutPage;