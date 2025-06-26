package com.dreamcollections.services.order.repository;

import com.dreamcollections.services.order.model.OrderItem;
// import com.dreamcollections.services.order.model.Order; // Not strictly needed
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
