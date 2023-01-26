package ru.vsu.csf.asashina.market.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.asashina.market.model.ResponseBuilder;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.model.request.ProductCreateRequest;
import ru.vsu.csf.asashina.market.model.request.ProductUpdateRequest;
import ru.vsu.csf.asashina.market.service.ProductService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllProductsInPages(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                   @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
                                                   @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                   @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc) {
        Page<ProductDTO> productPages = productService.getAllProductsInPagesByName(pageNumber, size, name, isAsc);
        return ResponseBuilder.build(productPages, pageNumber, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        return ResponseBuilder.build(OK, productService.getProductById(id));
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        return ResponseBuilder.build(CREATED, productService.createProductFromCreateRequest(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductById(@PathVariable("id") Long id,
                                               @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseBuilder.build(OK, productService.updateProductFromUpdateRequest(id, request));
    }
}
