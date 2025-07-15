import React, { useState } from 'react';
import { formatPrice } from '../../utils/currency';

const PaymentStep = ({ customerDetails, cart, onComplete, onBack }) => {
  const [paymentMethod, setPaymentMethod] = useState('cod');
  const [loading, setLoading] = useState(false);

  const handlePlaceOrder = async () => {
    setLoading(true);
    
    // Simulate order processing
    setTimeout(() => {
      setLoading(false);
      onComplete();
    }, 2000);
  };

  const totalAmount = cart?.totalAmount || 0;
  const totalItems = cart?.totalItems || 0;

  return (
    <div className="space-y-6">
      {/* Order Summary */}
      <div className="bg-gray-50 p-4 rounded-lg">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Order Summary</h3>
        
        {cart?.items?.length > 0 ? (
          <div className="space-y-2">
            {cart.items.map((item, index) => (
              <div key={index} className="flex justify-between text-sm">
                <span>{item.productName || 'Product'} × {item.quantity}</span>
                <span>{formatPrice(item.totalPrice || 0)}</span>
              </div>
            ))}
            <div className="border-t pt-2 mt-2">
              <div className="flex justify-between font-semibold">
                <span>Total ({totalItems} items)</span>
                <span>{formatPrice(totalAmount)}</span>
              </div>
            </div>
          </div>
        ) : (
          <p className="text-gray-600">No items in cart</p>
        )}
      </div>

      {/* Customer Details Summary */}
      <div className="bg-gray-50 p-4 rounded-lg">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Delivery Details</h3>
        <div className="space-y-2 text-sm">
          <div>
            <span className="font-medium">Name:</span> {customerDetails.fullName}
          </div>
          <div>
            <span className="font-medium">Phone:</span> {customerDetails.phone}
          </div>
          {customerDetails.email && (
            <div>
              <span className="font-medium">Email:</span> {customerDetails.email}
            </div>
          )}
          <div>
            <span className="font-medium">Address:</span>
            <div className="ml-2">
              {customerDetails.street}
              {customerDetails.addressLine2 && <>, {customerDetails.addressLine2}</>}
              <br />
              {customerDetails.city}, {customerDetails.stateOrProvince} {customerDetails.postalCode}
              <br />
              {customerDetails.country}
            </div>
          </div>
        </div>
      </div>

      {/* Payment Method */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Payment Method</h3>
        
        <div className="space-y-3">
          <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50">
            <input
              type="radio"
              name="paymentMethod"
              value="cod"
              checked={paymentMethod === 'cod'}
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="mr-3"
            />
            <div className="flex-1">
              <div className="font-medium">Cash on Delivery</div>
              <div className="text-sm text-gray-600">Pay when your order is delivered</div>
            </div>
            <div className="text-2xl">💵</div>
          </label>

          <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50 opacity-50">
            <input
              type="radio"
              name="paymentMethod"
              value="online"
              disabled
              className="mr-3"
            />
            <div className="flex-1">
              <div className="font-medium">Online Payment</div>
              <div className="text-sm text-gray-600">Credit/Debit Card, UPI, Net Banking (Coming Soon)</div>
            </div>
            <div className="text-2xl">💳</div>
          </label>
        </div>
      </div>

      {/* Terms and Conditions */}
      <div className="bg-blue-50 p-4 rounded-lg">
        <div className="flex items-start">
          <input
            type="checkbox"
            id="terms"
            className="mt-1 mr-3"
            defaultChecked
          />
          <label htmlFor="terms" className="text-sm text-gray-700">
            I agree to the{' '}
            <a href="#" className="text-blue-600 hover:underline">Terms and Conditions</a>
            {' '}and{' '}
            <a href="#" className="text-blue-600 hover:underline">Privacy Policy</a>
          </label>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex justify-between pt-6">
        <button
          type="button"
          onClick={onBack}
          className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
        >
          Back
        </button>
        <button
          onClick={handlePlaceOrder}
          disabled={loading}
          className={`px-8 py-3 rounded-md font-semibold transition-colors ${
            loading
              ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
              : 'bg-green-600 text-white hover:bg-green-700'
          }`}
        >
          {loading ? (
            <div className="flex items-center">
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Processing Order...
            </div>
          ) : (
            `Place Order - ${formatPrice(totalAmount)}`
          )}
        </button>
      </div>
    </div>
  );
};

export default PaymentStep;
