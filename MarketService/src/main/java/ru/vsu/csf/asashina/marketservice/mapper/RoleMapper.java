package ru.vsu.csf.asashina.marketservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketservice.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.Role;

import java.util.Set;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO toDTOFromEntity(Role entity);

    Set<RoleDTO> toDTOFromEntitySet(Set<Role> entities);

    Set<Role> toEntityFromDTOSet(Set<RoleDTO> dtos);
}
