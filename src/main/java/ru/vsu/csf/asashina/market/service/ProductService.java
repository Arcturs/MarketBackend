package ru.vsu.csf.asashina.market.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.market.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.market.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.market.mapper.ProductMapper;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.entity.Category;
import ru.vsu.csf.asashina.market.model.entity.Product;
import ru.vsu.csf.asashina.market.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.market.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.market.repository.ProductRepository;
import ru.vsu.csf.asashina.market.validator.PageValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private static final String PAGE_SORT_BY_PRICE = "price";

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final PageValidator pageValidator;

    private final CategoryService categoryService;

    public Page<ProductDTO> getAllProductsInPagesByName(Integer pageNumber, Integer size, String name, Boolean isAsc) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size,
                isAsc ? Sort.by(PAGE_SORT_BY_PRICE).ascending() : Sort.by(PAGE_SORT_BY_PRICE).descending());
        Page<Product> pages = productRepository.getProductInPagesAndSearchByName(name, pageRequest);

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
        checkProductNameExistsByName(request.getName());
        List<Category> categoriesFromRequest = categoryService.getCategoryListByIds(request.getCategoriesId());
        Product entityFromCreateRequest = productMapper.toEntityFromCreateRequest(request, categoriesFromRequest);
        Product createdProductWithId = productRepository.save(entityFromCreateRequest);
        return productMapper.toDTOFromEntity(createdProductWithId);
    }

    private void checkProductNameExistsByName(String name) {
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
}
