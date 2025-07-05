import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import CartIcon from './components/cart/CartIcon';

// Pages
import HomePage from './pages/HomePage';
import CategoryPage from './pages/CategoryPage';
import ProductPage from './pages/ProductPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderConfirmationPage from './pages/OrderConfirmationPage';
import OrdersPage from './pages/OrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';

const Navigation = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <header className="bg-blue-600 text-white p-4 shadow-md">
      <nav className="container mx-auto flex justify-between items-center">
        <Link to="/" className="text-3xl font-bold hover:text-blue-200 transition-colors">
          DreamCollections
        </Link>
        
        <div className="flex items-center space-x-4">
          {isAuthenticated ? (
            <>
              <CartIcon />
              <Link to="/orders" className="hover:text-blue-200 transition-colors">
                My Orders
              </Link>
              <div className="flex items-center space-x-2">
                <span className="text-sm">Welcome, {user?.username}</span>
                <button
                  onClick={logout}
                  className="bg-blue-700 hover:bg-blue-800 px-3 py-1 rounded text-sm transition-colors"
                >
                  Logout
                </button>
              </div>
            </>
          ) : (
            <div className="flex items-center space-x-4">
              <Link to="/login" className="hover:text-blue-200 transition-colors">
                Login
              </Link>
              <Link 
                to="/register" 
                className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition-colors"
              >
                Sign Up
              </Link>
            </div>
          )}
        </div>
      </nav>
    </header>
  );
};

function AppContent() {
  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <Navigation />

      <main className="flex-grow">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/category/:categoryId" element={<CategoryPage />} />
          <Route path="/product/:productId" element={<ProductPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/order-confirmation/:orderId" element={<OrderConfirmationPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/order/:orderId" element={<OrderDetailPage />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/register" element={<RegisterForm />} />
        </Routes>
      </main>

      <footer className="bg-gray-800 text-white text-center p-4 mt-auto">
        <p>&copy; {new Date().getFullYear()} DreamCollections. All rights reserved.</p>
      </footer>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <AppContent />
      </CartProvider>
    </AuthProvider>
  );
}

export default App;