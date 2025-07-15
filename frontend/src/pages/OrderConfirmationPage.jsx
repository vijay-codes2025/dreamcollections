import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import orderService from '../services/orderService';

const OrderConfirmationPage = () => {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        // Try to fetch from backend first
        const orderData = await orderService.getOrderById(orderId);
        setOrder(orderData);
      } catch (backendError) {
        console.log('Backend order fetch failed, checking localStorage:', backendError);
        // Fallback to localStorage for mock orders
        try {
          const storedOrders = JSON.parse(localStorage.getItem('userOrders') || '[]');
          const foundOrder = storedOrders.find(order => order.id.toString() === orderId);
          if (foundOrder) {
            setOrder(foundOrder);
          } else {
            setError('Order not found');
          }
        } catch (localError) {
          setError('Failed to load order details');
        }
      } finally {
        setLoading(false);
      }
    };

    if (orderId) {
      fetchOrder();
    }
  }, [orderId]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="max-w-2xl mx-auto p-6 text-center">
        <h2 className="text-2xl font-bold mb-4 text-red-600">Error</h2>
        <p className="text-gray-700 mb-4">{error || 'Order not found'}</p>
        <Link to="/" className="text-blue-500 hover:underline">
          Go to Homepage
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-lg shadow-md p-8">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-green-600 mb-2">Order Confirmed!</h1>
          <p className="text-gray-600">Thank you for your purchase</p>
        </div>

        <div className="border-t border-b py-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold mb-2">Order Details</h3>
              <p className="text-sm text-gray-600">Order ID: #{order.id}</p>
              <p className="text-sm text-gray-600">Status: {order.status}</p>
              <p className="text-sm text-gray-600">
                Date: {new Date(order.createdAt).toLocaleDateString()}
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-2">Total</h3>
              <p className="text-2xl font-bold text-blue-600">
                ₹{order.totalAmount ? parseFloat(order.totalAmount).toFixed(2) : '0.00'}
              </p>
            </div>
          </div>
        </div>

        <div className="mb-6">
          <h3 className="font-semibold mb-4">Order Items</h3>
          <div className="space-y-3">
            {order.items?.map((item) => (
              <div key={item.id} className="flex justify-between items-center py-2 border-b">
                <div>
                  <p className="font-medium">{item.productName}</p>
                  <p className="text-sm text-gray-600">
                    Size: {item.variantSize} × {item.quantity}
                  </p>
                </div>
                <p className="font-semibold">
                  ₹{item.priceAtPurchase ? (parseFloat(item.priceAtPurchase) * item.quantity).toFixed(2) : '0.00'}
                </p>
              </div>
            ))}
          </div>
        </div>

        <div className="text-center space-y-4">
          <p className="text-gray-600">
            You will receive an email confirmation shortly with tracking information.
          </p>
          <div className="space-x-4">
            <Link
              to="/orders"
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-6 rounded-md transition-colors duration-200"
            >
              View My Orders
            </Link>
            <Link
              to="/"
              className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-2 px-6 rounded-md transition-colors duration-200"
            >
              Continue Shopping
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;