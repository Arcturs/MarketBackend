package ru.vsu.csf.asashina.marketservice.service;

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
import ru.vsu.csf.asashina.marketservice.exception.*;
import ru.vsu.csf.asashina.marketservice.mapper.ProductMapper;
import ru.vsu.csf.asashina.marketservice.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.OrderProductDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.ProductDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.ProductDetailedDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.Category;
import ru.vsu.csf.asashina.marketservice.model.entity.Product;
import ru.vsu.csf.asashina.marketservice.model.request.AddProductToOrderRequest;
import ru.vsu.csf.asashina.marketservice.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.marketservice.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.marketservice.model.request.ProductsListToAttachToCategoryRequest;
import ru.vsu.csf.asashina.marketservice.repository.ProductRepository;
import ru.vsu.csf.asashina.marketservice.util.PageUtil;

import java.math.BigDecimal;
import java.util.Collections;
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
    private PageUtil pageUtil;

    private Page<Product> createValidPages() {
        return new PageImpl<>(List.of(
                Product.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(new BigDecimal("100.00"))
                        .amount(10)
                        .build(),
                Product.builder()
                        .productId(2L)
                        .name("Name 2")
                        .price(new BigDecimal("100.00"))
                        .amount(11)
                        .build(),
                Product.builder()
                        .productId(3L)
                        .name("Name 3")
                        .price(new BigDecimal("120.60"))
                        .amount(10)
                        .build()
        ));
    }

    private Page<Product> createValidPagesWithCategory() {
        return new PageImpl<>(List.of(
                Product.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(new BigDecimal("100.00"))
                        .amount(10)
                        .categories(Set.of(new Category(1L, "Name 1")))
                        .build()
        ));
    }

    private Page<ProductDetailedDTO> createValidPagesDTO() {
        return new PageImpl<>(List.of(
                ProductDetailedDTO.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(new BigDecimal("100.00"))
                        .amount(10)
                        .build(),
                ProductDetailedDTO.builder()
                        .productId(2L)
                        .name("Name 2")
                        .price(new BigDecimal("100.00"))
                        .amount(11)
                        .build(),
                ProductDetailedDTO.builder()
                        .productId(3L)
                        .name("Name 3")
                        .price(new BigDecimal("120.60"))
                        .amount(10)
                        .build()
        ));
    }

    private Page<ProductDetailedDTO> createValidPagesDTOWithCategory() {
        return new PageImpl<>(List.of(
                ProductDetailedDTO.builder()
                        .productId(1L)
                        .name("Name 1")
                        .price(new BigDecimal("100.00"))
                        .amount(10)
                        .categories(Set.of(new CategoryDTO(1L, "Name 1")))
                        .build()
        ));
    }

    private Product createValidProduct() {
        return Product.builder()
                .productId(1L)
                .name("Name 1")
                .price(new BigDecimal("100.00"))
                .amount(10)
                .build();
    }

    private ProductDTO createValidProductDTO() {
        return ProductDTO.builder()
                .productId(1L)
                .name("Name 1")
                .price(new BigDecimal("100.00"))
                .build();
    }

    private ProductDetailedDTO createValidDetailedProductDTO() {
        return ProductDetailedDTO.builder()
                .productId(1L)
                .name("Name 1")
                .price(new BigDecimal("100.00"))
                .amount(10)
                .build();
    }

    private ProductCreateRequest createValidProductCreateRequest() {
        return ProductCreateRequest.builder()
                .name("Name 1")
                .price(new BigDecimal("100.00"))
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
        Page<ProductDetailedDTO> expectedPages = createValidPagesDTO();

        when(productRepository.getProductInPagesAndSearchByName(eq(name), any(Pageable.class)))
                .thenReturn(pagesFromRepository);

        //when
        Page<ProductDetailedDTO> result = productService.getAllProductsInPagesByName(pageNumber, size, name, isAsc);

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

        when(productRepository.getProductInPagesAndSearchByName(eq(name), any(Pageable.class)))
                .thenReturn(Page.empty());

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
        Page<ProductDetailedDTO> expectedPages = createValidPagesDTOWithCategory();

        when(productRepository.getProductInPagesAndSearchByNameWithCategory(eq(name), eq(categoryId),
                any(Pageable.class))).thenReturn(pagesFromRepository);

        //when
        Page<ProductDetailedDTO> result = productService.getAllProductsInPagesByNameWithCategoryId(categoryId, pageNumber, size,
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

        when(productRepository.getProductInPagesAndSearchByNameWithCategory(eq(name), eq(categoryId),
                any(Pageable.class))).thenReturn(Page.empty());

        //when, then
        assertThatThrownBy(() -> productService.getAllProductsInPagesByNameWithCategoryId(categoryId, pageNumber, size,
                name, isAsc)).isInstanceOf(PageException.class);
    }

    @Test
    void getProductByIdSuccess() {
        //given
        long id = 1L;

        Product productFromRepository = createValidProduct();
        ProductDetailedDTO expectedProduct = createValidDetailedProductDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(productFromRepository));

        //when
        ProductDetailedDTO result = productService.getProductById(id);

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
                .price(new BigDecimal("100.00"))
                .amount(10)
                .build();
        Product productFromRepository = createValidProduct();
        ProductDetailedDTO expectedProduct = createValidDetailedProductDTO();

        when(productRepository.existsProductByNameIgnoreCase(request.getName())).thenReturn(false);
        when(categoryService.getCategoryDTOSetByIds(null)).thenReturn(null);
        when(productRepository.save(withoutIdProduct)).thenReturn(productFromRepository);

        //when
        ProductDetailedDTO result = productService.createProductFromCreateRequest(request);

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
                .price(new BigDecimal("100.00"))
                .amount(10)
                .categories(entitiesCategory)
                .build();
        Product productFromRepository = createValidProduct();
        productFromRepository.setCategories(entitiesCategory);
        ProductDetailedDTO expectedProduct = createValidDetailedProductDTO();
        expectedProduct.setCategories(createValidCategoryDTOSet());

        when(productRepository.existsProductByNameIgnoreCase(request.getName())).thenReturn(false);
        when(categoryService.getCategoryDTOSetByIds(request.getCategoriesId()))
                .thenReturn(categoriesFromCategoryService);
        when(productRepository.save(withoutIdProduct)).thenReturn(productFromRepository);

        //when
        ProductDetailedDTO result = productService.createProductFromCreateRequest(request);

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
                .price(new BigDecimal("100.00"))
                .amount(12)
                .build();
        ProductDetailedDTO expectedProduct = ProductDetailedDTO.builder()
                .productId(1L)
                .name("Name 1")
                .description("Cool")
                .price(new BigDecimal("100.00"))
                .amount(12)
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(beforeUpdateEntityFromRepository));
        when(productRepository.save(afterUpdateEntity)).thenReturn(afterUpdateEntity);

        //when
        ProductDetailedDTO result = productService.updateProductFromUpdateRequest(id, request);

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

    @Test
    void attachCategoryToProductsSuccess() {
        //given
        CategoryDTO category = new CategoryDTO(1L, "Name 1");
        ProductsListToAttachToCategoryRequest request = new ProductsListToAttachToCategoryRequest(List.of(2L));

        List<Product> foundProductListFromRepository = List.of(Product.builder()
                .productId(2L)
                .name("Name 2")
                .price(new BigDecimal("100.00"))
                .amount(11)
                .build());

        when(productRepository.findAllByProductIdIn(request.getProductsId())).thenReturn(foundProductListFromRepository);

        //when, then
        assertDoesNotThrow(() -> productService.attachCategoryToProducts(category, request));
    }

    @Test
    void attachCategoryToProductsThrowsExceptionWhenPassedNonExistingProductIds() {
        //given
        CategoryDTO category = new CategoryDTO(1L, "Name 1");
        ProductsListToAttachToCategoryRequest request = new ProductsListToAttachToCategoryRequest(List.of(4L));

        when(productRepository.findAllByProductIdIn(request.getProductsId())).thenReturn(Collections.emptyList());

        //when, then
        assertThatThrownBy(() -> productService.attachCategoryToProducts(category, request))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void removeCategoryFromProductSuccess() {
        //given
        long id = 1L;
        long categoryId = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.of(createValidProduct()));
        when(categoryService.getCategoryById(categoryId)).thenReturn(new CategoryDTO());

        //when, then
        assertDoesNotThrow(() -> productService.removeCategoryFromProduct(id, categoryId));
    }

    @Test
    void removeCategoryFromProductThrowsExceptionForNonExistingCategory() {
        //given
        long id = 1L;
        long categoryId = 2L;

        when(productRepository.findById(id)).thenReturn(Optional.of(createValidProduct()));
        when(categoryService.getCategoryById(categoryId)).thenThrow(ObjectNotExistException.class);

        //when, then
        assertThatThrownBy(() -> productService.removeCategoryFromProduct(id, categoryId))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void removeCategoryFromProductThrowsExceptionForNonExistingProduct() {
        //given
        long id = 2L;
        long categoryId = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> productService.removeCategoryFromProduct(id, categoryId))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void getProductFromAddToOrderRequestSuccess() {
        //given
        AddProductToOrderRequest request = new AddProductToOrderRequest(1L, 2);

        Product productFromRepo = createValidProduct();
        ProductDTO expectedProduct = createValidProductDTO();

        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(productFromRepo));

        //when
        ProductDTO result = productService.getProductFromAddToOrderRequest(request);

        //then
        assertEquals(expectedProduct, result);
    }

    @Test
    void getProductFromAddToOrderRequestThrowsExceptionForNonExistingProduct() {
        //given
        AddProductToOrderRequest request = new AddProductToOrderRequest(10L, 2);

        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> productService.getProductFromAddToOrderRequest(request))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void getProductFromAddToOrderRequestThrowsExceptionForWrongAmount() {
        //given
        AddProductToOrderRequest request = new AddProductToOrderRequest(1L, 20);

        Product productFromRepo = createValidProduct();

        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(productFromRepo));

        //when, then
        assertThatThrownBy(() -> productService.getProductFromAddToOrderRequest(request))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void decreaseProductsAmountSuccess() {
        //given
        Set<OrderProductDTO> orderProducts = Set.of(
                new OrderProductDTO(1, BigDecimal.valueOf(100.00), createValidProductDTO())
        );

        when(productRepository.findPessimisticLockAllByProductIdIn(List.of(1L))).thenReturn(List.of(createValidProduct()));

        //when, then
        assertDoesNotThrow(() -> productService.decreaseProductsAmount(orderProducts));
    }

    @Test
    void decreaseProductsAmountThrowsExceptionWheOrderProductAreEmpty() {
        //given
        Set<OrderProductDTO> orderProducts = Collections.EMPTY_SET;

        //when, then
        assertThatThrownBy(() -> productService.decreaseProductsAmount(orderProducts))
                .isInstanceOf(OrderEmptyException.class);
    }

    @Test
    void decreaseProductsAmountThrowsExceptionWhenAmountLargerThanInDB() {
        //given
        Set<OrderProductDTO> orderProducts = Set.of(
                new OrderProductDTO(100, BigDecimal.valueOf(100.00), createValidProductDTO())
        );

        when(productRepository.findPessimisticLockAllByProductIdIn(List.of(1L))).thenReturn(List.of(createValidProduct()));

        //when, then
        assertThatThrownBy(() -> productService.decreaseProductsAmount(orderProducts))
                .isInstanceOf(OutOfStockException.class);
    }
}