import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import orderService from '../services/orderService';

const OrderDetailPage = () => {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        // First try to get order from localStorage (for mock orders)
        const localOrders = JSON.parse(localStorage.getItem('userOrders') || '[]');
        const localOrder = localOrders.find(order => order.id.toString() === orderId);
        
        if (localOrder) {
          console.log('✅ Found order in localStorage:', localOrder);
          setOrder(localOrder);
          setLoading(false);
          return;
        }

        // If not found locally, try backend API
        console.log('🔍 Order not found locally, trying backend API...');
        const orderData = await orderService.getOrderById(orderId);
        setOrder(orderData);
      } catch (err) {
        console.error('❌ Failed to load order:', err);
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
      case 'CONFIRMED':
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

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-red-600 mb-4">Error</h1>
          <p className="text-gray-600 mb-4">{error}</p>
          <Link to="/orders" className="text-blue-600 hover:underline">
            Back to Orders
          </Link>
        </div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-600 mb-4">Order Not Found</h1>
          <Link to="/orders" className="text-blue-600 hover:underline">
            Back to Orders
          </Link>
        </div>
      </div>
    );
  }

  return (
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
            <h2 className="text-xl font-semibold mb-4">Customer Information</h2>
            <div className="space-y-2">
              <p><span className="font-medium">Name:</span> {order.customerName}</p>
              <p><span className="font-medium">Phone:</span> {order.customerPhone}</p>
              {order.customerEmail && (
                <p><span className="font-medium">Email:</span> {order.customerEmail}</p>
              )}
              <p><span className="font-medium">Address:</span> {order.shippingAddress}</p>
            </div>
          </div>

          <div>
            <h2 className="text-xl font-semibold mb-4">Order Summary</h2>
            <div className="space-y-2">
              <p><span className="font-medium">Order ID:</span> #{order.id}</p>
              <p><span className="font-medium">Status:</span> {order.status.replace('_', ' ')}</p>
              <p><span className="font-medium">Total Items:</span> {order.items?.length || 0}</p>
              <p><span className="font-medium">Total Amount:</span> ₹{order.totalAmount ? parseFloat(order.totalAmount).toFixed(2) : '0.00'}</p>
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4">Order Items</h2>
          <div className="space-y-4">
            {order.items?.map((item, index) => (
              <div key={index} className="flex items-center justify-between p-4 border rounded-lg">
                <div className="flex items-center space-x-4">
                  <img 
                    src={item.imageUrl || '/placeholder-product.jpg'} 
                    alt={item.name}
                    className="w-16 h-16 object-cover rounded"
                  />
                  <div>
                    <h3 className="font-semibold">{item.name}</h3>
                    <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold">
                    ₹{item.priceAtPurchase ? parseFloat(item.priceAtPurchase).toFixed(2) : '0.00'} each
                  </p>
                  <p className="text-sm text-gray-600">
                    Total: ₹{item.priceAtPurchase ? (parseFloat(item.priceAtPurchase) * item.quantity).toFixed(2) : '0.00'}
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
              ₹{order.totalAmount ? parseFloat(order.totalAmount).toFixed(2) : '0.00'}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderDetailPage;
