package ru.vsu.csf.asashina.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.csf.asashina.market.model.ResponseBuilder;
import ru.vsu.csf.asashina.market.service.CategoryService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<?> getAllCategoriesInList() {
        return ResponseBuilder.build(OK, categoryService.getAllCategories());
    }
}
