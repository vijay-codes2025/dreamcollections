import api from './api';
import authService from './authService';

class CartService {
  // Get cart items
  async getCart() {
    try {
      // If user is authenticated, get cart from backend
      if (authService.isAuthenticated()) {
        const response = await api.get('/carts/mine');
        return response.data;
      } else {
        // For guest users, use localStorage
        return this.getGuestCart();
      }
    } catch (error) {
      console.error('Error fetching cart:', error);
      // Fallback to localStorage
      return this.getGuestCart();
    }
  }

  // Add item to cart
  async addToCart(productVariantId, quantity = 1) {
    try {
      // If user is authenticated, add to backend cart
      if (authService.isAuthenticated()) {
        const response = await api.post('/carts/mine/items', {
          productVariantId,
          quantity
        });
        return response.data;
      } else {
        // For guest users, use localStorage
        return this.addToGuestCart(productVariantId, quantity);
      }
    } catch (error) {
      console.error('Error adding to cart:', error);
      // Fallback to localStorage
      return this.addToGuestCart(productVariantId, quantity);
    }
  }

  // Update cart item quantity
  async updateCartItem(productVariantId, quantity) {
    try {
      // If user is authenticated, update backend cart
      if (authService.isAuthenticated()) {
        const response = await api.put(`/carts/mine/items/${productVariantId}`, {
          quantity
        });
        return response.data;
      } else {
        // For guest users, use localStorage
        return this.updateGuestCartItem(productVariantId, quantity);
      }
    } catch (error) {
      console.error('Error updating cart:', error);
      // Fallback to localStorage
      return this.updateGuestCartItem(productVariantId, quantity);
    }
  }

  // Remove item from cart
  async removeFromCart(productVariantId) {
    try {
      // If user is authenticated, remove from backend cart
      if (authService.isAuthenticated()) {
        const response = await api.delete(`/carts/mine/items/${productVariantId}`);
        return response.data;
      } else {
        // For guest users, use localStorage
        return this.removeFromGuestCart(productVariantId);
      }
    } catch (error) {
      console.error('Error removing from cart:', error);
      // Fallback to localStorage
      return this.removeFromGuestCart(productVariantId);
    }
  }

  // Clear cart
  async clearCart() {
    try {
      // If user is authenticated, clear backend cart
      if (authService.isAuthenticated()) {
        const response = await api.delete('/carts/mine');
        return response.data;
      } else {
        // For guest users, clear localStorage cart
        return this.clearGuestCart();
      }
    } catch (error) {
      console.error('Error clearing cart:', error);
      // Fallback to localStorage
      return this.clearGuestCart();
    }
  }

  // Sync guest cart with user cart after login
  async syncGuestCart() {
    try {
      const guestCart = this.getGuestCart();
      if (guestCart.items && guestCart.items.length > 0) {
        // Add each guest cart item to user's cart
        for (const item of guestCart.items) {
          await this.addToCart(item.productVariantId, item.quantity);
        }
        // Clear guest cart after sync
        this.clearGuestCart();
      }
    } catch (error) {
      console.error('Error syncing guest cart:', error);
    }
  }

  // Guest cart methods (localStorage-based)
  getGuestCart() {
    try {
      const cart = localStorage.getItem('guestCart');
      return cart ? JSON.parse(cart) : { items: [], totalAmount: 0 };
    } catch (error) {
      console.error('Error getting guest cart:', error);
      return { items: [], totalAmount: 0 };
    }
  }

  addToGuestCart(productVariantId, quantity) {
    try {
      const cart = this.getGuestCart();
      const existingItem = cart.items.find(item => item.productVariantId === productVariantId);
      
      if (existingItem) {
        existingItem.quantity += quantity;
      } else {
        cart.items.push({ productVariantId, quantity });
      }
      
      this.saveGuestCart(cart);
      return cart;
    } catch (error) {
      console.error('Error adding to guest cart:', error);
      return this.getGuestCart();
    }
  }

  updateGuestCartItem(productVariantId, quantity) {
    try {
      const cart = this.getGuestCart();
      const item = cart.items.find(item => item.productVariantId === productVariantId);
      
      if (item) {
        if (quantity <= 0) {
          cart.items = cart.items.filter(item => item.productVariantId !== productVariantId);
        } else {
          item.quantity = quantity;
        }
      }
      
      this.saveGuestCart(cart);
      return cart;
    } catch (error) {
      console.error('Error updating guest cart:', error);
      return this.getGuestCart();
    }
  }

  removeFromGuestCart(productVariantId) {
    try {
      const cart = this.getGuestCart();
      cart.items = cart.items.filter(item => item.productVariantId !== productVariantId);
      this.saveGuestCart(cart);
      return cart;
    } catch (error) {
      console.error('Error removing from guest cart:', error);
      return this.getGuestCart();
    }
  }

  clearGuestCart() {
    try {
      localStorage.removeItem('guestCart');
      return { items: [], totalAmount: 0 };
    } catch (error) {
      console.error('Error clearing guest cart:', error);
      return { items: [], totalAmount: 0 };
    }
  }

  saveGuestCart(cart) {
    try {
      localStorage.setItem('guestCart', JSON.stringify(cart));
    } catch (error) {
      console.error('Error saving guest cart:', error);
    }
  }

  // Get cart item count
  async getCartItemCount() {
    try {
      const cart = await this.getCart();
      return cart.items ? cart.items.reduce((total, item) => total + item.quantity, 0) : 0;
    } catch (error) {
      console.error('Error getting cart item count:', error);
      return 0;
    }
  }
}

export default new CartService();
