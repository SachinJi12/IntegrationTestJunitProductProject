package com.fullstackDevelop.springbootproject.CRUD.Integraton;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstackDevelop.springbootproject.CRUD.Integraton.entity.Product;

// this is to check that the test case are using h2 repos
public interface TestH2Repository extends JpaRepository<Product,Integer> {
}
