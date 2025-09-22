package com.olvera.foodApp.review.controller;

import com.olvera.foodApp.response.Response;
import com.olvera.foodApp.review.dtos.ReviewDTO;
import com.olvera.foodApp.review.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response<ReviewDTO>> createReview(
            @RequestBody @Valid ReviewDTO reviewDTO
    ) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }

    @GetMapping("/menu-item/{menuId}")
    public ResponseEntity<Response<List<ReviewDTO>>> getReviewsForMenu(
            @PathVariable Long menuId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsForMenu(menuId));
    }

    @GetMapping("/menu-item/average/{menuId}")
    public ResponseEntity<Response<Double>> getAverageRating(
            @PathVariable Long menuId) {
        return ResponseEntity.ok(reviewService.getAverageRating(menuId));
    }

}
