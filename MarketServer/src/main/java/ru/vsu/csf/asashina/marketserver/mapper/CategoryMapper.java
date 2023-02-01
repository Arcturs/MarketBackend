package ru.vsu.csf.asashina.marketserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.marketserver.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Category;
import ru.vsu.csf.asashina.marketserver.model.request.CategoryCreateRequest;

import java.util.List;
import java.util.Set;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Set<CategoryDTO> toDTOFromEntitySet(Set<Category> entities);

    Set<Category> toEntityFromDTOSet(Set<CategoryDTO> dtos);

    List<CategoryDTO> toDTOFromEntityList(List<Category> entities);

    CategoryDTO toDTOFromEntity(Category entity);

    Category toEntityFromCreateRequest(CategoryCreateRequest request);
}
