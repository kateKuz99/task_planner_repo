package com.kursovaya.service;

import com.kursovaya.dto.UserMainInfoDto;
import com.kursovaya.dto.UserUpdateDto;
import com.kursovaya.model.UserEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserEntity> getUserByUsername(String username);

    Mono<UserEntity> getUserById(Long id);

    Mono<UserEntity> registerUser(UserEntity entity);

    Mono<Boolean> activateUser(String code);

    Mono<UserEntity> getUserByEmail(String userEmail);

    Flux<UserEntity> getAllUsers();

    Mono<UserEntity> updateUser(Long userId, UserUpdateDto userUpdateDto);

    public Mono<UserEntity> uploadAvatar(Long userId, MultipartFile avatar);

}
