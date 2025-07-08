import React, { createContext, useContext, useState, useEffect } from 'react';
import cartService from '../services/cartService';
import { useAuth } from './AuthContext';

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);
  const { isAuthenticated } = useAuth();

  const fetchCart = async () => {
    if (!isAuthenticated) {
      setCart(null);
      return;
    }

    try {
      setLoading(true);
      const cartData = await cartService.getCart();
      setCart(cartData);
    } catch (error) {
      console.error('Failed to fetch cart:', error);
      setCart(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, [isAuthenticated]);

  const addToCart = async (productVariantId, quantity) => {
    try {
      const updatedCart = await cartService.addItem(productVariantId, quantity);
      setCart(updatedCart);
      return updatedCart;
    } catch (error) {
      throw error;
    }
  };

  const updateQuantity = async (productVariantId, quantity) => {
    try {
      const updatedCart = await cartService.updateQuantity(productVariantId, quantity);
      setCart(updatedCart);
      return updatedCart;
    } catch (error) {
      throw error;
    }
  };

  const removeFromCart = async (productVariantId) => {
    try {
      const updatedCart = await cartService.removeItem(productVariantId);
      setCart(updatedCart);
      return updatedCart;
    } catch (error) {
      throw error;
    }
  };

  const clearCart = async () => {
    try {
      await cartService.clearCart();
      setCart(null);
    } catch (error) {
      throw error;
    }
  };

  const getCartItemCount = () => {
    return cart?.totalItemsCount || 0;
  };

  const value = {
    cart,
    loading,
    addToCart,
    updateQuantity,
    removeFromCart,
    clearCart,
    getCartItemCount,
    refreshCart: fetchCart
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};
