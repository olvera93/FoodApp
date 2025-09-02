package com.olvera.foodApp.auth_users.services.impl;

import com.olvera.foodApp.auth_users.dtos.UserDTO;
import com.olvera.foodApp.auth_users.entity.User;
import com.olvera.foodApp.auth_users.repository.UserRepository;
import com.olvera.foodApp.auth_users.services.UserService;
import com.olvera.foodApp.aws.AwsS3Service;
import com.olvera.foodApp.email_notification.dtos.NotificationDTO;
import com.olvera.foodApp.email_notification.services.NotificationService;
import com.olvera.foodApp.exceptions.BadRequestException;
import com.olvera.foodApp.exceptions.NotFoundException;
import com.olvera.foodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final NotificationService notificationService;

    private final AwsS3Service awsS3Service;

    @Override
    public User getCurrentLoggedInUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {

        log.info("INSIDE getAllUsers()");

        List<User> userList = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<UserDTO> userDTOS = modelMapper.map(userList, new TypeToken<List<UserDTO>>() {
        }.getType());

        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retreived successfully")
                .data(userDTOS)
                .build();
    }

    @Override
    public Response<UserDTO> getOwnAccountDetails() {

        log.info("INSIDE getOwnAccountDetails()");

        User user = getCurrentLoggedInUser();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<?>  updateOwnAccount(UserDTO userDTO) {

        log.info("INSIDE updateOwnAccount()");

        User user = getCurrentLoggedInUser();

        String profileUrl = user.getProfileUrl();

        MultipartFile imageFile = userDTO.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            if (profileUrl != null && !profileUrl.isEmpty()) {
                String keyName = profileUrl.substring(profileUrl.lastIndexOf("/") + 1);
                awsS3Service.deleteFile("profile/" + keyName);
                log.info("Deleted old profile image from s3");
            }

            // upload new image
            String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            URL newImageUrl = awsS3Service.uploadFile("profile/" + imageName, imageFile);
            user.setProfileUrl(newImageUrl.toString());
        }

        // update user details
        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
        if (userDTO.getPassword() != null) user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
        }

        user.setEmail(userDTO.getEmail());

        // save the user
        userRepository.save(user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account updated successfully")
                .build();

    }

    @Override
    public Response<?> deactivateOwnAccount() {

        log.info("INSIDE deactivateOwnAccount()");

        User user = getCurrentLoggedInUser();

        // deactivate the user
        user.setActive(false);
        userRepository.save(user);

        // SEND EMAIL AFTER DEACTIVATION
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Account Deactivated")
                .body("Your account has been deactivated. If this was a mistake, please contact support.")
                .build();

        notificationService.sendEmail(notificationDTO);

        // Return a success response
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account deactivated successfully")
                .build();

    }
}
