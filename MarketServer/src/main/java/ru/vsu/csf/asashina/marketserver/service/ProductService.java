package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketserver.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.exception.OrderEmptyException;
import ru.vsu.csf.asashina.marketserver.exception.OutOfStockException;
import ru.vsu.csf.asashina.marketserver.mapper.ProductMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderProductDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.ProductDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.ProductDetailedDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Product;
import ru.vsu.csf.asashina.marketserver.model.request.AddProductToOrderRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductsListToAttachToCategoryRequest;
import ru.vsu.csf.asashina.marketserver.repository.ProductRepository;
import ru.vsu.csf.asashina.marketserver.util.PageUtil;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private static final String PAGE_SORT_BY_PRICE = "price";

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final PageUtil pageUtil;

    private final CategoryService categoryService;

    public Page<ProductDetailedDTO> getAllProductsInPagesByName(Integer pageNumber, Integer size, String name, Boolean isAsc) {
        PageRequest pageRequest = pageUtil.createPageRequest(pageNumber, size, isAsc, PAGE_SORT_BY_PRICE);
        Page<Product> pages = productRepository.getProductInPagesAndSearchByName(name, pageRequest);

        pageUtil.checkPageOutOfRange(pages, pageNumber);

        return pages.map(productMapper::toDetailedDTOFromEntity);
    }

    public Page<ProductDetailedDTO> getAllProductsInPagesByNameWithCategoryId(Long categoryId, Integer pageNumber, Integer size, String name, Boolean isAsc) {
        PageRequest pageRequest = pageUtil.createPageRequest(pageNumber, size, isAsc, PAGE_SORT_BY_PRICE);
        Page<Product> pages = productRepository.getProductInPagesAndSearchByNameWithCategory(name, categoryId,
                pageRequest);

        pageUtil.checkPageOutOfRange(pages, pageNumber);

        return pages.map(productMapper::toDetailedDTOFromEntity);
    }

    public ProductDetailedDTO getProductById(Long id) {
        Product product = findProductById(id);
        return productMapper.toDetailedDTOFromEntity(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ObjectNotExistException("Product with following id does not exist")
        );
    }

    @Transactional
    public ProductDetailedDTO createProductFromCreateRequest(ProductCreateRequest request) {
        checkIfProductNameExistsByName(request.getName());
        Set<CategoryDTO> categoriesFromRequest = categoryService.getCategoryDTOSetByIds(request.getCategoriesId());
        Product entityFromCreateRequest = productMapper.toEntityFromCreateRequest(request, categoriesFromRequest);
        Product createdProductWithId = productRepository.save(entityFromCreateRequest);
        return productMapper.toDetailedDTOFromEntity(createdProductWithId);
    }

    private void checkIfProductNameExistsByName(String name) {
        if (productRepository.existsProductByNameIgnoreCase(name)) {
            throw new ObjectAlreadyExistsException("Product with following name already exists");
        }
    }

    @Transactional
    public ProductDetailedDTO updateProductFromUpdateRequest(Long id, ProductUpdateRequest request) {
        Product beforeUpdateEntity = findProductById(id);
        productMapper.updateEntityFromUpdateRequest(request, beforeUpdateEntity);
        Product afterUpdateEntity = productRepository.save(beforeUpdateEntity);
        return productMapper.toDetailedDTOFromEntity(afterUpdateEntity);
    }

    @Transactional
    public void deleteProductById(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    @Transactional
    public void attachCategoryToProducts(CategoryDTO category, ProductsListToAttachToCategoryRequest request) {
        List<Product> products = productRepository.findAllByProductIdIn(request.getProductsId());
        if (products.isEmpty()) {
            throw new ObjectNotExistException("Products with following ids do not exist");
        }

        List<ProductDetailedDTO> productDetailedDTOS = products.stream()
                .map(productMapper::toDetailedDTOFromEntity)
                .toList();
        addCategoryToProducts(productDetailedDTOS, category);
        
        List<Product> productsEntitiesWithAddedCategory = productDetailedDTOS.stream()
                .map(productMapper::toEntityFromDetailedDTO)
                .toList();
        productRepository.saveAll(productsEntitiesWithAddedCategory);
    }

    private void addCategoryToProducts(List<ProductDetailedDTO> products, CategoryDTO category) {
        for (ProductDetailedDTO product : products) {
            Set<CategoryDTO> categories = product.getCategories();
            if (categories == null) {
                categories = new HashSet<>();
            }
            categories.add(category);
            product.setCategories(categories);
        }
    }

    @Transactional
    public void removeCategoryFromProduct(Long id, Long categoryId) {
        findProductById(id);
        categoryService.getCategoryById(categoryId);
        productRepository.removeCategoryFromProduct(id, categoryId);
    }

    public ProductDTO getProductFromAddToOrderRequest(AddProductToOrderRequest request) {
        Product product = findProductById(request.getProductId());
        checkIfProductAmountLessThanInDatabase(product.getAmount(), request.getAmount());
        return productMapper.toDTOFromEntity(product);
    }

    private void checkIfProductAmountLessThanInDatabase(Integer dbAmount, Integer requestAmount) {
        if (dbAmount < requestAmount) {
            throw new OutOfStockException(MessageFormat.format("You cannot buy more than {0} products", dbAmount));
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void decreaseProductsAmount(Set<OrderProductDTO> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new OrderEmptyException("Order is empty");
        }
        List<Long> ids = orderProducts.stream()
                .map(el -> el.getProduct().getProductId())
                .toList();
        List<Product> products = productRepository.findPessimisticLockAllByProductIdIn(ids);
        Map<Long, Product> idProductMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        List<Product> updatedProducts = new ArrayList<>();
        for (OrderProductDTO orderProduct : orderProducts) {
            Product product = idProductMap.get(orderProduct.getProduct().getProductId());
            if (product.getAmount() < orderProduct.getAmount()) {
                throw new OutOfStockException(MessageFormat.format("You cannot buy more than {0} {1}",
                        product.getAmount(), product.getName()));
            }
            product.setAmount(product.getAmount() - orderProduct.getAmount());
            updatedProducts.add(product);
        }

        productRepository.saveAll(updatedProducts);
    }
}
