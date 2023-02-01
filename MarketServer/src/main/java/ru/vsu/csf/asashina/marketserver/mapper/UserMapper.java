package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.User;
import ru.vsu.csf.asashina.marketserver.model.request.UserSignUpRequest;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    User toEntityFromSignUpRequest(UserSignUpRequest request, String passwordHash);

    @Mapping(target = "roles", expression = "java(roleMapper.toDTOFromEntitySet(entity.getRoles()))")
    UserDTO toDTOFromEntity(User entity);

    @Mapping(target = "roles", expression = "java(roleMapper.toEntityFromDTOSet(dto.getRoles()))")
    User toEntityFromDTO(UserDTO dto);
}
