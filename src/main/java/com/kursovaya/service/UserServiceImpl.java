package com.kursovaya.service;

import com.kursovaya.dto.UserUpdateDto;
import com.kursovaya.model.UserEntity;
import com.kursovaya.repos.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

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

    public Mono<UserEntity> uploadAvatar(Long userId, MultipartFile avatarFile) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    String key = "avatars/" + UUID.randomUUID() + "-" + avatarFile.getOriginalFilename();

                    return Mono.fromCallable(() -> {
                        s3Client.putObject(
                                PutObjectRequest.builder()
                                        .bucket("avatars")
                                        .key(key)
                                        .contentType(avatarFile.getContentType())
                                        .build(),
                                software.amazon.awssdk.core.sync.RequestBody.fromBytes(avatarFile.getBytes())
                        );

                        // Генерим ссылку на файл
                        String avatarUrl = minioUrl + "/avatars/" + key;

                        user.setAvatarUrl(avatarUrl);
                        return user;
                    }).flatMap(userRepository::save);
                });
    }

    private String uploadToMinio(MultipartFile file) throws IOException {
        // Генерируем уникальное имя файла
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Загружаем файл в MinIO
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(file.getBytes()));

        // Генерируем URL для доступа к файлу
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fileName)).toString();
    }
}

