package com.ecommerce.product.specificatio;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null || name.isBlank() ? null
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"

                );


    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category == null || category.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")),
                        category.toLowerCase()
                );
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(
            BigDecimal minPrice) {

        return (root, query, criteriaBuilder) ->
                minPrice == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    public static Specification<Product> priceLessThanOrEqualTo(
            BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) ->
                maxPrice == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }
}
