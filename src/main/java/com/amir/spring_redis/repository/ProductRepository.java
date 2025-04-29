package com.amir.spring_redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amir.spring_redis.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {



}
