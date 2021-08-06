package com.example.aoprediscache.Service;

import com.example.aoprediscache.Aop.RedisCacheable;
import com.example.aoprediscache.Model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    //Filled in application.java class
    public static List<Product> productList = new ArrayList<>();
    private final RedisTemplate redisTemplate;

    @Override
    public List<Product> findAllProduct() throws InterruptedException {
        Thread.sleep(1000L);
        return productList;
    }

    @Override
    @RedisCacheable(key = "'products_'.concat(#id)")
    public Product findProductById(int id) throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println("Fetch to list. id: "+id);
        List<Product> products = productList.stream().filter(p -> p.getId() == id).collect(Collectors.toList());
        if(!products.isEmpty()) return products.get(0);
        else return null;
    }

    //Cached with Aop @After annotation
    @Override
    public Product saveProduct(Product product) {
        productList.add(product);
        return product;
    }

    //Delete by id in cache with Aop @After annotation
    @Override
    public void deleteProductById(int id) {
       productList = productList.stream().filter(p -> p.getId() != id).collect(Collectors.toList());
    }
}
