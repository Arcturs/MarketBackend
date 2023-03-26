package ru.vsu.csf.asashina.marketservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketservice.model.dto.OrderDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.OrderWithUserDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.Order;

import java.time.Instant;

@Mapper(uses = {OrderProductMapper.class, UserMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDTOFromEntity(Order entity);

    OrderWithUserDTO toWithUserDTOFromEntity(Order entity);

    @Mapping(target = "isPaid", expression = "java(false)")
    Order createEntity(String orderNumber, Instant created, UserDTO user);
}
