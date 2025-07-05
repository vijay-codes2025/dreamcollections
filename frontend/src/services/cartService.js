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

export const getCart = async () => {
  try {
    const response = await apiClient.get('/carts/mine');
    return response.data;
  } catch (error) {
    console.error('Get cart error:', error.response?.data || error.message);
    throw error;
  }
};

export const addItem = async (productVariantId, quantity) => {
  try {
    const response = await apiClient.post('/carts/mine/items', {
      productVariantId,
      quantity
    });
    return response.data;
  } catch (error) {
    console.error('Add to cart error:', error.response?.data || error.message);
    throw error;
  }
};

export const updateQuantity = async (productVariantId, quantity) => {
  try {
    const response = await apiClient.put(`/carts/mine/items/${productVariantId}`, {
      quantity
    });
    return response.data;
  } catch (error) {
    console.error('Update cart quantity error:', error.response?.data || error.message);
    throw error;
  }
};

export const removeItem = async (productVariantId) => {
  try {
    const response = await apiClient.delete(`/carts/mine/items/${productVariantId}`);
    return response.data;
  } catch (error) {
    console.error('Remove from cart error:', error.response?.data || error.message);
    throw error;
  }
};

export const clearCart = async () => {
  try {
    const response = await apiClient.delete('/carts/mine');
    return response.data;
  } catch (error) {
    console.error('Clear cart error:', error.response?.data || error.message);
    throw error;
  }
};

export default {
  getCart,
  addItem,
  updateQuantity,
  removeItem,
  clearCart,
};