package com.ecommerce.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be not exceed 2000 character")
    private String description;
    @NotNull(message = "price is required")
    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity can not be negative")
    private Integer quantity;
    @NotBlank(message = "category is required")
    private String category;
}
