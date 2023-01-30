package ru.vsu.csf.asashina.marketserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.model.request.CategoryCreateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductsListToAttachToCategoryRequest;
import ru.vsu.csf.asashina.marketserver.service.CategoryService;
import ru.vsu.csf.asashina.marketserver.service.ProductService;

import static org.springframework.http.HttpStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final static String CATEGORY_TAG = "Category";

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("")
    @Operation(summary = "Gets all categories in list", tags = CATEGORY_TAG, responses = {
            @ApiResponse(responseCode = "200", description = "List of categories returned", content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class)))
            })
    })
    @SecurityRequirements
    public ResponseEntity<?> getAllCategoriesInList() {
        return ResponseBuilder.build(OK, categoryService.getAllCategories());
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Fetches all products in specifies category", tags = CATEGORY_TAG, responses = {
            @ApiResponse(responseCode = "200", description = "Returns products in pages with category information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryWithProductsDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Category does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    @SecurityRequirements
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
    @Operation(summary = "Creates new category", tags = CATEGORY_TAG, responses = {
            @ApiResponse(responseCode = "201", description = "Returns new category", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "Category with following name already exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> createCategoryFromRequest(@RequestBody @Valid CategoryCreateRequest request) {
        return ResponseBuilder.build(CREATED, categoryService.createCategoryFromCreateRequest(request));
    }

    @PostMapping("/{id}/attach-products")
    @Operation(summary = "Attach products to specified category", tags = CATEGORY_TAG, responses = {
            @ApiResponse(responseCode = "200", description = "Products were successfully attached"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Category/Products do not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> attachProductsToCategory(@PathVariable("id") Long id,
                                                      @RequestBody @Valid ProductsListToAttachToCategoryRequest request) {
        CategoryDTO category = categoryService.getCategoryById(id);
        productService.attachCategoryToProducts(category, request);
        return ResponseBuilder.buildWithoutBodyResponse(OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes category", tags = CATEGORY_TAG, responses = {
            @ApiResponse(responseCode = "204", description = "Category was deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid category id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Category does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> deleteCategoryById(@PathVariable("id") Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseBuilder.buildWithoutBodyResponse(NO_CONTENT);
    }
}
