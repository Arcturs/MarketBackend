package ru.vsu.csf.asashina.market.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.csf.asashina.market.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    List<CategoryDTO> toDTOFromEntityList(List<Category> entities);
}
