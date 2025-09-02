package com.olvera.foodApp.auth_users.services;

import com.olvera.foodApp.auth_users.dtos.LoginRequest;
import com.olvera.foodApp.auth_users.dtos.LoginResponse;
import com.olvera.foodApp.auth_users.dtos.RegistrationRequest;
import com.olvera.foodApp.response.Response;

public interface AuthService  {

    Response<?> register(RegistrationRequest registrationRequest);

    Response<LoginResponse> login(LoginRequest loginRequest);

}
