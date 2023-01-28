package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.ProductDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Product;
import ru.vsu.csf.asashina.marketserver.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductUpdateRequest;

import java.util.Set;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "categories", expression = "java(categoryMapper.toDTOFromEntitySet(entity.getCategories()))")
    ProductDTO toDTOFromEntity(Product entity);

    @Mapping(target = "categories", expression = "java(categoryMapper.toEntityFromDTOSet(categories))")
    Product toEntityFromCreateRequest(ProductCreateRequest request, Set<CategoryDTO> categories);

    @Mapping(target = "categories", expression = "java(categoryMapper.toEntityFromDTOSet(dto.getCategories()))")
    Product toEntityFromDTO(ProductDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromUpdateRequest(ProductUpdateRequest request, @MappingTarget Product entity);
}
