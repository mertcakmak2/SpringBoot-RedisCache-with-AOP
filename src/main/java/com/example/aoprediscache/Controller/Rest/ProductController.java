package com.example.aoprediscache.Controller.Rest;

import com.example.aoprediscache.Model.Product;
import com.example.aoprediscache.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public List<Product> findAllProducts() throws InterruptedException {
        return productService.findAllProduct();
    }

    @PostMapping("")
    public Product saveProduct(@RequestBody Product product) throws InterruptedException {
        return productService.saveProduct(product);
    }

    @GetMapping("/{id}")
    public Product findProductById(@PathVariable int id) throws InterruptedException {
        return productService.findProductById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteProductById(@PathVariable int id) throws InterruptedException {
        productService.deleteProductById(id);
        return "Deleted by id: "+id;
    }

}
