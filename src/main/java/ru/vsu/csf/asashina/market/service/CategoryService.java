package ru.vsu.csf.asashina.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.market.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.market.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.market.mapper.CategoryMapper;
import ru.vsu.csf.asashina.market.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.model.request.CategoryCreateRequest;
import ru.vsu.csf.asashina.market.repository.CategoryRepository;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public Set<CategoryDTO> getCategoryDTOSetByIds(List<Long> ids) {
        if (ids != null) {
            Set<Category> categories = categoryRepository.findAllByCategoryIdIn(ids);
            return categoryMapper.toDTOFromEntitySet(categories);
        }
        return null;
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDTOFromEntityList(categories);
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return categoryMapper.toDTOFromEntity(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ObjectNotExistException("Category with following id does not exist")
        );
    }

    @Transactional
    public CategoryDTO createCategoryFromCreateRequest(CategoryCreateRequest request) {
        if (categoryRepository.existsCategoryByNameIgnoreCase(request.getName())) {
            throw new ObjectAlreadyExistsException("Category with following name already exists");
        }
        Category beforeSavingToRepositoryCategory = categoryMapper.toEntityFromCreateRequest(request);
        Category categoryWithId = categoryRepository.save(beforeSavingToRepositoryCategory);
        return categoryMapper.toDTOFromEntity(categoryWithId);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }
}
