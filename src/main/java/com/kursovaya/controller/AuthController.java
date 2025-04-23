package com.kursovaya.controller;

import com.kursovaya.dto.*;
import com.kursovaya.mapper.UserMainInfoMapper;
import com.kursovaya.mapper.UserMapper;
import com.kursovaya.model.UserEntity;
import com.kursovaya.security.CustomPrincipal;
import com.kursovaya.security.SecurityService;
import com.kursovaya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserMainInfoMapper userMainInfoMapper;

    @PostMapping("/auth/register")
    @Operation(summary = "Register new user")
    public Mono<UserDto> register(@RequestBody UserDto userDto) {
        UserEntity entity = userMapper.toEntity(userDto);
        return userService.registerUser(entity).map(userMapper::toDto);
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Authenticate user")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(AuthResponseDto.builder()
                        .userId(tokenDetails.getUserId())
                        .token(tokenDetails.getToken())
                        .issuedAt(tokenDetails.getIssuedAt())
                        .expiresAt(tokenDetails.getExpiresAt())
                        .role(tokenDetails.getRole().toString())
                        .build()));
    }

    @GetMapping("/auth/info")
    @Operation(summary = "User's information")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getUserById(customPrincipal.getId()).map(userMapper::toDto);
    }

    @GetMapping("/auth/activate/{code}")
    @Operation(summary = "Activate user account ")
    public Mono<ServerResponse>activate(@PathVariable String code) {
        return userService.activateUser(code).flatMap(flag -> {
            if (flag) return ServerResponse.ok().bodyValue("Account successfully activated");
            else return ServerResponse.badRequest().bodyValue("Account wasn't activated");
        });
    }

    @GetMapping("/users")
    @Operation(summary = "Getting all users")
    public Flux<UserMainInfoDto> getAllUsers() {
        return userService.getAllUsers()
                .map(userMainInfoMapper::toDto);
    }

    @PutMapping("/auth/update")
    @Operation(summary = "Update user's information")
    public Mono<UserEntity> updateUser(@RequestBody UserUpdateDto userUpdateDto, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.updateUser(customPrincipal.getId(), userUpdateDto);
    }

    @PostMapping(value = "/auth/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload user avatar")
    public Mono<UserDto> uploadAvatar(
            @RequestPart("avatar") FilePart avatar,
            Authentication authentication) {

        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userService.uploadAvatar(customPrincipal.getId(), avatar)
                .map(userMapper::toDto)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.error(new RuntimeException("Error uploading avatar", e));
                });
    }

    @GetMapping("/auth/avatar")
    @Operation(summary = "Get user avatar")
    public Mono<ResponseEntity<Resource>> getAvatar(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userService.getAvatar(customPrincipal.getId())
                .map(resource -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource))  // Возвращаем изображение
                .defaultIfEmpty(ResponseEntity.notFound().build());  // Возвращаем 404, если аватар не найден
    }

    @DeleteMapping("/auth/{userId}")
    @Operation(summary = "Delete user by ID (only for admins)")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable Long userId) {
        return userService.deleteUserById(userId)
                .thenReturn(ResponseEntity.ok("User successfully deleted"))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.internalServerError()
                            .body("Error deleting user: " + e.getMessage()));
                });
    }



}
