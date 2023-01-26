package ru.vsu.csf.asashina.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.repository.CategoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private List<Category> createValidCategoryList() {
        return List.of(new Category(1L, "Name 1"));
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
}