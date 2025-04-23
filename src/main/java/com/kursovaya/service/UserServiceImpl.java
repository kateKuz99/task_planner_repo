package com.kursovaya.service;

import com.kursovaya.dto.UserUpdateDto;
import com.kursovaya.model.UserEntity;
import com.kursovaya.repos.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailServiceImpl mailService;
    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Override
    public Mono<UserEntity> getUserByUsername(String username) {
        logger.info("Get user by username - "+username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Mono<UserEntity> getUserById(Long id) {
        logger.info("Get user by id - "+id);
        return userRepository.findById(id);
    }

    public Mono<UserEntity> registerUser(UserEntity user) {
        return userRepository.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .enabled(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .activationCode(activationCodeGeneration())
                        .build()
        ).doOnSuccess(u -> {
            System.out.println("IN registerUser - user: " + user + "created");
            if (!u.getEmail().isEmpty()) {

               // String message = String.format("Hello %s!\nThis is your activation code: %s. Past it and your account will be activated",u.getUsername(),u.getActivationCode());
               // mailService.sendSimpleEmail(u.getEmail(),"Activation code", message);
                mailService.sendHtmlEmail(u.getEmail(), "Activation code", u.getActivationCode());
                System.out.println(u.getActivationCode());
                logger.info("Register new user - "+u.toString());
            }
        });
    }

    @Override
    public Mono<Boolean> activateUser(String code) {
        return userRepository.findByActivationCode(code)
                .flatMap(userEntity -> {
                    userEntity.setEnabled(true);
                    userEntity.setActivationCode(null);
                    return userRepository.save(userEntity);
                })
                .flatMap(savedUser -> {
                    logger.info("Activate user - "+savedUser.getId());
                    return Mono.just(true);
                })
                .onErrorResume(e -> {
                    logger.error("Error to activate user");
                    return Mono.just(false);
                })
                .switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<UserEntity> getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    private String activationCodeGeneration(){
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<8;i++) result.append(random.nextInt(0,9));
        return result.toString();
    }

    public Mono<UserEntity> updateUser(Long userId, UserUpdateDto userUpdateDto) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    // Обновляем поля, если они переданы в DTO
                    if (userUpdateDto.getUsername() != null) {
                        user.setUsername(userUpdateDto.getUsername());
                    }
                    if (userUpdateDto.getFirstName() != null) {
                        user.setFirstName(userUpdateDto.getFirstName());
                    }
                    if (userUpdateDto.getLastName() != null) {
                        user.setLastName(userUpdateDto.getLastName());
                    }
                    if (userUpdateDto.getEmail() != null) {
                        user.setEmail(userUpdateDto.getEmail());
                    }
                    if (userUpdateDto.getPassword() != null) {
                        String hashedPassword = passwordEncoder.encode(userUpdateDto.getPassword());
                        user.setPassword(hashedPassword);  // Сохраняем хешированный пароль
                    }
                    return userRepository.save(user); // Сохраняем обновленную сущность
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found"))); // Если пользователь не найден
    }

    @Override
    public Mono<UserEntity> uploadAvatar(Long userId, FilePart filePart) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user ->
                        DataBufferUtils.join(filePart.content())
                                .map(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer); // обязательно освободить буфер
                                    return bytes;
                                })
                                .publishOn(Schedulers.boundedElastic()) // блокирующая операция
                                .map(bytes -> {
                                    // Генерируем уникальное имя файла
                                    String fileName = UUID.randomUUID() + "-" + filePart.filename();

                                    // Ключ — это просто имя файла, а не "avatars/имя", чтобы не дублировать
                                    String key = fileName;

                                    // Загружаем в Minio/S3
                                    s3Client.putObject(
                                            PutObjectRequest.builder()
                                                    .bucket(bucket)
                                                    .key(key)
                                                    .contentType(filePart.headers().getContentType().toString())
                                                    .build(),
                                            RequestBody.fromBytes(bytes)
                                    );

                                    // Создаём корректный URL, без двойного /avatars
                                    String avatarUrl = minioUrl + "/" + bucket + "/" + key;
                                    user.setAvatarUrl(avatarUrl);
                                    return user;
                                })
                                .flatMap(userRepository::save)
                );
    }


    @Override
    public Mono<Resource> getAvatar(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found"))) // Если пользователь не найден
                .flatMap(user -> {
                    String avatarUrl = user.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        // Загружаем аватар из S3
                        String key = avatarUrl.replace(minioUrl + "/" + bucket + "/", "");
                        return Mono.fromCallable(() -> {
                            // Скачиваем файл с S3
                            byte[] fileBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                                    .bucket(bucket)
                                    .key(key)
                                    .build()).asByteArray();
                            return new ByteArrayResource(fileBytes);
                        });
                    } else {
                        return Mono.error(new RuntimeException("Avatar not found"));
                    }
                });
    }

    public Mono<Void> deleteUserById(Long userId) {
        return userRepository.deleteById(userId);
    }










}




