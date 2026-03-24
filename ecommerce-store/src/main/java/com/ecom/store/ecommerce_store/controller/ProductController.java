package com.ecom.store.ecommerce_store.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.ProductDto;
import com.ecom.store.ecommerce_store.model.Product;
import com.ecom.store.ecommerce_store.model.ProductImage;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.service.ProductService;
import com.ecom.store.ecommerce_store.service.UserService;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private ProductService productService;
    private UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products != null) {
            return ResponseEntity.status(HttpStatus.OK).body(products);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody ProductDto product) {
        if (product.getName() == null || product.getPrice() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product name and price are required");
        }
        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setCategory(product.getCategory());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found. Please Login before Creating a product");
        }
        newProduct.setSellerId(user.getId());
        if (product.getImages() != null) {
            List<ProductImage> images = product.getImages().stream().map(url -> {
                ProductImage image = new ProductImage();
                image.setImageUrl(url.toString());
                image.setProduct(newProduct);
                return image;

            }).toList();
            newProduct.setImages(images);
        }
        newProduct.setCreatedAt(LocalDateTime.now());
        productService.createProduct(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
