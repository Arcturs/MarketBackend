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
import ru.vsu.csf.asashina.market.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.market.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.market.exception.PageException;
import ru.vsu.csf.asashina.market.mapper.ProductMapper;
import ru.vsu.csf.asashina.market.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.model.entity.Product;
import ru.vsu.csf.asashina.market.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.market.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.market.repository.ProductRepository;
import ru.vsu.csf.asashina.market.validator.PageValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Spy
    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Spy
    private PageValidator pageValidator;

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

    private Page<Product> createValidPagesWithCategory() {
        return new PageImpl<>(List.of(
                Product.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(100.0F)
                        .amount(10)
                        .categories(Set.of(new Category(1L, "Name 1")))
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

    private Page<ProductDTO> createValidPagesDTOWithCategory() {
        return new PageImpl<>(List.of(
                ProductDTO.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(100.0F)
                        .amount(10)
                        .categories(Set.of(new CategoryDTO(1L, "Name 1")))
                        .build()
        ));
    }

    private Product createValidProduct() {
        return Product.builder()
                .productId(1L)
                .name("Name 1")
                .price(100.0F)
                .amount(10)
                .build();
    }

    private ProductDTO createValidProductDTO() {
        return ProductDTO.builder()
                .productId(1L)
                .name("Name 1")
                .price(100.0F)
                .amount(10)
                .build();
    }

    private ProductCreateRequest createValidProductCreateRequest() {
        return ProductCreateRequest.builder()
                .name("Name 1")
                .price(100.0F)
                .amount(10)
                .build();
    }

    private ProductUpdateRequest createValidProductUpdateRequest() {
        return ProductUpdateRequest.builder()
                .amount(12)
                .description("Cool")
                .build();
    }

    private Set<Category> createValidCategorySet() {
        return Set.of(new Category(1L, "Name 1"));
    }

    private Set<CategoryDTO> createValidCategoryDTOSet() {
        return Set.of(new CategoryDTO(1L, "Name 1"));
    }

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

        when(productRepository.getProductInPagesAndSearchByName(eq(name), any(Pageable.class)))
                .thenReturn(pagesFromRepository);

        //when, then
        assertThatThrownBy(() -> productService.getAllProductsInPagesByName(pageNumber, size, name, isAsc))
                .isInstanceOf(PageException.class);
    }

    @Test
    void getAllProductsInPagesByNameWithCategoryIdSuccess() {
        //given
        long categoryId = 1L;
        int pageNumber = 1;
        int size = 2;
        String name = "";
        boolean isAsc = true;

        Page<Product> pagesFromRepository = createValidPagesWithCategory();
        Page<ProductDTO> expectedPages = createValidPagesDTOWithCategory();

        when(productRepository.getProductInPagesAndSearchByNameWithCategory(eq(name), eq(categoryId),
                any(Pageable.class))).thenReturn(pagesFromRepository);

        //when
        Page<ProductDTO> result = productService.getAllProductsInPagesByNameWithCategoryId(categoryId, pageNumber, size,
                name, isAsc);

        //then
        assertEquals(expectedPages, result);
    }

    @Test
    void getAllProductsInPagesByNameWithCategoryIdThrowsExceptionForPageOutOfRange() {
        //given
        long categoryId = 1L;
        int pageNumber = 5;
        int size = 2;
        String name = "";
        boolean isAsc = false;

        Page<Product> pagesFromRepository = createValidPagesWithCategory();

        when(productRepository.getProductInPagesAndSearchByNameWithCategory(eq(name), eq(categoryId),
                any(Pageable.class))).thenReturn(pagesFromRepository);

        //when, then
        assertThatThrownBy(() -> productService.getAllProductsInPagesByNameWithCategoryId(categoryId, pageNumber, size,
                name, isAsc)).isInstanceOf(PageException.class);
    }

    @Test
    void getProductByIdSuccess() {
        //given
        long id = 1L;

        Product productFromRepository = createValidProduct();
        ProductDTO expectedProduct = createValidProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(productFromRepository));

        //when
        ProductDTO result = productService.getProductById(id);

        //then
        assertEquals(expectedProduct, result);
    }

    @Test
    void getProductByIdThrowsExceptionForNonExistingProduct() {
        //given
        long id = 2L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void createProductFromCreateRequestSuccess() {
        //given
        ProductCreateRequest request = createValidProductCreateRequest();

        Product withoutIdProduct = Product.builder()
                .productId(null)
                .name("Name 1")
                .price(100.0F)
                .amount(10)
                .build();
        Product productFromRepository = createValidProduct();
        ProductDTO expectedProduct = createValidProductDTO();

        when(productRepository.existsProductByNameIgnoreCase(request.getName())).thenReturn(false);
        when(categoryService.getCategoryDTOSetByIds(null)).thenReturn(null);
        when(productRepository.save(withoutIdProduct)).thenReturn(productFromRepository);

        //when
        ProductDTO result = productService.createProductFromCreateRequest(request);

        //then
        assertEquals(expectedProduct, result);
    }

    @Test
    void createProductFromCreateRequestSuccessWithCategory() {
        //given
        ProductCreateRequest request = createValidProductCreateRequest();
        request.setCategoriesId(List.of(1L));

        Set<CategoryDTO> categoriesFromCategoryService = createValidCategoryDTOSet();
        Set<Category> entitiesCategory = createValidCategorySet();
        Product withoutIdProduct = Product.builder()
                .productId(null)
                .name("Name 1")
                .price(100.0F)
                .amount(10)
                .categories(entitiesCategory)
                .build();
        Product productFromRepository = createValidProduct();
        productFromRepository.setCategories(entitiesCategory);
        ProductDTO expectedProduct = createValidProductDTO();
        expectedProduct.setCategories(createValidCategoryDTOSet());

        when(productRepository.existsProductByNameIgnoreCase(request.getName())).thenReturn(false);
        when(categoryService.getCategoryDTOSetByIds(request.getCategoriesId()))
                .thenReturn(categoriesFromCategoryService);
        when(productRepository.save(withoutIdProduct)).thenReturn(productFromRepository);

        //when
        ProductDTO result = productService.createProductFromCreateRequest(request);

        //then
        assertEquals(expectedProduct, result);
    }

    @Test
    void createProductFromCreateRequestThrowsExceptionWhenProductWithNameAlreadyExists() {
        //given
        ProductCreateRequest request = createValidProductCreateRequest();

        when(productRepository.existsProductByNameIgnoreCase(request.getName())).thenReturn(true);

        //when, then
        assertThatThrownBy(() -> productService.createProductFromCreateRequest(request))
                .isInstanceOf(ObjectAlreadyExistsException.class);
    }

    @Test
    void updateProductFromUpdateRequestSuccess() {
        //given
        Long id = 1L;
        ProductUpdateRequest request = createValidProductUpdateRequest();

        Product beforeUpdateEntityFromRepository = createValidProduct();
        Product afterUpdateEntity = Product.builder()
                .productId(1L)
                .name("Name 1")
                .description("Cool")
                .price(100.0F)
                .amount(12)
                .build();
        ProductDTO expectedProduct = ProductDTO.builder()
                .productId(1L)
                .name("Name 1")
                .description("Cool")
                .price(100.0F)
                .amount(12)
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(beforeUpdateEntityFromRepository));
        when(productRepository.save(afterUpdateEntity)).thenReturn(afterUpdateEntity);

        //when
        ProductDTO result = productService.updateProductFromUpdateRequest(id, request);

        //then
        assertEquals(expectedProduct, result);
    }

    @Test
    void updateProductFromUpdateRequestThrowsExceptionForNotExistingProduct() {
        //given
        Long id = 2L;
        ProductUpdateRequest request = createValidProductUpdateRequest();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> productService.updateProductFromUpdateRequest(id, request))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void deleteProductByIdSuccess() {
        //given
        long id = 1L;

        Product productFromRepository = createValidProduct();

        when(productRepository.findById(id)).thenReturn(Optional.of(productFromRepository));

        //when, then
        assertDoesNotThrow(() -> productService.deleteProductById(id));
    }

    @Test
    void deleteProductByIdThrowsExceptionForNotExistingProduct() {
        //given
        long id = 2L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> productService.deleteProductById(id)).isInstanceOf(ObjectNotExistException.class);
    }
}