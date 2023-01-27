package ru.vsu.csf.asashina.market.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.asashina.market.model.ResponseBuilder;
import ru.vsu.csf.asashina.market.model.dto.CategoryDTO;
import ru.vsu.csf.asashina.market.model.dto.CategoryWithProductsDTO;
import ru.vsu.csf.asashina.market.model.dto.PagingInfoDTO;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.request.CategoryCreateRequest;
import ru.vsu.csf.asashina.market.service.CategoryService;
import ru.vsu.csf.asashina.market.service.ProductService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllCategoriesInList() {
        return ResponseBuilder.build(OK, categoryService.getAllCategories());
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<?> getAllProductsInCategoryInPagesById(@PathVariable("id") Long id,
                                                          @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                          @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
                                                          @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                          @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc) {
        CategoryDTO category = categoryService.getCategoryById(id);
        Page<ProductDTO> products =
                productService.getAllProductsInPagesByNameWithCategoryId(id, pageNumber, size, name, isAsc);
        return ResponseBuilder.build(OK, buildCategoryWithProductsResponse(category, products, pageNumber, size));
    }

    private CategoryWithProductsDTO buildCategoryWithProductsResponse(CategoryDTO category, Page<ProductDTO> products,
                                                                      int pageNumber, int size) {
        return CategoryWithProductsDTO.builder()
                .category(category)
                .products(products.getContent())
                .paging(new PagingInfoDTO(pageNumber, size, products.getTotalPages()))
                .build();
    }

    @PostMapping("")
    public ResponseEntity<?> createCategoryFromRequest(@RequestBody @Valid CategoryCreateRequest request) {
        return ResponseBuilder.build(CREATED, categoryService.createCategoryFromCreateRequest(request));
    }
}
