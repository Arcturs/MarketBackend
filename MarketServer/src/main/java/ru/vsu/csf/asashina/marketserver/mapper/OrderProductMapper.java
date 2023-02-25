package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderProductDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.OrderProduct;

import java.math.BigDecimal;

@Mapper(uses = ProductMapper.class, imports = BigDecimal.class)
public interface OrderProductMapper {

    OrderProductMapper INSTANCE = Mappers.getMapper(OrderProductMapper.class);

    @Mapping(target = "overallPrice",
            expression = "java(entity.getProduct().getPrice().multiply(BigDecimal.valueOf(entity.getAmount())))")
    OrderProductDTO toDTOFromEntity(OrderProduct entity);
}
