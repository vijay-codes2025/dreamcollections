import axios from 'axios';

// Use relative URL to leverage Vite proxy
const API_BASE_URL = '/api'; // This will be proxied to http://localhost:8080/api

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor for auth token
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Get current user profile
export const getCurrentUserProfile = async () => {
  try {
    console.log('🚀 Fetching current user profile...');
    const response = await apiClient.get('/users/me');
    console.log('✅ User profile fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error fetching user profile:', error.response?.data || error.message);
    throw error;
  }
};

// Get user profile by ID (Admin only)
export const getUserProfileById = async (userId) => {
  try {
    console.log('🚀 Fetching user profile by ID:', userId);
    const response = await apiClient.get(`/users/${userId}`);
    console.log('✅ User profile fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error fetching user profile by ID:', error.response?.data || error.message);
    throw error;
  }
};

export default {
  getCurrentUserProfile,
  getUserProfileById,
};
