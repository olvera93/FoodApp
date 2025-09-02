package com.olvera.foodApp.auth_users.services;

import com.olvera.foodApp.auth_users.dtos.UserDTO;
import com.olvera.foodApp.auth_users.entity.User;
import com.olvera.foodApp.response.Response;

import java.util.List;

public interface UserService {

    User getCurrentLoggedInUser();

    Response<List<UserDTO>> getAllUsers();

    Response<UserDTO> getOwnAccountDetails();

    Response<?> updateOwnAccount(UserDTO userDTO);

    Response<?> deactivateOwnAccount();

}
