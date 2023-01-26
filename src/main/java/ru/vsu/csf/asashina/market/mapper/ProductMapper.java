package ru.vsu.csf.asashina.market.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.entity.Product;
import ru.vsu.csf.asashina.market.model.request.ProductCreateRequest;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDTOFromEntity(Product entity);

    Product toEntityFromCreateRequest(ProductCreateRequest request);
}
