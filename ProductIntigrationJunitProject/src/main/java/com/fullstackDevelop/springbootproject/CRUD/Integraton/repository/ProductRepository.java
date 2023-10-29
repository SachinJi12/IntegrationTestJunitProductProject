package com.fullstackDevelop.springbootproject.CRUD.Integraton.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstackDevelop.springbootproject.CRUD.Integraton.entity.Product;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Product findByName(String name);
}
