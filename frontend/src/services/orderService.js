import api from './api';
import authService from './authService';

class OrderService {
  // Create a new order
  async createOrder(orderData) {
    try {
      // Always try to create order in backend first
      const response = await api.post('/orders', {
        customerName: orderData.customerName,
        customerPhone: orderData.customerPhone,
        customerEmail: orderData.customerEmail || null,
        shippingAddress: {
          street: orderData.address,
          city: orderData.city,
          stateOrProvince: orderData.state,
          postalCode: orderData.postalCode,
          country: 'India',
          contactPhone: orderData.customerPhone
        },
        billingAddress: null, // Same as shipping for now
        paymentMethod: orderData.paymentMethod
      });
      return response.data;
    } catch (error) {
      console.error('Error creating order:', error);
      // Fallback to localStorage for guest orders
      return this.createGuestOrder(orderData);
    }
  }

  // Get user's orders
  async getMyOrders() {
    try {
      // Always try backend first
      const response = await api.get('/orders/my-orders');
      return response.data;
    } catch (error) {
      console.error('Error fetching orders from backend:', error);
      // Fallback to localStorage
      return this.getGuestOrders();
    }
  }

  // Get order by ID
  async getOrderById(orderId) {
    try {
      // Always try backend first
      const response = await api.get(`/orders/${orderId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching order from backend:', error);
      // Fallback to localStorage
      return this.getGuestOrderById(orderId);
    }
  }

  // Cancel order
  async cancelOrder(orderId) {
    try {
      // If user is authenticated, cancel order in backend
      if (authService.isAuthenticated()) {
        const response = await api.put(`/orders/${orderId}/cancel`);
        return response.data;
      } else {
        // For guest users, update order status in localStorage
        return this.cancelGuestOrder(orderId);
      }
    } catch (error) {
      console.error('Error cancelling order:', error);
      // Fallback to localStorage
      return this.cancelGuestOrder(orderId);
    }
  }

  // Guest order methods (localStorage-based)
  createGuestOrder(orderData) {
    try {
      const orderId = this.generateOrderId();
      const order = {
        id: orderId,
        customerName: orderData.customerName,
        customerPhone: orderData.customerPhone,
        customerEmail: orderData.customerEmail || null,
        shippingAddress: {
          street: orderData.address,
          city: orderData.city,
          state: orderData.state,
          postalCode: orderData.postalCode,
          country: 'India'
        },
        paymentMethod: orderData.paymentMethod,
        items: orderData.items,
        totalAmount: orderData.totalAmount,
        status: 'CONFIRMED',
        orderDate: new Date().toISOString(),
        createdAt: new Date().toISOString()
      };

      // Save to localStorage
      const orders = this.getGuestOrders();
      orders.push(order);
      localStorage.setItem('userOrders', JSON.stringify(orders));

      return order;
    } catch (error) {
      console.error('Error creating guest order:', error);
      throw new Error('Failed to create order');
    }
  }

  getGuestOrders() {
    try {
      const orders = localStorage.getItem('userOrders');
      return orders ? JSON.parse(orders) : [];
    } catch (error) {
      console.error('Error getting guest orders:', error);
      return [];
    }
  }

  getGuestOrderById(orderId) {
    try {
      const orders = this.getGuestOrders();
      const order = orders.find(order => order.id.toString() === orderId.toString());
      if (!order) {
        throw new Error('Order not found');
      }
      return order;
    } catch (error) {
      console.error('Error getting guest order by ID:', error);
      throw new Error('Order not found');
    }
  }

  cancelGuestOrder(orderId) {
    try {
      const orders = this.getGuestOrders();
      const orderIndex = orders.findIndex(order => order.id.toString() === orderId.toString());
      
      if (orderIndex === -1) {
        throw new Error('Order not found');
      }

      orders[orderIndex].status = 'CANCELLED';
      orders[orderIndex].updatedAt = new Date().toISOString();
      
      localStorage.setItem('userOrders', JSON.stringify(orders));
      return orders[orderIndex];
    } catch (error) {
      console.error('Error cancelling guest order:', error);
      throw new Error('Failed to cancel order');
    }
  }

  // Generate unique order ID
  generateOrderId() {
    const timestamp = Date.now();
    const random = Math.floor(Math.random() * 1000000);
    return `${timestamp}${random}`;
  }

  // Sync guest orders with user account after login
  async syncGuestOrders() {
    try {
      const guestOrders = this.getGuestOrders();
      if (guestOrders.length > 0 && authService.isAuthenticated()) {
        // In a real implementation, you might want to sync these orders
        // For now, we'll keep them separate
        console.log('Guest orders found, keeping them in localStorage for now');
      }
    } catch (error) {
      console.error('Error syncing guest orders:', error);
    }
  }

  // Clear guest orders
  clearGuestOrders() {
    try {
      localStorage.removeItem('userOrders');
    } catch (error) {
      console.error('Error clearing guest orders:', error);
    }
  }

  // Get order statistics
  async getOrderStats() {
    try {
      if (authService.isAuthenticated()) {
        const response = await api.get('/orders/stats');
        return response.data;
      } else {
        // Calculate stats from guest orders
        const orders = this.getGuestOrders();
        return {
          totalOrders: orders.length,
          totalAmount: orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0),
          pendingOrders: orders.filter(order => order.status === 'PENDING_PAYMENT').length,
          completedOrders: orders.filter(order => order.status === 'CONFIRMED').length
        };
      }
    } catch (error) {
      console.error('Error getting order stats:', error);
      return {
        totalOrders: 0,
        totalAmount: 0,
        pendingOrders: 0,
        completedOrders: 0
      };
    }
  }
}

export default new OrderService();
