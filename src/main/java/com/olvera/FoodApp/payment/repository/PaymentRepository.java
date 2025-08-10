package com.olvera.FoodApp.payment.repository;

import com.olvera.FoodApp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
