package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderWithUserDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Order;
import ru.vsu.csf.asashina.marketserver.model.entity.User;

import java.time.Instant;

@Mapper(uses = {OrderProductMapper.class, UserMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDTOFromEntity(Order entity);

    OrderWithUserDTO toWithUserDTOFromEntity(Order entity);

    @Mapping(target = "isPaid", expression = "java(false)")
    Order createEntity(String orderNumber, Instant created, UserDTO user);
}
