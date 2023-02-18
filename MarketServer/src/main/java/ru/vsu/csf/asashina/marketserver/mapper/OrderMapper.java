package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderWithUserDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Order;

@Mapper(uses = {OrderProductMapper.class, UserMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDTOFromEntity(Order entity);

    OrderWithUserDTO toWithUserDTOFromEntity(Order entity);
}
