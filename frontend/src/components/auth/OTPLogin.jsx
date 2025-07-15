import React, { useState } from 'react';
import { authService } from '../../services/authService';

const OTPLogin = ({ phoneNumber, onSuccess, onCancel }) => {
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [otpSent, setOtpSent] = useState(false);

  const sendOTP = async () => {
    setLoading(true);
    setError('');
    
    try {
      // For demo purposes, we'll simulate OTP sending
      console.log(`Sending OTP to ${phoneNumber}`);
      
      // Try to send OTP via backend
      try {
        await authService.sendOTP(phoneNumber);
      } catch (backendError) {
        console.log('Backend OTP send failed, simulating OTP send');
      }
      
      setOtpSent(true);
      setError('');
    } catch (error) {
      setError('Failed to send OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const verifyOTP = async () => {
    if (!otp || otp.length !== 6) {
      setError('Please enter a valid 6-digit OTP');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // For demo purposes, accept any 6-digit OTP
      if (otp.length === 6) {
        // Load user data from localStorage
        const savedUsers = JSON.parse(localStorage.getItem('savedUsers') || '{}');
        const userData = savedUsers[phoneNumber];
        
        if (userData) {
          onSuccess(userData);
        } else {
          setError('User data not found. Please complete the form manually.');
        }
      } else {
        setError('Invalid OTP. Please try again.');
      }
    } catch (error) {
      setError('Failed to verify OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!otpSent) {
    return (
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-sm font-medium text-blue-800">
              Welcome back! We found your details.
            </h3>
            <p className="text-sm text-blue-600 mt-1">
              Verify your phone number ({phoneNumber}) to auto-fill your information.
            </p>
          </div>
          <div className="flex gap-2">
            <button
              onClick={sendOTP}
              disabled={loading}
              className="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? 'Sending...' : 'Send OTP'}
            </button>
            <button
              onClick={onCancel}
              className="px-3 py-1 bg-gray-300 text-gray-700 text-sm rounded hover:bg-gray-400"
            >
              Skip
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-4 mb-4">
      <h3 className="text-sm font-medium text-green-800 mb-2">
        Enter OTP sent to {phoneNumber}
      </h3>
      
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded mb-3 text-sm">
          {error}
        </div>
      )}
      
      <div className="flex items-center gap-2">
        <input
          type="text"
          value={otp}
          onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
          placeholder="Enter 6-digit OTP"
          maxLength="6"
          className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-center text-lg tracking-widest"
        />
        <button
          onClick={verifyOTP}
          disabled={loading || otp.length !== 6}
          className="px-4 py-2 bg-green-600 text-white text-sm rounded hover:bg-green-700 disabled:opacity-50"
        >
          {loading ? 'Verifying...' : 'Verify'}
        </button>
        <button
          onClick={onCancel}
          className="px-3 py-2 bg-gray-300 text-gray-700 text-sm rounded hover:bg-gray-400"
        >
          Cancel
        </button>
      </div>
      
      <p className="text-xs text-green-600 mt-2">
        For demo: Enter any 6-digit number as OTP
      </p>
    </div>
  );
};

export default OTPLogin;
