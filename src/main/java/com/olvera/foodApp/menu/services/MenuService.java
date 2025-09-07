package com.olvera.foodApp.menu.services;

import com.olvera.foodApp.menu.dtos.MenuDTO;
import com.olvera.foodApp.response.Response;

import java.util.List;

public interface MenuService {

    Response<MenuDTO> createMenu(MenuDTO menuDTO);

    Response<MenuDTO> updateMenu(MenuDTO menuDTO);

    Response<MenuDTO> getMenuById(Long id);

    Response<?> deleteMenu(Long id);

    Response<List<MenuDTO>> getMenus(Long categoryId, String search);

}
