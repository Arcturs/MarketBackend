package ru.vsu.csf.asashina.market.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.vsu.csf.asashina.market.exception.PageException;
import ru.vsu.csf.asashina.market.mapper.ProductMapper;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.entity.Product;
import ru.vsu.csf.asashina.market.repository.ProductRepository;
import ru.vsu.csf.asashina.market.validator.PageValidator;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Spy
    private PageValidator pageValidator;

    @Test
    void getAllProductsInPagesByNameSuccess() {
        //given
        int pageNumber = 1;
        int size = 2;
        String name = "";
        boolean isAsc = true;

        Page<Product> pagesFromRepository = createValidPages();
        Page<ProductDTO> expectedPages = createValidPagesDTO();

        when(productRepository.getProductInPagesAndSearchByName(eq(name), any(Pageable.class)))
                .thenReturn(pagesFromRepository);

        //when
        Page<ProductDTO> result = productService.getAllProductsInPagesByName(pageNumber, size, name, isAsc);

        //then
        assertEquals(expectedPages, result);
    }

    @Test
    void getAllProductsInPagesByNameThrowsExceptionForPageOutOfRange() {
        //given
        int pageNumber = 5;
        int size = 2;
        String name = "";
        boolean isAsc = false;

        Page<Product> pagesFromRepository = createValidPages();
        Page<ProductDTO> expectedPages = createValidPagesDTO();

        when(productRepository.getProductInPagesAndSearchByName(eq(name), any(Pageable.class)))
                .thenReturn(pagesFromRepository);

        //when
        assertThatThrownBy(() -> productService.getAllProductsInPagesByName(pageNumber, size, name, isAsc))
                .isInstanceOf(PageException.class);
    }

    private Page<Product> createValidPages() {
        return new PageImpl<>(List.of(
                Product.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(100.0F)
                        .amount(10)
                        .build(),
                Product.builder()
                        .productId(2L)
                        .name("Name 2")
                        .price(100.0F)
                        .amount(11)
                        .build(),
                Product.builder()
                        .productId(3L)
                        .name("Name 3")
                        .price(120.6F)
                        .amount(10)
                        .build()
        ));
    }

    private Page<ProductDTO> createValidPagesDTO() {
        return new PageImpl<>(List.of(
                ProductDTO.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(100.0F)
                        .amount(10)
                        .build(),
                ProductDTO.builder()
                        .productId(2L)
                        .name("Name 2")
                        .price(100.0F)
                        .amount(11)
                        .build(),
                ProductDTO.builder()
                        .productId(3L)
                        .name("Name 3")
                        .price(120.6F)
                        .amount(10)
                        .build()
        ));
    }
}