package com.study.payment.repository;

import com.study.payment.entity.Product;
import com.study.payment.entity.PurchaseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, Long> {
    @Query("SELECT p.product AS product, SUM(p.quantity) AS totalQuantity " +
            "FROM PurchaseProduct p " +
            "GROUP BY p.product " +
            "ORDER BY SUM(p.quantity) DESC")
    List<Product> findTop5ProductList();

    int countByProduct(Product product);
}
