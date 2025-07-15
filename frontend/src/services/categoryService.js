import axios from 'axios';

// Use relative URL to leverage Vite proxy
const API_BASE_URL = '/api'; // This will be proxied to http://localhost:8080/api

// Test function to check API connectivity
export const testApiConnectivity = async () => {
  try {
    console.log('🔍 Testing API connectivity...');
    console.log('API_BASE_URL:', API_BASE_URL);

    // Test with a simple fetch to see if we can reach the server
    const response = await fetch(`${API_BASE_URL}/categories`, {
      method: 'GET',
      mode: 'cors',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    });

    console.log('🔍 Test response:', {
      status: response.status,
      statusText: response.statusText,
      ok: response.ok,
      url: response.url,
      type: response.type,
      headers: Object.fromEntries(response.headers.entries())
    });

    if (response.ok) {
      const text = await response.text();
      console.log('🔍 Response text:', text);
      try {
        const json = JSON.parse(text);
        console.log('✅ API connectivity test successful:', json);
        return json;
      } catch (parseError) {
        console.error('❌ JSON parse error:', parseError);
        return text;
      }
    } else {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
  } catch (error) {
    console.error('❌ API connectivity test failed:', error);
    throw error;
  }
};

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  timeout: 10000, // 10 second timeout
  responseType: 'json', // Ensure response is parsed as JSON
});

// Add response interceptor to handle JSON parsing
apiClient.interceptors.response.use(
  (response) => {
    // Log successful responses for debugging
    console.log('Axios response interceptor - success:', response);
    return response;
  },
  (error) => {
    // Log error responses for debugging
    console.error('Axios response interceptor - error:', error);
    if (error.response) {
      console.error('Error response data:', error.response.data);
      console.error('Error response status:', error.response.status);
    }
    return Promise.reject(error);
  }
);

// Function to get the auth token (implement as needed, e.g., from localStorage)
const getAuthToken = () => {
  // Example: return localStorage.getItem('userToken');
  // For now, returning null. Replace with actual token retrieval logic.
  // This will be needed for ADMIN operations.
  const token = localStorage.getItem('accessToken'); // Or however your token is stored
  return token;
};

// Add a request interceptor to include the auth token
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

// Alternative fetch-based implementation as fallback
export const getTopLevelCategoriesFetch = async () => {
  try {
    console.log('🚀 Starting fetch request to /categories');

    const response = await fetch(`${API_BASE_URL}/categories`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    });

    console.log('✅ Fetch Response received:', {
      status: response.status,
      statusText: response.statusText,
      ok: response.ok,
      headers: Object.fromEntries(response.headers.entries())
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('✅ JSON data parsed:', data);

    return data;

  } catch (error) {
    console.error('❌ Error in getTopLevelCategoriesFetch:', error);
    throw error;
  }
};

export const getTopLevelCategories = async () => {
  try {
    console.log('🚀 Starting API request to /categories');

    // First test basic connectivity
    console.log('🔍 Testing basic connectivity first...');
    await testApiConnectivity();

    // Try axios first
    const response = await apiClient.get('/categories');

    console.log('✅ Axios Response received:', {
      status: response.status,
      statusText: response.statusText,
      headers: response.headers,
      data: response.data,
      dataType: typeof response.data
    });

    // Return the data directly - axios should handle JSON parsing automatically
    return response.data;

  } catch (axiosError) {
    console.warn('⚠️ Axios failed, trying fetch as fallback:', axiosError);

    // Fallback to fetch if axios fails
    return await getTopLevelCategoriesFetch();
  }
};

export const getCategoryById = async (id) => {
  try {
    const response = await apiClient.get(`/categories/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching category with id ${id}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

export const getSubCategories = async (parentId) => {
  try {
    const response = await apiClient.get(`/categories/${parentId}/subcategories`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching sub-categories for parent id ${parentId}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

export const createCategory = async (categoryData) => {
  // categoryData should be an object like: { name: "New Category", description: "Desc", parentId: 1 (or null) }
  try {
    const response = await apiClient.post('/categories', categoryData);
    return response.data;
  } catch (error) {
    console.error('Error creating category:', error.response ? error.response.data : error.message);
    throw error;
  }
};

export const updateCategory = async (id, categoryData) => {
  // categoryData should be an object like: { name: "Updated Category", description: "New Desc", parentId: 2 (or null) }
  try {
    const response = await apiClient.put(`/categories/${id}`, categoryData);
    return response.data;
  } catch (error) {
    console.error(`Error updating category with id ${id}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

export const deleteCategory = async (id) => {
  try {
    const response = await apiClient.delete(`/categories/${id}`);
    return response.data; // Usually a success message
  } catch (error) {
    console.error(`Error deleting category with id ${id}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

// Alias for getTopLevelCategories to match the expected interface
export const getAllCategories = getTopLevelCategories;

export const categoryService = {
  getAllCategories,
  getTopLevelCategories,
  getCategoryById,
  getSubCategories,
  createCategory,
  updateCategory,
  deleteCategory,
};

export default categoryService;
