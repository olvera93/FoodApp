package com.olvera.foodApp.role.services;

import com.olvera.foodApp.response.Response;
import com.olvera.foodApp.role.dtos.RoleDTO;

import java.util.List;

public interface RoleService {

    Response<RoleDTO> createRole(RoleDTO roleDTO);

    Response<RoleDTO> updateRole(RoleDTO roleDTO);

    Response<List<RoleDTO>> getAllRoles();

    Response<?> deleteRole(Long id);

}
