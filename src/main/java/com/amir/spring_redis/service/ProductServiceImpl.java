package com.amir.spring_redis.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.amir.spring_redis.dto.ProductDto;
import com.amir.spring_redis.entity.Product;
import com.amir.spring_redis.repository.ProductRepository;

/**
 * Service implementation class for handling product-related operations
 * with caching support using Redis.
 * 
 * This class implements the ProductService interface and provides CRUD operations
 * for Product entities with Redis caching support. Each method is annotated with
 * appropriate caching annotations to optimize performance.
 * 
 * The caching strategy uses Redis as the caching provider with the following approach:
 * - Create: Caches newly created products
 * - Read: Retrieves from cache if available, otherwise from database
 * - Update: Updates both database and cache
 * - Delete: Removes entry from both database and cache
 */
@Service
public class ProductServiceImpl implements ProductService {

    // Removed the local definition, will use ProductService.PRODUCT_CACHE instead

    /**
     * Repository for product data access.
     * Handles all database operations for Product entities.
     */
    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection
     * @param productRepository The repository for product operations
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates a new product and updates the cache.
     * [...]
     * @CachePut annotation ensures the cache is updated with the new product
     */
    @Override // Good practice to add @Override
    @CachePut(value = ProductService.PRODUCT_CACHE, key = "#result.id") // Use interface constant
    public ProductDto createProduct(ProductDto productDto) {
        var product = new Product();
        product.setName(productDto.name());
        product.setPrice(productDto.price());
        Product savedProduct = productRepository.save(product);
        return new ProductDto(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice());
    }

    /**
     * Retrieves a product by its ID, using cache if available.
     * [...]
     * @Cacheable annotation enables caching of the result
     */
    @Override // Good practice to add @Override
    // Renamed method and parameter to match interface
    @Cacheable(value = ProductService.PRODUCT_CACHE, key = "#id") // Use interface constant and correct key (#id)
    public ProductDto getProductById(Long id) { 
        Product product = productRepository.findById(id) // Use 'id' here
            // Consider using a more specific custom exception like ResourceNotFoundException
            .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + id + " in the database")); 
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    /**
     * Updates an existing product and updates the cache.
     * [...]
     * @CachePut annotation ensures the cache is updated with the modified product
     */
    @Override // Good practice to add @Override
    @CachePut(value = ProductService.PRODUCT_CACHE, key = "#result.id") // Use interface constant
    public ProductDto updateProduct(ProductDto productDto) {
        Long productId = productDto.id(); 
        // Fetch existing product first to ensure it exists before updating
        Product product = productRepository.findById(productId) 
             // Consider using a more specific custom exception like ResourceNotFoundException
            .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId + " to update")); 
        
        // Update properties
        product.setName(productDto.name());
        product.setPrice(productDto.price());
        
        // Save updated product
        Product updatedProduct = productRepository.save(product);
        
        // Return DTO
        return new ProductDto(
            updatedProduct.getId(),
            updatedProduct.getName(),
            updatedProduct.getPrice()
        );
    }

    /**
     * Deletes a product by its ID and removes it from the cache.
     * [...]
     * @CacheEvict annotation ensures the product is removed from the cache
     */
    @Override // Good practice to add @Override
    @CacheEvict(value = ProductService.PRODUCT_CACHE, key = "#id") // Use interface constant and correct key (#id)
    public void deleteProduct(Long id) {
         // Optional: Check if product exists before attempting delete to provide better feedback/error
        if (!productRepository.existsById(id)) {
             // Consider using a more specific custom exception like ResourceNotFoundException
            throw new IllegalArgumentException("Cannot find product with id: " + id + " to delete");
        }
        productRepository.deleteById(id); // Use 'id' here
    }
}