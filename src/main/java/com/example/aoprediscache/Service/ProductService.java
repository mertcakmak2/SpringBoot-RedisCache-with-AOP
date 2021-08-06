package com.example.aoprediscache.Service;

import com.example.aoprediscache.Model.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAllProduct() throws InterruptedException;
    Product findProductById(int id) throws InterruptedException;
    Product saveProduct(Product product);
    void deleteProductById(int id);

}
