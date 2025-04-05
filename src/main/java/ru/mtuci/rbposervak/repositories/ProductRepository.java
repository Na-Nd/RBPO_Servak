package ru.mtuci.rbposervak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbposervak.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product getProductsById(Long id);
    Product findProductById(Long id);
}
