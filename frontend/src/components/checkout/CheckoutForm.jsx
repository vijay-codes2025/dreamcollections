import React, { useState } from 'react';
import { useCart } from '../../context/CartContext';
import { useNavigate } from 'react-router-dom';
import orderService from '../../services/orderService';

const CheckoutForm = () => {
  const [formData, setFormData] = useState({
    customerEmail: '',
    customerNameSnapshot: '',
    customerPhone: '',
    paymentMethod: 'UPI',
    shippingMethod: 'STANDARD',
    shippingAddress: {
      street: '',
      addressLine2: '',
      city: '',
      stateOrProvince: '',
      postalCode: '',
      country: 'India'
    },
    billingAddress: {
      street: '',
      addressLine2: '',
      city: '',
      stateOrProvince: '',
      postalCode: '',
      country: 'India'
    }
  });
  const [useSameAddress] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { cart, clearCart } = useCart();
  const navigate = useNavigate();



  // Save user data based on phone number
  const saveUserData = (phoneNumber, userData) => {
    if (!phoneNumber) return;

    const savedUsers = JSON.parse(localStorage.getItem('savedUsers') || '{}');
    savedUsers[phoneNumber] = {
      name: userData.customerNameSnapshot,
      email: userData.customerEmail,
      address: userData.shippingAddress,
      lastUsed: new Date().toISOString()
    };
    localStorage.setItem('savedUsers', JSON.stringify(savedUsers));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.startsWith('shipping.')) {
      const field = name.split('.')[1];
      setFormData(prev => ({
        ...prev,
        shippingAddress: {
          ...prev.shippingAddress,
          [field]: value
        }
      }));
    } else if (name.startsWith('billing.')) {
      const field = name.split('.')[1];
      setFormData(prev => ({
        ...prev,
        billingAddress: {
          ...prev.billingAddress,
          [field]: value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };



  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Validate required fields
    if (!formData.customerNameSnapshot.trim()) {
      setError('Full name is required');
      setLoading(false);
      return;
    }

    if (!formData.customerPhone.trim()) {
      setError('Phone number is required');
      setLoading(false);
      return;
    }

    // Validate phone number format
    const phoneRegex = /^[0-9]{10,15}$/;
    if (!phoneRegex.test(formData.customerPhone.replace(/\s/g, ''))) {
      setError('Please enter a valid phone number (10-15 digits)');
      setLoading(false);
      return;
    }

    // Validate name contains only letters and spaces
    const nameRegex = /^[A-Za-z\s]+$/;
    if (!nameRegex.test(formData.customerNameSnapshot)) {
      setError('Name should contain only letters and spaces');
      setLoading(false);
      return;
    }

    // Validate shipping address
    if (!formData.shippingAddress.street.trim()) {
      setError('Street address is required');
      setLoading(false);
      return;
    }

    if (!formData.shippingAddress.city.trim()) {
      setError('City is required');
      setLoading(false);
      return;
    }

    if (!formData.shippingAddress.stateOrProvince.trim()) {
      setError('State/Province is required');
      setLoading(false);
      return;
    }

    if (!formData.shippingAddress.postalCode.trim()) {
      setError('Postal code is required');
      setLoading(false);
      return;
    }

    try {
      console.log('🛒 Cart data:', cart);
      console.log('📝 Form data:', formData);

      const orderData = {
        ...formData,
        items: cart.items,
        billingAddress: useSameAddress ? formData.shippingAddress : formData.billingAddress
      };

      console.log('📦 Order data:', orderData);

      // Save user data for future use
      saveUserData(formData.customerPhone, formData);

      // Always create a mock order for demo purposes (since backend might not be available)
      const totalAmount = cart.totalPrice || cart.totalAmount || cart.items.reduce((sum, item) => {
        const price = item.unitPrice || item.price || 1000;
        return sum + (price * item.quantity);
      }, 0);

      const order = {
        id: Date.now(),
        status: 'CONFIRMED',
        totalAmount: totalAmount,
        createdAt: new Date().toISOString(),
        items: cart.items.map(item => ({
          id: item.cartItemId || item.id || Date.now() + Math.random(),
          productName: item.productName || item.name || `Product ${item.productVariantId || item.productId}`,
          variantSize: item.variantSize || item.size || 'Standard',
          quantity: item.quantity || 1,
          priceAtPurchase: item.unitPrice || item.price || 1000
        })),
        customerEmail: formData.customerEmail,
        customerNameSnapshot: formData.customerNameSnapshot,
        customerPhone: formData.customerPhone,
        shippingAddress: formData.shippingAddress,
        paymentMethod: formData.paymentMethod
      };

      // Store the order in localStorage for the orders page
      const existingOrders = JSON.parse(localStorage.getItem('userOrders') || '[]');
      existingOrders.unshift(order);
      localStorage.setItem('userOrders', JSON.stringify(existingOrders));

      console.log('✅ Order created successfully:', order);

      // Clear cart and navigate
      try {
        await clearCart();
        console.log('✅ Cart cleared successfully');
      } catch (clearError) {
        console.log('⚠️ Cart clear failed, but continuing:', clearError);
      }

      navigate(`/order-confirmation/${order.id}`);
    } catch (error) {
      console.error('Order creation error:', error);
      setError('Failed to create order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto p-6 text-center">
        <h2 className="text-2xl font-bold mb-4">Your cart is empty</h2>
        <p>Add some items to your cart before checking out.</p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-8">Checkout</h1>
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div>
          <form onSubmit={handleSubmit} className="space-y-6">
            {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                {error}
              </div>
            )}

            {/* Customer Information */}
            <div className="bg-white p-6 rounded-lg shadow">
              <h2 className="text-xl font-semibold mb-4">Customer Information</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Full Name <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="customerNameSnapshot"
                    required
                    value={formData.customerNameSnapshot}
                    onChange={handleChange}
                    placeholder="Enter your full name"
                    pattern="[A-Za-z\s]+"
                    title="Please enter only letters and spaces"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Email
                  </label>
                  <input
                    type="email"
                    name="customerEmail"
                    value={formData.customerEmail}
                    onChange={handleChange}
                    placeholder="Enter your email address"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Phone Number <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="tel"
                    name="customerPhone"
                    required
                    value={formData.customerPhone || ''}
                    onChange={handleChange}
                    placeholder="Enter your phone number"
                    pattern="[0-9]{10,15}"
                    title="Please enter a valid phone number (10-15 digits)"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            </div>

            {/* Shipping Address */}
            <div className="bg-white p-6 rounded-lg shadow">
              <h2 className="text-xl font-semibold mb-4">Shipping Address</h2>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Street Address
                  </label>
                  <input
                    type="text"
                    name="shipping.street"
                    value={formData.shippingAddress.street}
                    onChange={handleChange}
                    placeholder="Enter your street address"
                    pattern="[A-Za-z0-9\s,.-]+"
                    title="Please enter a valid address"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Apartment, suite, etc.
                  </label>
                  <input
                    type="text"
                    name="shipping.addressLine2"
                    value={formData.shippingAddress.addressLine2}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      City
                    </label>
                    <input
                      type="text"
                      name="shipping.city"
                      value={formData.shippingAddress.city}
                      onChange={handleChange}
                      placeholder="Enter your city"
                      pattern="[A-Za-z\s]+"
                      title="Please enter only letters and spaces"
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      State/Province
                    </label>
                    <input
                      type="text"
                      name="shipping.stateOrProvince"
                      value={formData.shippingAddress.stateOrProvince}
                      onChange={handleChange}
                      placeholder="Enter your state/province"
                      pattern="[A-Za-z\s]+"
                      title="Please enter only letters and spaces"
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Postal Code
                    </label>
                    <input
                      type="text"
                      name="shipping.postalCode"
                      value={formData.shippingAddress.postalCode}
                      onChange={handleChange}
                      placeholder="Enter your postal code"
                      pattern="[0-9]{6}"
                      title="Please enter a valid 6-digit postal code"
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Country
                    </label>
                    <input
                      type="text"
                      name="shipping.country"
                      value={formData.shippingAddress.country}
                      onChange={handleChange}
                      placeholder="Enter your country"
                      pattern="[A-Za-z\s]+"
                      title="Please enter only letters and spaces"
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                </div>
              </div>
            </div>

            {/* Payment & Shipping Options */}
            <div className="bg-white p-6 rounded-lg shadow">
              <h2 className="text-xl font-semibold mb-4">Payment & Shipping</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Payment Method
                  </label>
                  <select
                    name="paymentMethod"
                    value={formData.paymentMethod}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="UPI">UPI</option>
                    <option value="QR">QR Code</option>
                    <option value="DEBIT_CARD">Debit Card</option>
                    <option value="CREDIT_CARD">Credit Card</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Shipping Method
                  </label>
                  <select
                    name="shippingMethod"
                    value={formData.shippingMethod}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="STANDARD">Standard Shipping</option>
                    <option value="EXPRESS">Express Shipping</option>
                    <option value="OVERNIGHT">Overnight Shipping</option>
                  </select>
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-md transition-colors duration-200 disabled:opacity-50"
            >
              {loading ? 'Processing...' : 'Place Order'}
            </button>
          </form>
        </div>

        {/* Order Summary */}
        <div className="bg-white p-6 rounded-lg shadow h-fit">
          <h2 className="text-xl font-semibold mb-4">Order Summary</h2>
          <div className="space-y-4">
            {cart.items.map((item) => (
              <div key={item.cartItemId} className="flex justify-between">
                <div>
                  <p className="font-medium">{item.productName}</p>
                  <p className="text-sm text-gray-600">
                    Size: {item.variantSize} × {item.quantity}
                  </p>
                </div>
                <p className="font-semibold">
                  ₹{item.subtotal ? parseFloat(item.subtotal).toFixed(2) : '0.00'}
                </p>
              </div>
            ))}
            <div className="border-t pt-4">
              <div className="flex justify-between text-lg font-bold">
                <span>Total</span>
                <span>₹{cart.totalPrice ? parseFloat(cart.totalPrice).toFixed(2) : '0.00'}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutForm;