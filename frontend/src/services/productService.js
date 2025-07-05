import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api'; // API Gateway URL

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Function to get the auth token (implement as needed, e.g., from localStorage)
const getAuthToken = () => {
  const token = localStorage.getItem('accessToken'); // Or however your token is stored
  return token;
};

// Add a request interceptor to include the auth token if available
// This is mainly for admin operations on products, viewing might be public
apiClient.interceptors.request.use(
  config => {
    const token = getAuthToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

export const getProductsByCategoryId = async (categoryId, page = 0, size = 10, sort = 'name') => {
  try {
    const response = await apiClient.get(`/products/category/${categoryId}`, {
      params: {
        page,
        size,
        sort, // e.g., 'name,asc' or 'price,desc'
      },
    });
    return response.data; // This will be a Page object { content: [], totalPages, totalElements, ... }
  } catch (error) {
    console.error(`Error fetching products for category id ${categoryId}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

export const getProductById = async (productId) => {
  try {
    const response = await apiClient.get(`/products/${productId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching product with id ${productId}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

// Add other product service functions as needed (create, update, delete, getAll, search)

export default {
  getProductsByCategoryId,
  getProductById,
};
