package com.olvera.foodApp.cart.services;

import com.olvera.foodApp.cart.dtos.CartDTO;
import com.olvera.foodApp.response.Response;

public interface CartService {

    Response<?> addItemToCart(CartDTO cartDTO);

    Response<?> incrementItem(Long menuId);

    Response<?> decrementItem(Long menuId);

    Response<?> removeItem(Long cartItemid);

    Response<CartDTO> getShoppingCart();

    Response<?> clearShoppingCart();

}
