package ru.vsu.csf.asashina.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.csf.asashina.market.service.ProductService;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
}
