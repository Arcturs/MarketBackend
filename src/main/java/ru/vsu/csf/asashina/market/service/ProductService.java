package ru.vsu.csf.asashina.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.market.repository.ProductRepository;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


}
