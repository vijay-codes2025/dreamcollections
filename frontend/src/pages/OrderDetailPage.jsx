import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import ProtectedRoute from '../components/auth/ProtectedRoute';
import orderService from '../services/orderService';

const OrderDetailPage = () => {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        const orderData = await orderService.getOrderById(orderId);
        setOrder(orderData);
      } catch (err) {
        setError(err.response?.status === 404 ? 'Order not found' : 'Failed to load order');
      } finally {
        setLoading(false);
      }
    };

    if (orderId) {
      fetchOrder();
    }
  }, [orderId]);

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING_PAYMENT':
        return 'text-yellow-600 bg-yellow-100';
      case 'PAID':
      case 'PROCESSING':
        return 'text-blue-600 bg-blue-100';
      case 'SHIPPED':
        return 'text-purple-600 bg-purple-100';
      case 'DELIVERED':
        return 'text-green-600 bg-green-100';
      case 'CANCELLED':
      case 'FAILED':
        return 'text-red-600 bg-red-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

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
        <Link to="/orders" className="text-blue-500 hover:underline">
          Back to Orders
        </Link>
      </div>
    );
  }

  return (
    <ProtectedRoute>
      <div className="max-w-4xl mx-auto p-6">
        <nav className="mb-6">
          <Link to="/orders" className="text-blue-600 hover:underline">
            ← Back to Orders
          </Link>
        </nav>

        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex justify-between items-start mb-6">
            <div>
              <h1 className="text-3xl font-bold mb-2">Order #{order.id}</h1>
              <p className="text-gray-600">
                Placed on {new Date(order.createdAt).toLocaleDateString()}
              </p>
            </div>
            <span className={`inline-flex px-3 py-1 text-sm font-semibold rounded-full ${getStatusColor(order.status)}`}>
              {order.status.replace('_', ' ')}
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <div>
              <h3 className="font-semibold mb-3">Order Summary</h3>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Order ID:</span>
                  <span>#{order.id}</span>
                </div>
                <div className="flex justify-between">
                  <span>Status:</span>
                  <span>{order.status.replace('_', ' ')}</span>
                </div>
                <div className="flex justify-between">
                  <span>Order Date:</span>
                  <span>{new Date(order.createdAt).toLocaleDateString()}</span>
                </div>
                {order.updatedAt && (
                  <div className="flex justify-between">
                    <span>Last Updated:</span>
                    <span>{new Date(order.updatedAt).toLocaleDateString()}</span>
                  </div>
                )}
              </div>
            </div>

            <div>
              <h3 className="font-semibold mb-3">Shipping Address</h3>
              <div className="text-sm text-gray-600">
                {order.shippingAddress ? (
                  <div>
                    <p>{order.shippingAddress}</p>
                  </div>
                ) : (
                  <p>No shipping address available</p>
                )}
              </div>
            </div>
          </div>

          <div className="mb-8">
            <h3 className="font-semibold mb-4">Order Items</h3>
            <div className="space-y-4">
              {order.items?.map((item) => (
                <div key={item.id} className="flex items-center justify-between py-4 border-b">
                  <div className="flex items-center">
                    <img
                      src={item.productImageUrl || 'https://via.placeholder.com/80'}
                      alt={item.productName}
                      className="w-16 h-16 object-cover rounded-md mr-4"
                    />
                    <div>
                      <h4 className="font-medium">{item.productName}</h4>
                      <p className="text-sm text-gray-600">Size: {item.variantSize}</p>
                      <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">
                      ${item.priceAtPurchase ? parseFloat(item.priceAtPurchase).toFixed(2) : '0.00'} each
                    </p>
                    <p className="text-sm text-gray-600">
                      Total: ${item.priceAtPurchase ? (parseFloat(item.priceAtPurchase) * item.quantity).toFixed(2) : '0.00'}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="border-t pt-6">
            <div className="flex justify-between items-center">
              <span className="text-xl font-bold">Total Amount:</span>
              <span className="text-xl font-bold text-blue-600">
                ${order.totalAmount ? parseFloat(order.totalAmount).toFixed(2) : '0.00'}
              </span>
            </div>
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
};

export default OrderDetailPage;