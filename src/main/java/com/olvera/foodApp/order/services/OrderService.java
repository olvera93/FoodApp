package com.olvera.foodApp.order.services;

import com.olvera.foodApp.enums.OrderStatus;
import com.olvera.foodApp.order.dtos.OrderDTO;
import com.olvera.foodApp.order.dtos.OrderItemDTO;
import com.olvera.foodApp.response.Response;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Response<?> placeOrderFromCart();

    Response<OrderDTO> getOrderById(Long id);

    Response<Page<OrderDTO>> getAllOrders(OrderStatus orderStatus, int page, int size);

    Response<List<OrderDTO>> getOrderOfUser();

    Response<OrderItemDTO> getOrderItemById(Long orderItemId);

    Response<OrderDTO> updateOrderStatus(OrderDTO orderDTO);

    Response<Long> countUniqueCustomers();

}
