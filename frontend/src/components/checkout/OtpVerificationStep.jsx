import React, { useState, useEffect } from 'react';
import authService from '../../services/authService';

const OtpVerificationStep = ({ customerDetails, onVerified, onBack }) => {
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const [canResend, setCanResend] = useState(false);

  useEffect(() => {
    // Send OTP when component mounts
    sendOtp();
  }, []);

  useEffect(() => {
    // Countdown timer
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    } else if (otpSent) {
      setCanResend(true);
    }
  }, [countdown, otpSent]);

  const sendOtp = async () => {
    try {
      setLoading(true);
      setError('');
      
      const response = await fetch('/api/auth/otp/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          phoneNumber: customerDetails.phone,
          email: customerDetails.email,
          type: 'CHECKOUT'
        }),
      });

      if (response.ok) {
        setOtpSent(true);
        setCountdown(300); // 5 minutes
        setCanResend(false);
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to send OTP');
      }
    } catch (error) {
      console.error('Error sending OTP:', error);
      setError('Failed to send OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const verifyOtp = async () => {
    if (!otp || otp.length !== 6) {
      setError('Please enter a valid 6-digit OTP');
      return;
    }

    try {
      setLoading(true);
      setError('');

      const response = await fetch('/api/auth/otp/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          phoneNumber: customerDetails.phone,
          email: customerDetails.email,
          otp: otp
        }),
      });

      if (response.ok) {
        const data = await response.json();
        // Store the JWT tokens
        localStorage.setItem('accessToken', data.token);
        if (data.refreshToken) {
          localStorage.setItem('refreshToken', data.refreshToken);
        }
        onVerified();
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Invalid OTP. Please try again.');
      }
    } catch (error) {
      console.error('Error verifying OTP:', error);
      setError('Failed to verify OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleResend = () => {
    setOtp('');
    setError('');
    sendOtp();
  };

  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const maskPhone = (phone) => {
    if (!phone || phone.length < 4) return phone;
    return phone.substring(0, phone.length - 4) + '••••';
  };

  const maskEmail = (email) => {
    if (!email || !email.includes('@')) return email;
    const [username, domain] = email.split('@');
    if (username.length <= 2) return email;
    return username.substring(0, 2) + '•••@' + domain;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="text-center">
        <div className="mx-auto w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
          <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
          </svg>
        </div>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">Verify Your Phone Number</h3>
        <p className="text-gray-600">
          We've sent a 6-digit verification code to:
        </p>
        <p className="font-medium text-gray-900 mt-1">
          📱 {maskPhone(customerDetails.phone)}
          {customerDetails.email && (
            <>
              <br />
              📧 {maskEmail(customerDetails.email)}
            </>
          )}
        </p>
      </div>

      {/* OTP Input */}
      <div>
        <label htmlFor="otp" className="block text-sm font-medium text-gray-700 mb-2">
          Enter 6-digit verification code
        </label>
        <input
          type="text"
          id="otp"
          value={otp}
          onChange={(e) => {
            const value = e.target.value.replace(/\D/g, '').slice(0, 6);
            setOtp(value);
            setError('');
          }}
          className={`w-full px-4 py-3 text-center text-2xl font-mono border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
            error ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="000000"
          maxLength={6}
        />
        {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
      </div>

      {/* Timer and Resend */}
      <div className="text-center">
        {countdown > 0 ? (
          <p className="text-gray-600">
            Resend code in <span className="font-medium text-blue-600">{formatTime(countdown)}</span>
          </p>
        ) : canResend ? (
          <button
            onClick={handleResend}
            disabled={loading}
            className="text-blue-600 hover:text-blue-700 font-medium"
          >
            Resend verification code
          </button>
        ) : null}
      </div>

      {/* Edit Details Option */}
      <div className="text-center">
        <button
          onClick={onBack}
          className="text-sm text-gray-600 hover:text-gray-800"
        >
          Wrong phone number? Edit details
        </button>
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
          onClick={verifyOtp}
          disabled={loading || otp.length !== 6}
          className={`px-6 py-2 rounded-md transition-colors ${
            loading || otp.length !== 6
              ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
              : 'bg-blue-600 text-white hover:bg-blue-700'
          }`}
        >
          {loading ? (
            <div className="flex items-center">
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Verifying...
            </div>
          ) : (
            'Verify & Continue'
          )}
        </button>
      </div>
    </div>
  );
};

export default OtpVerificationStep;
