package com.olvera.foodApp.payment.services;

import com.olvera.foodApp.payment.dtos.PaymentDTO;
import com.olvera.foodApp.response.Response;

import java.util.List;

public interface PaymentService {

    Response<?> initializePayment(PaymentDTO paymentRequest);

    void updatePaymentForOrder(PaymentDTO paymentDTO);

    Response<List<PaymentDTO>> getAllPayments();

    Response<PaymentDTO> getPaymentById(Long paymentId);

}
