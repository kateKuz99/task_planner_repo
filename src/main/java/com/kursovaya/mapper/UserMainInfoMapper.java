package com.kursovaya.mapper;

import com.kursovaya.dto.UserMainInfoDto;
import com.kursovaya.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMainInfoMapper extends Mappable<UserEntity, UserMainInfoDto>{
}
