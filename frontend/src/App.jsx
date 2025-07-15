import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import './App.css';
import ProtectedRoute from './components/auth/ProtectedRoute';
import CartIcon from './components/cart/CartIcon';

// Pages
import HomePage from './pages/HomePage';
import ProductsPage from './pages/ProductsPage';
import CategoryPage from './pages/CategoryPage';
import ProductPage from './pages/ProductPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderConfirmationPage from './pages/OrderConfirmationPage';
import OrdersPage from './pages/OrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import PhoneLoginPage from './pages/PhoneLoginPage';
import LoginForm from './components/auth/LoginForm';
import Footer from './components/layout/Footer';

const Navigation = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <header className="bg-gradient-to-r from-blue-50 to-indigo-50 text-gray-800 shadow-sm border-b border-blue-100">
      <nav className="container mx-auto px-4 py-4">
        <div className="flex justify-between items-center">
          <Link to="/" className="text-2xl font-bold text-gray-800 hover:text-blue-600 transition-colors">
            DreamCollections
          </Link>

          {/* Main Navigation Links */}
          <div className="hidden md:flex items-center space-x-8">
            <div className="relative group">
              <button className="text-gray-600 hover:text-blue-600 transition-colors font-medium">
                Shop by Category
              </button>
              <div className="absolute top-full left-0 mt-2 w-48 bg-white shadow-lg rounded-md opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50">
                <Link to="/products" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                  All Products
                </Link>
                <Link to="/products?category=1" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                  Women's Jewelry
                </Link>
                <Link to="/products?category=2" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                  Men's Jewelry
                </Link>
                <Link to="/products?category=3" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                  Kids Collection
                </Link>
                <Link to="/products?category=5" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                  Wedding Collection
                </Link>
              </div>
            </div>
          </div>

          {/* User Actions */}
          <div className="flex items-center space-x-4">
            {/* Cart Icon - Always visible for guest checkout */}
            <CartIcon />

            {isAuthenticated ? (
              <>
                <Link to="/orders" className="text-gray-600 hover:text-blue-600 transition-colors font-medium">
                  My Orders
                </Link>
                <div className="flex items-center space-x-3">
                  <span className="text-sm text-gray-600 bg-gray-100 px-3 py-1 rounded-full">
                    Welcome, {user?.firstName || user?.username}
                  </span>
                  <button
                    onClick={logout}
                    className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-md text-sm transition-colors"
                  >
                    Logout
                  </button>
                </div>
              </>
            ) : (
              <div className="flex items-center space-x-4">
                <Link to="/login" className="bg-blue-600 text-white hover:bg-blue-700 px-6 py-2 rounded-md font-medium transition-colors">
                  Sign In
                </Link>
                <Link to="/signup" className="text-gray-600 hover:text-blue-600 transition-colors font-medium text-sm">
                  Sign Up
                </Link>
              </div>
            )}
          </div>
        </div>

        {/* Mobile Navigation */}
        <div className="md:hidden mt-4 pt-4 border-t border-gray-200">
          <div className="flex flex-wrap gap-4">
            <Link to="/products" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
              All Products
            </Link>
            <Link to="/products?category=1" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
              Women's
            </Link>
            <Link to="/products?category=2" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
              Men's
            </Link>
            <Link to="/products?category=3" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
              Kids
            </Link>
            <Link to="/products?category=5" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
              Wedding
            </Link>
          </div>
        </div>
      </nav>
    </header>
  );
};

function AppContent() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50/30 to-indigo-50/30 flex flex-col">
      <Navigation />

      <main className="flex-grow bg-transparent">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/products/:id" element={<ProductPage />} />
          <Route path="/category/:categoryId" element={<CategoryPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/order-confirmation/:orderId" element={<OrderConfirmationPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/order/:orderId" element={<OrderDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/phone-login" element={<PhoneLoginPage />} />
        </Routes>
      </main>

      <Footer />
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