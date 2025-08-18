package com.olvera.FoodApp.auth_users.entity;

import com.olvera.FoodApp.cart.entity.Cart;
import com.olvera.FoodApp.order.entity.Order;
import com.olvera.FoodApp.payment.entity.Payment;
import com.olvera.FoodApp.review.entity.Review;
import com.olvera.FoodApp.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phoneNumber;

    private String profileUrl;

    private String address;

    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
