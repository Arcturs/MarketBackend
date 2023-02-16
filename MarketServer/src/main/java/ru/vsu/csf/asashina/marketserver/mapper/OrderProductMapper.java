package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderProductDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.OrderProduct;

@Mapper(uses = ProductMapper.class)
public interface OrderProductMapper {

    OrderProductMapper INSTANCE = Mappers.getMapper(OrderProductMapper.class);

    @Mapping(target = "overallPrice", expression = "java(entity.getAmount() * entity.getProduct().getPrice())")
    OrderProductDTO toDTOFromEntity(OrderProduct entity);
}
