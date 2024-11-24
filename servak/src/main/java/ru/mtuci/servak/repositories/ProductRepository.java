package ru.mtuci.servak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.servak.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product getProductById(Long id);
}
