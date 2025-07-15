import React, { useState } from 'react';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import CustomerDetailsStep from './CustomerDetailsStep';
import OtpVerificationStep from './OtpVerificationStep';
import PaymentStep from './PaymentStep';

const CheckoutModal = ({ isOpen, onClose }) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [customerDetails, setCustomerDetails] = useState(null);
  const [otpSent, setOtpSent] = useState(false);
  const [isVerified, setIsVerified] = useState(false);
  const { cart } = useCart();
  const { isAuthenticated } = useAuth();

  if (!isOpen) return null;

  const handleCustomerDetailsSubmit = (details) => {
    setCustomerDetails(details);
    
    // If user is already authenticated, skip OTP and go to payment
    if (isAuthenticated) {
      setCurrentStep(3);
    } else {
      // For guest users, proceed to OTP verification
      setCurrentStep(2);
    }
  };

  const handleOtpVerified = () => {
    setIsVerified(true);
    setCurrentStep(3);
  };

  const handlePaymentComplete = () => {
    // Handle successful payment
    alert('Order placed successfully!');
    onClose();
    setCurrentStep(1);
    setCustomerDetails(null);
    setOtpSent(false);
    setIsVerified(false);
  };

  const renderStep = () => {
    switch (currentStep) {
      case 1:
        return (
          <CustomerDetailsStep
            onSubmit={handleCustomerDetailsSubmit}
            onCancel={onClose}
          />
        );
      case 2:
        return (
          <OtpVerificationStep
            customerDetails={customerDetails}
            onVerified={handleOtpVerified}
            onBack={() => setCurrentStep(1)}
          />
        );
      case 3:
        return (
          <PaymentStep
            customerDetails={customerDetails}
            cart={cart}
            onComplete={handlePaymentComplete}
            onBack={() => setCurrentStep(isAuthenticated ? 1 : 2)}
          />
        );
      default:
        return null;
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          {/* Header */}
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-gray-900">
              {currentStep === 1 && 'Customer Details'}
              {currentStep === 2 && 'Verify Phone Number'}
              {currentStep === 3 && 'Payment & Review'}
            </h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl"
            >
              ×
            </button>
          </div>

          {/* Progress Indicator */}
          <div className="mb-8">
            <div className="flex items-center">
              <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
                currentStep >= 1 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
              }`}>
                1
              </div>
              <div className={`flex-1 h-1 mx-2 ${
                currentStep >= 2 ? 'bg-blue-600' : 'bg-gray-300'
              }`}></div>
              <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
                currentStep >= 2 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
              }`}>
                2
              </div>
              <div className={`flex-1 h-1 mx-2 ${
                currentStep >= 3 ? 'bg-blue-600' : 'bg-gray-300'
              }`}></div>
              <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
                currentStep >= 3 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
              }`}>
                3
              </div>
            </div>
            <div className="flex justify-between text-sm text-gray-600 mt-2">
              <span>Details</span>
              <span>{isAuthenticated ? 'Skip' : 'Verify'}</span>
              <span>Payment</span>
            </div>
          </div>

          {/* Step Content */}
          {renderStep()}
        </div>
      </div>
    </div>
  );
};

export default CheckoutModal;
