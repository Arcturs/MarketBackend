package ru.vsu.csf.asashina.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.market.mapper.CategoryMapper;
import ru.vsu.csf.asashina.market.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public List<Category> getCategoryListByIds(List<Long> ids) {
        if (ids != null) {
            return categoryRepository.findAllByCategoryIdIn(ids);
        }
        return null;
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDTOFromEntityList(categories);
    }
}