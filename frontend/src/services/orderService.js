import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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

export const createOrder = async (orderData) => {
  try {
    const response = await apiClient.post('/orders', orderData);
    return response.data;
  } catch (error) {
    console.error('Create order error:', error.response?.data || error.message);
    throw error;
  }
};

export const getMyOrders = async (page = 0, size = 10) => {
  try {
    const response = await apiClient.get('/orders/my-orders', {
      params: { page, size }
    });
    return response.data;
  } catch (error) {
    console.error('Get orders error:', error.response?.data || error.message);
    throw error;
  }
};

export const getOrderById = async (orderId) => {
  try {
    const response = await apiClient.get(`/orders/${orderId}`);
    return response.data;
  } catch (error) {
    console.error('Get order error:', error.response?.data || error.message);
    throw error;
  }
};

export default {
  createOrder,
  getMyOrders,
  getOrderById,
};