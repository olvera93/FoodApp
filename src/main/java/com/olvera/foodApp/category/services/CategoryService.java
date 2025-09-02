package com.olvera.foodApp.category.services;

import com.olvera.foodApp.category.dtos.CategoryDTO;
import com.olvera.foodApp.response.Response;

import java.util.List;

public interface CategoryService {

    Response<CategoryDTO> addCategory(CategoryDTO categoryDTO);

    Response<CategoryDTO> updateCategory(CategoryDTO categoryDTO);

    Response<CategoryDTO> getCategoryById(Long id);

    Response<List<CategoryDTO>> getAllCategories();

    Response<?> deleteCategory(Long id);

}
