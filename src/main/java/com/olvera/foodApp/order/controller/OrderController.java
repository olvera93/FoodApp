package com.olvera.foodApp.order.controller;

import com.olvera.foodApp.enums.OrderStatus;
import com.olvera.foodApp.order.dtos.OrderDTO;
import com.olvera.foodApp.order.dtos.OrderItemDTO;
import com.olvera.foodApp.order.services.OrderService;
import com.olvera.foodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Response<?>> checkout() {
        return ResponseEntity.ok(orderService.placeOrderFromCart());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<OrderDTO>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Response<List<OrderDTO>>> getMyOrders() {
        return ResponseEntity.ok(orderService.getOrderOfUser());
    }

    @GetMapping("/order-item/{orderItemId}")
    public ResponseEntity<Response<OrderItemDTO>> getOrderItemById(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderService.getOrderItemById(orderItemId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Page<OrderDTO>>> getAllOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(orderStatus, page, size));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<OrderDTO>> updateOrderStatus(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderDTO));
    }

    @GetMapping("/unique-customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Long>> countUniqueCustomers() {
        return ResponseEntity.ok(orderService.countUniqueCustomers());
    }

}
