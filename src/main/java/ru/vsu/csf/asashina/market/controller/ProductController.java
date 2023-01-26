package ru.vsu.csf.asashina.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.asashina.market.model.ResponseBuilder;
import ru.vsu.csf.asashina.market.model.dto.ProductDTO;
import ru.vsu.csf.asashina.market.service.ProductService;

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
        return ResponseBuilder.build(HttpStatus.OK, productService.getProductById(id));
    }
}
