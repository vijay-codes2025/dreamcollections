import api from './api';

class AuthService {
  // Sign up with name, mobile, and optional email
  async signup(signupData) {
    try {
      const response = await api.post('/auth/signup', {
        username: signupData.phoneNumber, // Use phone number as username
        firstName: signupData.firstName,
        lastName: signupData.lastName || '',
        phoneNumber: signupData.phoneNumber,
        email: signupData.email || `${signupData.phoneNumber}@dreamcollections.com`, // Generate email if not provided
        password: signupData.password,
        role: 'customer'
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: 'Signup failed' };
    }
  }

  // Traditional login with phone/email and password
  async login(loginData) {
    try {
      const response = await api.post('/auth/signin', {
        loginId: loginData.loginId, // Can be phone or email
        password: loginData.password
      });
      
      if (response.data.accessToken) {
        localStorage.setItem('user', JSON.stringify(response.data));
        localStorage.setItem('token', response.data.accessToken);
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: 'Login failed' };
    }
  }

  // Send OTP for mobile login
  async sendOTP(phoneNumber, email = null) {
    try {
      const response = await api.post('/auth/otp/send', {
        phoneNumber: phoneNumber,
        email: email,
        type: 'LOGIN'
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: 'Failed to send OTP' };
    }
  }

  // Verify OTP and login
  async verifyOTP(phoneNumber, email, otp) {
    try {
      const response = await api.post('/auth/otp/verify', {
        phoneNumber: phoneNumber,
        email: email,
        otp: otp
      });
      
      if (response.data.accessToken) {
        localStorage.setItem('user', JSON.stringify(response.data));
        localStorage.setItem('token', response.data.accessToken);
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: 'OTP verification failed' };
    }
  }

  // Resend OTP
  async resendOTP(phoneNumber, email = null) {
    try {
      const response = await api.post('/auth/otp/resend', {
        phoneNumber: phoneNumber,
        email: email
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: 'Failed to resend OTP' };
    }
  }

  // Google OAuth login (placeholder for future implementation)
  async googleLogin(googleToken) {
    try {
      const response = await api.post('/auth/google', {
        token: googleToken
      });
      
      if (response.data.accessToken) {
        localStorage.setItem('user', JSON.stringify(response.data));
        localStorage.setItem('token', response.data.accessToken);
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: 'Google login failed' };
    }
  }

  // Logout
  logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('guestCart'); // Clear guest cart on logout
  }

  // Get current user
  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  // Check if user is authenticated
  isAuthenticated() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    return !!(token && user);
  }

  // Get auth token
  getToken() {
    return localStorage.getItem('token');
  }

  // Refresh token (placeholder for future implementation)
  async refreshToken() {
    try {
      const user = this.getCurrentUser();
      if (!user?.refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await api.post('/auth/refresh', {
        refreshToken: user.refreshToken
      });
      
      if (response.data.accessToken) {
        const updatedUser = { ...user, ...response.data };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        localStorage.setItem('token', response.data.accessToken);
      }
      
      return response.data;
    } catch (error) {
      this.logout(); // Clear invalid tokens
      throw error.response?.data || { message: 'Token refresh failed' };
    }
  }
}

export default new AuthService();
