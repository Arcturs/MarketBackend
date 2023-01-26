package ru.vsu.csf.asashina.market.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.model.entity.Product;
import ru.vsu.csf.asashina.market.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.market.model.request.ProductUpdateRequest;

import java.util.List;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "categories", expression = "java(categoryMapper.toDTOFromEntityList(entity.getCategories()))")
    ProductDTO toDTOFromEntity(Product entity);

    Product toEntityFromCreateRequest(ProductCreateRequest request, List<Category> categories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromUpdateRequest(ProductUpdateRequest request, @MappingTarget Product entity);
}
