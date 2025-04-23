package com.kursovaya.repos;

import com.kursovaya.model.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByUsername(String username);

    Mono<UserEntity> findByActivationCode(String code);

    Mono<UserEntity> findByEmail(String userEmail);


}
