package com.kursovaya.mapper;

import com.kursovaya.dto.UserDto;
import com.kursovaya.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<UserEntity, UserDto>{
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    UserDto toDto(UserEntity user);

    @Mapping(source = "avatarUrl", target = "avatarUrl")
    UserEntity toEntity(UserDto dto);
}
