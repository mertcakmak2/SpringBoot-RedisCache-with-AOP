package com.example.aoprediscache;

import com.example.aoprediscache.Model.Product;
import com.example.aoprediscache.Service.ProductService;
import com.example.aoprediscache.Service.ProductServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Arrays;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AoprediscacheApplication {

    public static void main(String[] args) {

        Product product1 = Product.builder().id(1).name("book").price(20).build();
        Product product2 = Product.builder().id(2).name("toy").price(35).build();
        Product product3 = Product.builder().id(3).name("mobile phone").price(712).build();
        Product product4 = Product.builder().id(4).name("laptop").price(8172).build();

        ProductServiceImpl.productList.addAll(Arrays.asList(product1, product2, product3, product4));
        SpringApplication.run(AoprediscacheApplication.class, args);
    }

}
