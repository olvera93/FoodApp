package com.olvera.foodApp.review.services;

import com.olvera.foodApp.response.Response;
import com.olvera.foodApp.review.dtos.ReviewDTO;

import java.util.List;

public interface ReviewService {

    Response<ReviewDTO> createReview(ReviewDTO reviewDTO);

    Response<List<ReviewDTO>> getReviewsForMenu(Long menuId);

    Response<Double> getAverageRating(Long menuId);

}
