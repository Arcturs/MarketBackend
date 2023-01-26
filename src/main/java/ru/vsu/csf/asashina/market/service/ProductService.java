package ru.vsu.csf.asashina.market.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.market.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.market.mapper.ProductMapper;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.entity.Product;
import ru.vsu.csf.asashina.market.repository.ProductRepository;
import ru.vsu.csf.asashina.market.validator.PageValidator;

@Service
@AllArgsConstructor
public class ProductService {

    private static final String PAGE_SORT_BY_PRICE = "price";

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final PageValidator pageValidator;

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
}
