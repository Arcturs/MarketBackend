package ru.vsu.csf.asashina.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.csf.asashina.market.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.market.mapper.CategoryMapper;
import ru.vsu.csf.asashina.market.mapper.ProductMapper;
import ru.vsu.csf.asashina.market.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);;

    private List<Category> createValidCategoryList() {
        return List.of(new Category(1L, "Name 1"));
    }

    private List<CategoryDTO> createValidCategoryDTOList() {
        return List.of(new CategoryDTO(1L, "Name 1"));
    }

    private Category createValidCategory() {
        return new Category(1L, "Name 1");
    }

    private CategoryDTO createValidCategoryDTO() {
        return new CategoryDTO(1L, "Name 1");
    }

    @Test
    void getCategoryListByIdsWhenListNotNull() {
        //given
        List<Long> ids = List.of(1L);

        List<Category> expectedList = createValidCategoryList();

        when(categoryRepository.findAllByCategoryIdIn(ids)).thenReturn(expectedList);

        //when
        List<Category> result = categoryService.getCategoryListByIds(ids);

        //then
        assertEquals(expectedList, result);
    }

    @Test
    void getCategoryListByIdsWhenListIsNull() {
        //given
        List<Long> ids = null;

        //when
        List<Category> result = categoryService.getCategoryListByIds(ids);

        //then
        assertNull(result);
    }

    @Test
    void getAllCategoriesSuccess() {
        //given
        List<Category> categoriesFromRepository = createValidCategoryList();
        List<CategoryDTO> expectedList = createValidCategoryDTOList();

        when(categoryRepository.findAll()).thenReturn(categoriesFromRepository);

        //when
        List<CategoryDTO> result = categoryService.getAllCategories();

        //then
        assertEquals(expectedList, result);
    }

    @Test
    void getCategoryByIdSuccess() {
        //given
        long id = 1L;

        Category categoryFromRepository = createValidCategory();
        CategoryDTO expectedCategory = createValidCategoryDTO();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryFromRepository));

        //when
        CategoryDTO result = categoryService.getCategoryById(id);

        //then
        assertEquals(expectedCategory, result);
    }

    @Test
    void getCategoryByIdThrowsExceptionWhenCategoryDoesNotExist() {
        //given
        long id = 2L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> categoryService.getCategoryById(id)).isInstanceOf(ObjectNotExistException.class);
    }
}