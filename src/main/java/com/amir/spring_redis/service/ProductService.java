package com.amir.spring_redis.service;

import com.amir.spring_redis.dto.ProductDto;

/**
 * Service interface for managing product operations with Redis caching
 */
public interface ProductService {
    
    /**
     * Constant defining the cache name for product entities
     */
    String PRODUCT_CACHE = "productCache";

    /**
     * Creates a new product
     * @param productDto The product data transfer object containing product information
     * @return The created product DTO
     */
    ProductDto createProduct(ProductDto productDto);

    /**
     * Retrieves a product by its ID
     * @param id The ID of the product to retrieve
     * @return The product DTO if found
     */
    ProductDto getProductById(Long id);

    /**
     * Updates an existing product
     * @param productDto The product DTO containing updated information
     * @return The updated product DTO
     */
    ProductDto updateProduct(ProductDto productDto);

    /**
     * Deletes a product by its ID
     * @param id The ID of the product to delete
     */
    void deleteProduct(Long id);

}
