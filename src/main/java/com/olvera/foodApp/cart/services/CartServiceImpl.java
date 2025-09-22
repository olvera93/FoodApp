package com.olvera.foodApp.cart.services;

import com.olvera.foodApp.auth_users.entity.User;
import com.olvera.foodApp.auth_users.services.UserService;
import com.olvera.foodApp.cart.dtos.CartDTO;
import com.olvera.foodApp.cart.entity.Cart;
import com.olvera.foodApp.cart.entity.CartItem;
import com.olvera.foodApp.cart.repository.CartItemRepository;
import com.olvera.foodApp.cart.repository.CartRepository;
import com.olvera.foodApp.exceptions.NotFoundException;
import com.olvera.foodApp.menu.entity.Menu;
import com.olvera.foodApp.menu.repository.MenuRepository;
import com.olvera.foodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;

    private final CartRepository cartRepository;

    private final MenuRepository menuRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Override
    public Response<?> addItemToCart(CartDTO cartDTO) {

        log.info("Inside addItemCart()");

        Long menuId = cartDTO.getMenuId();
        int quantity = cartDTO.getQuantity();

        User user = userService.getCurrentLoggedInUser();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu Item Not Found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        // Check if the item is already in the cart
        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getMenu().getId().equals(menuId))
                .findFirst();

        // If present, increment item
        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setSubTotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        } else {
            // if not present
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .menu(menu)
                    .quantity(quantity)
                    .pricePerUnit(menu.getPrice())
                    .subTotal(menu.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();

            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }

        //cartRepository.save(cart); // not, it will auto save and persists int the cart table

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item added to cart successfully")
                .build();
    }

    @Override
    public Response<?> incrementItem(Long menuId) {

        log.info("Inside incrementItem()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found for user"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(() -> new NotFoundException("Menu not found in cart"));

        int newQuantity = cartItem.getQuantity() + 1;

        cartItem.setQuantity(newQuantity);

        cartItem.setSubTotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));

        cartItemRepository.save(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity incremented successfully")
                .build();

    }

    @Override
    public Response<?> decrementItem(Long menuId) {

        log.info("Inside decrementItem()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(() -> new NotFoundException("Menu not found in cart"));

        int newQuantity = cartItem.getQuantity() - 1;

        if (newQuantity > 0) {
            cartItem.setQuantity(newQuantity);
            cartItem.setSubTotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));

            cartItemRepository.save(cartItem);
        } else {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity updated successfully")
                .build();
    }

    @Override
    public Response<?> removeItem(Long cartItemid) {

        log.info("Inside removeItem()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));

        CartItem cartItem = cartItemRepository.findById(cartItemid)
                .orElseThrow(() -> new NotFoundException("Cart Item Not Found"));

        if (!cart.getCartItems().contains(cartItem)) {
            throw new NotFoundException("Cart item does not belong to this user's cart");
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item removed from cart successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response<CartDTO> getShoppingCart() {

        log.info("Inside getShoppingCart()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found for user"));

        List<CartItem> cartItems = cart.getCartItems();

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (cartItems != null) { // Add null check here
            for (CartItem item : cartItems) {
                totalAmount = totalAmount.add(item.getSubTotal());
            }
        }

        cartDTO.setTotalAmount(totalAmount); // set the total amount

        // remove the review form the response
        if (cartDTO.getCartItems() != null) {
            cartDTO.getCartItems()
                    .forEach(item -> item.getMenu().setReviews(null));
        }

        return Response.<CartDTO> builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart retrieved successfully")
                .data(cartDTO)
                .build();
    }

    @Override
    public Response<?> clearShoppingCart() {

        log.info("Inside clearShoppingCart()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found for user"));

        // Delete cart items from the database first
        cartItemRepository.deleteAll(cart.getCartItems());

        // Clear the cart's items collection
        cart.getCartItems().clear();

        // update the database
        cartRepository.save(cart);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart cleared successfully")
                .build();
    }
}
