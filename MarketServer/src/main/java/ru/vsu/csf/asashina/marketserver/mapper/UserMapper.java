package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserWithoutPasswordDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.User;
import ru.vsu.csf.asashina.marketserver.model.request.UserSignUpRequest;

@Mapper(uses = RoleMapper.class)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntityFromSignUpRequest(UserSignUpRequest request, String passwordHash);

    UserDTO toDTOFromEntity(User entity);

    UserWithoutPasswordDTO toWithoutPasswordDTOFromEntity(User entity);

    @Mapping(target = "roles", expression = "java(roleMapper.toEntityFromDTOSet(dto.getRoles()))")
    User toEntityFromDTO(UserDTO dto);
}
