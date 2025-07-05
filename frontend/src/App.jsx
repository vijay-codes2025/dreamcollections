import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import HomePage from './pages/HomePage';
import CategoryPage from './pages/CategoryPage';
// import ProductPage from './pages/ProductPage'; // Future: for individual product view
// import NotFoundPage from './pages/NotFoundPage'; // Future: for 404

function App() {
  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <header className="bg-blue-600 text-white p-4 shadow-md">
        <nav className="container mx-auto flex justify-between items-center">
          <Link to="/" className="text-3xl font-bold">
            DreamCollections
          </Link>
          {/* Add other nav links here if needed, e.g., Cart, Login */}
        </nav>
      </header>

      <main className="flex-grow">
        {/* Routes will render the page content based on the URL */}
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/category/:categoryId" element={<CategoryPage />} />
          {/* <Route path="/product/:productId" element={<ProductPage />} /> */}
          {/* <Route path="*" element={<NotFoundPage />} /> */}
        </Routes>
      </main>

      <footer className="bg-gray-800 text-white text-center p-4 mt-auto">
        <p>&copy; {new Date().getFullYear()} DreamCollections. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default App;
