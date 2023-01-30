package ru.vsu.csf.asashina.marketserver.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.vsu.csf.asashina.marketserver.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.marketserver.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.marketserver.service.ProductService;

import static org.springframework.http.HttpStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final static String PRODUCT_TAG = "Product";

    private final ProductService productService;

    @GetMapping("")
    @Operation(summary = "Fetches all products in pages", tags = PRODUCT_TAG, responses = {
            @ApiResponse(responseCode = "200", description = "Returns products in pages", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductsInPagesDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    @SecurityRequirements
    public ResponseEntity<?> getAllProductsInPages(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                   @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
                                                   @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                   @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc) {
        Page<ProductDTO> productPages = productService.getAllProductsInPagesByName(pageNumber, size, name, isAsc);
        return ResponseBuilder.build(OK, buildProductsInPagesResponse(productPages, pageNumber, size));
    }

    private ProductsInPagesDTO buildProductsInPagesResponse(Page<ProductDTO> products, Integer pageNumber, Integer size) {
        return new ProductsInPagesDTO(products.getContent(),
                new PagingInfoDTO(pageNumber, size, products.getTotalPages()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Gets product's details", tags = PRODUCT_TAG, responses = {
            @ApiResponse(responseCode = "200", description = "Returns product", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid product's id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    @SecurityRequirements
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        return ResponseBuilder.build(OK, productService.getProductById(id));
    }

    @PostMapping("")
    @Operation(summary = "Creates new product", tags = PRODUCT_TAG, responses = {
            @ApiResponse(responseCode = "201", description = "Returns new product", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "Product with following name already exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        return ResponseBuilder.build(CREATED, productService.createProductFromCreateRequest(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates product's details", tags = PRODUCT_TAG, responses = {
            @ApiResponse(responseCode = "200", description = "Returns updated product", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> updateProductById(@PathVariable("id") Long id,
                                               @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseBuilder.build(OK, productService.updateProductFromUpdateRequest(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes product", tags = PRODUCT_TAG, responses = {
            @ApiResponse(responseCode = "204", description = "Product was deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid product's id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> deleteProductById(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return ResponseBuilder.buildWithoutBodyResponse(NO_CONTENT);
    }

    @DeleteMapping("/{id}/remove-category/{categoryId}")
    @Operation(summary = "Removes category from product", tags = PRODUCT_TAG, responses = {
            @ApiResponse(responseCode = "204", description = "Category was removed"),
            @ApiResponse(responseCode = "400", description = "Invalid product/category's id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product/Category does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> removeCategoryFromProduct(@PathVariable("id") Long id,
                                                       @PathVariable("categoryId") Long categoryId) {
        productService.removeCategoryFromProduct(id, categoryId);
        return ResponseBuilder.buildWithoutBodyResponse(NO_CONTENT);
    }
}
