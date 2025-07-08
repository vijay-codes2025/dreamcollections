import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor for auth token if available
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

export const getProductsByCategoryId = async (categoryId, page = 0, size = 10, sort = 'name') => {
  try {
    const response = await apiClient.get(`/products/category/${categoryId}`, {
      params: {
        page,
        size,
        sort,
      },
    });
    return response.data;
  } catch (error) {
    console.error(`Error fetching products for category id ${categoryId}:`, error.response?.data || error.message);
    throw error;
  }
};

export const getProductById = async (productId) => {
  try {
    const response = await apiClient.get(`/products/${productId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching product with id ${productId}:`, error.response?.data || error.message);
    throw error;
  }
};

export const getAllProducts = async (page = 0, size = 10, sort = 'name') => {
  try {
    const response = await apiClient.get('/products', {
      params: {
        page,
        size,
        sort,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching all products:', error.response?.data || error.message);
    throw error;
  }
};

export const searchProducts = async (searchTerm, page = 0, size = 10) => {
  try {
    const response = await apiClient.get('/products/search', {
      params: {
        name: searchTerm,
        page,
        size,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error searching products:', error.response?.data || error.message);
    throw error;
  }
};

export default {
  getProductsByCategoryId,
  getProductById,
  getAllProducts,
  searchProducts,
};
