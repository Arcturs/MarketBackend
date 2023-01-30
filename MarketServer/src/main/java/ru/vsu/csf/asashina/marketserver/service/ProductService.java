package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketserver.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.mapper.ProductMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.ProductDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Product;
import ru.vsu.csf.asashina.marketserver.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductsListToAttachToCategoryRequest;
import ru.vsu.csf.asashina.marketserver.repository.ProductRepository;
import ru.vsu.csf.asashina.marketserver.validator.PageValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProductService {

    private static final String PAGE_SORT_BY_PRICE = "price";

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final PageValidator pageValidator;

    private final CategoryService categoryService;

    public Page<ProductDTO> getAllProductsInPagesByName(Integer pageNumber, Integer size, String name, Boolean isAsc) {
        PageRequest pageRequest = buildPageRequest(pageNumber, size, isAsc);
        Page<Product> pages = productRepository.getProductInPagesAndSearchByName(name, pageRequest);

        pageValidator.checkPageOutOfRange(pages, pageNumber);

        return pages.map(productMapper::toDTOFromEntity);
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer size, Boolean isAsc) {
        return PageRequest.of(pageNumber - 1, size,
                isAsc ? Sort.by(PAGE_SORT_BY_PRICE).ascending() : Sort.by(PAGE_SORT_BY_PRICE).descending());
    }

    public Page<ProductDTO> getAllProductsInPagesByNameWithCategoryId(Long categoryId, Integer pageNumber, Integer size, String name, Boolean isAsc) {
        PageRequest pageRequest = buildPageRequest(pageNumber, size, isAsc);
        Page<Product> pages = productRepository.getProductInPagesAndSearchByNameWithCategory(name, categoryId,
                pageRequest);

        pageValidator.checkPageOutOfRange(pages, pageNumber);

        return pages.map(productMapper::toDTOFromEntity);
    }

    public ProductDTO getProductById(Long id) {
        Product product = findProductById(id);
        return productMapper.toDTOFromEntity(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ObjectNotExistException("Product with following id does not exist")
        );
    }

    @Transactional
    public ProductDTO createProductFromCreateRequest(ProductCreateRequest request) {
        checkIfProductNameExistsByName(request.getName());
        Set<CategoryDTO> categoriesFromRequest = categoryService.getCategoryDTOSetByIds(request.getCategoriesId());
        Product entityFromCreateRequest = productMapper.toEntityFromCreateRequest(request, categoriesFromRequest);
        Product createdProductWithId = productRepository.save(entityFromCreateRequest);
        return productMapper.toDTOFromEntity(createdProductWithId);
    }

    private void checkIfProductNameExistsByName(String name) {
        if (productRepository.existsProductByNameIgnoreCase(name)) {
            throw new ObjectAlreadyExistsException("Product with following name already exists");
        }
    }

    @Transactional
    public ProductDTO updateProductFromUpdateRequest(Long id, ProductUpdateRequest request) {
        Product beforeUpdateEntity = findProductById(id);
        productMapper.updateEntityFromUpdateRequest(request, beforeUpdateEntity);
        Product afterUpdateEntity = productRepository.save(beforeUpdateEntity);
        return productMapper.toDTOFromEntity(afterUpdateEntity);
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

        List<ProductDTO> productDTOS = products.stream()
                .map(productMapper::toDTOFromEntity)
                .toList();
        addCategoryToProducts(productDTOS, category);
        
        List<Product> productsEntitiesWithAddedCategory = productDTOS.stream()
                .map(productMapper::toEntityFromDTO)
                .toList();
        productRepository.saveAll(productsEntitiesWithAddedCategory);
    }

    private void addCategoryToProducts(List<ProductDTO> products, CategoryDTO category) {
        for (ProductDTO product : products) {
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
}
