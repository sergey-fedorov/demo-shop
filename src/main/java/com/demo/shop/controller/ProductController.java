package com.demo.shop.controller;


import com.demo.shop.model.Product;
import com.demo.shop.service.ProductService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping(value = { "", "/" })
    public @NotNull Iterable<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable("id") final long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    public ResponseEntity<Product> createCustomer(@RequestBody Product product){
        productService.create(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);

    }
}
