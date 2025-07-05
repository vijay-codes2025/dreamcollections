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

export const getTopLevelCategories = async () => {
  try {
    // The controller's @GetMapping on /categories now returns top-level with children
    const response = await apiClient.get('/categories');
    return response.data;
  } catch (error) {
    console.error('Error fetching top-level categories:', error.response ? error.response.data : error.message);
    throw error;
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
  } catch (error)
 {
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

export default {
  getTopLevelCategories,
  getCategoryById,
  getSubCategories,
  createCategory,
  updateCategory,
  deleteCategory,
};
