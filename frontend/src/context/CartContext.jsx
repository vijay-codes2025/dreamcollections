import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
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

  // Initialize guest cart from localStorage
  const initializeGuestCart = useCallback(() => {
    const guestCart = localStorage.getItem('guestCart');
    if (guestCart) {
      try {
        const parsedCart = JSON.parse(guestCart);
        setCart(parsedCart);
      } catch (error) {
        console.error('Failed to parse guest cart:', error);
        localStorage.removeItem('guestCart');
      }
    } else {
      // Initialize empty guest cart
      const emptyCart = {
        id: 'guest-cart',
        items: [],
        totalAmount: 0,
        totalItems: 0
      };
      setCart(emptyCart);
      localStorage.setItem('guestCart', JSON.stringify(emptyCart));
    }
  }, []);

  const fetchCart = useCallback(async () => {
    if (!isAuthenticated) {
      initializeGuestCart();
      return;
    }

    try {
      setLoading(true);
      const cartData = await cartService.getCart();
      setCart(cartData);
    } catch (error) {
      console.error('Failed to fetch cart:', error);
      // Fallback to guest cart if authenticated cart fails
      initializeGuestCart();
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, initializeGuestCart]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  const addToCart = async (productVariantId, quantity) => {
    try {
      if (isAuthenticated) {
        // Authenticated user - use backend cart service
        const updatedCart = await cartService.addItem(productVariantId, quantity);
        setCart(updatedCart);
        return updatedCart;
      } else {
        // Guest user - use local storage cart
        const currentCart = cart || { id: 'guest-cart', items: [], totalAmount: 0, totalItems: 0 };

        // Check if item already exists in cart
        const existingItemIndex = currentCart.items.findIndex(item => item.productVariantId === productVariantId);

        if (existingItemIndex >= 0) {
          // Update existing item quantity
          currentCart.items[existingItemIndex].quantity += quantity;
        } else {
          // Add new item to cart
          currentCart.items.push({
            id: Date.now(), // Temporary ID for guest cart
            productVariantId,
            quantity,
            // These would normally come from the backend, but for demo we'll use placeholders
            productName: 'Product',
            price: 0,
            totalPrice: 0
          });
        }

        // Recalculate totals
        currentCart.totalItems = currentCart.items.reduce((sum, item) => sum + item.quantity, 0);
        currentCart.totalAmount = currentCart.items.reduce((sum, item) => sum + (item.totalPrice || 0), 0);

        // Save to localStorage and update state
        localStorage.setItem('guestCart', JSON.stringify(currentCart));
        setCart(currentCart);
        return currentCart;
      }
    } catch (error) {
      throw error;
    }
  };

  const updateQuantity = async (productVariantId, quantity) => {
    try {
      // Try to update backend first
      const updatedCart = await cartService.updateQuantity(productVariantId, quantity);
      setCart(updatedCart);
      return updatedCart;
    } catch (error) {
      console.log('Backend update failed, updating local cart:', error);

      // Fallback to local cart update
      if (cart && cart.items) {
        const updatedItems = cart.items.map(item => {
          if (item.productVariantId === productVariantId || item.id === productVariantId) {
            return { ...item, quantity: Math.max(0, quantity) };
          }
          return item;
        }).filter(item => item.quantity > 0); // Remove items with 0 quantity

        const updatedCart = {
          ...cart,
          items: updatedItems,
          totalItems: updatedItems.reduce((sum, item) => sum + item.quantity, 0),
          totalAmount: updatedItems.reduce((sum, item) => sum + (item.unitPrice || item.price || 0) * item.quantity, 0)
        };

        setCart(updatedCart);

        // Update localStorage for guest cart
        if (!isAuthenticated) {
          localStorage.setItem('guestCart', JSON.stringify(updatedCart));
        }

        return updatedCart;
      }
      throw error;
    }
  };

  const removeFromCart = async (productVariantId) => {
    try {
      // Try to remove from backend first
      const updatedCart = await cartService.removeItem(productVariantId);
      setCart(updatedCart);
      return updatedCart;
    } catch (error) {
      console.log('Backend remove failed, updating local cart:', error);

      // Fallback to local cart update
      if (cart && cart.items) {
        const updatedItems = cart.items.filter(item =>
          item.productVariantId !== productVariantId &&
          item.id !== productVariantId
        );

        const updatedCart = {
          ...cart,
          items: updatedItems,
          totalItems: updatedItems.reduce((sum, item) => sum + item.quantity, 0),
          totalAmount: updatedItems.reduce((sum, item) => sum + (item.unitPrice || item.price || 0) * item.quantity, 0)
        };

        setCart(updatedCart);

        // Update localStorage for guest cart
        if (!isAuthenticated) {
          localStorage.setItem('guestCart', JSON.stringify(updatedCart));
        }

        return updatedCart;
      }
      throw error;
    }
  };

  const clearCart = async () => {
    try {
      // Try to clear backend cart first
      await cartService.clearCart();
    } catch (error) {
      console.log('Backend clear failed, clearing local cart:', error);
    }

    // Always clear local cart regardless of backend success/failure
    const emptyCart = {
      id: isAuthenticated ? 'user-cart' : 'guest-cart',
      items: [],
      totalAmount: 0,
      totalItems: 0
    };

    setCart(emptyCart);

    // Update localStorage for guest cart
    if (!isAuthenticated) {
      localStorage.setItem('guestCart', JSON.stringify(emptyCart));
    }
  };

  const getCartItemCount = () => {
    return cart?.totalItems || 0;
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
