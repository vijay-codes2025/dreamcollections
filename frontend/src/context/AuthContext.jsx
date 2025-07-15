import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        // Check if user is authenticated using new authService
        if (authService.isAuthenticated()) {
          const userData = authService.getCurrentUser();
          setUser(userData);
          setIsAuthenticated(true);
        } else {
          // Check for phone-based auth session (legacy)
          const currentUser = localStorage.getItem('currentUser');
          if (currentUser) {
            const userData = JSON.parse(currentUser);
            setUser(userData);
            setIsAuthenticated(true);
          }
        }
      } catch (error) {
        console.error('Failed to initialize auth:', error);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('currentUser');
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials);
      localStorage.setItem('accessToken', response.token);
      setUser({
        id: response.id,
        username: response.username,
        email: response.email,
        roles: response.roles
      });
      setIsAuthenticated(true);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const loginWithPhone = async (phoneNumber, otp) => {
    try {
      const formattedPhone = formatPhoneNumber(phoneNumber);
      const response = await authService.verifyOTP(formattedPhone, null, otp);

      if (response.accessToken) {
        const user = {
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles
        };

        setUser(user);
        setIsAuthenticated(true);
        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('token', response.accessToken);

        return user;
      } else {
        throw new Error('OTP verification failed');
      }
    } catch (error) {
      throw error;
    }
  };

  const formatPhoneNumber = (phone) => {
    // Remove all non-digits
    const digits = phone.replace(/\D/g, '');

    // If it starts with 91, add +
    if (digits.startsWith('91') && digits.length === 12) {
      return '+' + digits;
    }

    // If it's 10 digits, add +91
    if (digits.length === 10) {
      return '+91' + digits;
    }

    // If it already has +91, return as is
    if (phone.startsWith('+91')) {
      return phone;
    }

    return phone;
  };

  const sendOTP = async (phoneNumber) => {
    try {
      const formattedPhone = formatPhoneNumber(phoneNumber);
      const response = await authService.sendOTP(formattedPhone);
      return response;
    } catch (error) {
      throw error;
    }
  };



  const logout = () => {
    authService.logout();
    localStorage.removeItem('currentUser'); // Remove legacy phone auth
    setUser(null);
    setIsAuthenticated(false);
  };

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    loginWithPhone,
    sendOTP,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
