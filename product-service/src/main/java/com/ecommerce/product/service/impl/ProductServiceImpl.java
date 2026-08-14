package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.specificatio.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .build();
        Product savedProduct = productRepository.save(product);
        return ProductResponse.builder()
                .name(savedProduct.getName())
                .category(savedProduct.getCategory())
                .description(savedProduct.getDescription())
                .id(savedProduct.getId())
                .price(savedProduct.getPrice())
                .quantity(savedProduct.getQuantity())
                .build();
    }

    @Override
    public ProductResponse getProductUsingId(Long id) {

        Product product = productRepository.findById(id).
                orElseThrow(() -> new ProductNotFoundException("Product not found with :" + id));

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .build();
    }

    @Override
    public Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction,
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> specification =
                Specification.allOf(
                        ProductSpecification.hasName(name),
                        ProductSpecification.hasCategory(category),
                        ProductSpecification.priceGreaterThanOrEqualTo(minPrice),
                        ProductSpecification.priceLessThanOrEqualTo(maxPrice)
                );

        return productRepository.findAll(specification, pageable)
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .category(product.getCategory())
                        .build());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("product not found with :" + id));
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setPrice(product.getPrice());
        product.setQuantity(request.getQuantity());

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.builder()
                .id(updatedProduct.getId())
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .category(updatedProduct.getCategory())
                .price(updatedProduct.getPrice())
                .quantity(updatedProduct.getQuantity())
                .build();
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with :" + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> searchProduct(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream().map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .category(product.getCategory())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .build()).toList();

    }

    @Override
    public List<ProductResponse> filterByCategory(String category) {

        return productRepository.findByCategoryIgnoreCase(category)
                .stream().map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .category(product.getCategory())
                        .description(product.getDescription())
                        .quantity(product.getQuantity())
                        .price(product.getPrice())
                        .build()).toList();
    }

    @Override
    public List<ProductResponse> getProductByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream().map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .category(product.getCategory())
                        .description(product.getDescription())
                        .quantity(product.getQuantity())
                        .price(product.getPrice())
                        .build()
                ).toList();
    }


}
