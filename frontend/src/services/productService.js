import axios from 'axios';

// Use relative URL to leverage Vite proxy
const API_BASE_URL = '/api'; // This will be proxied to http://localhost:8080/api

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

// Get product variant by ID
export const getProductVariantById = async (variantId) => {
  try {
    console.log('🚀 Fetching product variant by ID:', variantId);
    const response = await apiClient.get(`/products/variants/${variantId}`);
    console.log('✅ Product variant fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error fetching product variant:', error.response?.data || error.message);
    throw error;
  }
};

// Get multiple product variants by IDs
export const getProductVariantsByIds = async (variantIds) => {
  try {
    console.log('🚀 Fetching product variants by IDs:', variantIds);
    const response = await apiClient.post('/products/variants/findByIds', variantIds);
    console.log('✅ Product variants fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error fetching product variants:', error.response?.data || error.message);
    throw error;
  }
};

// Enhanced product service with additional methods
export const getProducts = async (params = {}) => {
  try {
    const queryParams = {
      page: params.page || 0,
      size: params.size || 10,
      sort: params.sort || 'name'
    };

    if (params.categoryId) queryParams.categoryId = params.categoryId;
    if (params.search) queryParams.search = params.search;
    if (params.minPrice) queryParams.minPrice = params.minPrice;
    if (params.maxPrice) queryParams.maxPrice = params.maxPrice;

    const response = await apiClient.get('/products', { params: queryParams });
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error.response?.data || error.message);
    throw error;
  }
};

export const getFeaturedProducts = async (limit = 8) => {
  return getProducts({ page: 0, size: limit });
};

export const getKidsProducts = async (limit = 6) => {
  return getProducts({ categoryId: 3, page: 0, size: limit });
};

export const productService = {
  getProducts,
  getProductsByCategoryId,
  getProductById,
  getAllProducts,
  searchProducts,
  getProductVariantById,
  getProductVariantsByIds,
  getFeaturedProducts,
  getKidsProducts
};

export default productService;
