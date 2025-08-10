package com.olvera.FoodApp.order.repository;

import com.olvera.FoodApp.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
