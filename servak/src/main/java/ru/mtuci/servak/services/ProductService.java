package ru.mtuci.servak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.Product;
import ru.mtuci.servak.repositories.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if(product == null) {
            throw new RuntimeException("Продукт с id: " + id + " не найден");
        }

        return product;
    }
}
