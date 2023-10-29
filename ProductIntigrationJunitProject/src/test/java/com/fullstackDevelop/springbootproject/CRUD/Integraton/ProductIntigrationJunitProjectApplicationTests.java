package com.fullstackDevelop.springbootproject.CRUD.Integraton;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import com.fullstackDevelop.springbootproject.CRUD.Integraton.entity.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIntigrationJunitProjectApplicationTests {

	// get the server port
    @LocalServerPort
    private int port;
    
    // add the JPA repos for test cases rather than actuall appli. mysql db
    @Autowired
    private TestH2Repository h2Repository;
    
    // define the hardcoded base url, you can get from postman
    private String baseUrl = "http://localhost";
    
    // fetch data from a remote API i.e to comsume Rest API
    private static RestTemplate restTemplate;
    
    // before all test cases initialize this rest template
    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }
    
    // before each test case we will set up the base url and concatenate the url like 
    // http://localhost:9191/products/
    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/products");
    }
    
    // test to add the product in h2
    @Test
    public void testAddProduct() {
    	//Arrange
        Product product = new Product("headset", 2, 7999);
        //Act
        Product response = restTemplate.postForObject(baseUrl, product, Product.class);
        //Assert
        assertEquals("headset", response.getName());
        assertEquals(1, h2Repository.findAll().size());
    }
    
    // to get all the products
    @Test
    @Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (4,'AC', 1, 34000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='AC'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProducts() {
        List<Product> products = restTemplate.getForObject(baseUrl, List.class);
        assertEquals(1, products.size());
        assertEquals(1, h2Repository.findAll().size());
    }
    
    // get product by id
    @Test
    @Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (1,'CAR', 1, 334000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindProductById() {
        Product product = restTemplate.getForObject(baseUrl + "/{id}", Product.class, 1);
        assertAll(
                () -> assertNotNull(product),
                () -> assertEquals(1, product.getId()),
                () -> assertEquals("CAR", product.getName())
        );

    }

    // update the product
    @Test
    @Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (2,'shoes', 1, 999)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateProduct(){
        Product product = new Product("shoes", 1, 1999);
        restTemplate.put(baseUrl+"/update/{id}", product, 2);
        Product productFromDB = h2Repository.findById(2).get();
        assertAll(
                () -> assertNotNull(productFromDB),
                () -> assertEquals(1999, productFromDB.getPrice())
        );



    }

    // delete the product
    @Test
    @Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (8,'books', 5, 1499)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteProduct(){
        int recordCount=h2Repository.findAll().size();
        assertEquals(1, recordCount);
        restTemplate.delete(baseUrl+"/delete/{id}", 8);
        assertEquals(0, h2Repository.findAll().size());

    }


}
