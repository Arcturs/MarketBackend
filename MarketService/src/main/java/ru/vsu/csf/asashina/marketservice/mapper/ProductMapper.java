package ru.vsu.csf.asashina.marketservice.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketservice.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.ProductDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.ProductDetailedDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.Product;
import ru.vsu.csf.asashina.marketservice.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.marketservice.model.request.ProductUpdateRequest;

import java.util.Set;

@Mapper(uses = CategoryMapper.class)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);;

    ProductDetailedDTO toDetailedDTOFromEntity(Product entity);

    ProductDTO toDTOFromEntity(Product entity);

    Product toEntityFromCreateRequest(ProductCreateRequest request, Set<CategoryDTO> categories);

    Product toEntityFromDetailedDTO(ProductDetailedDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromUpdateRequest(ProductUpdateRequest request, @MappingTarget Product entity);
}
