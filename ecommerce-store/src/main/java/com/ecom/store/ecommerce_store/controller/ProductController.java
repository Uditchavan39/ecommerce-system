package com.ecom.store.ecommerce_store.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.dto.ProductCreationDto;
import com.ecom.store.ecommerce_store.dto.ProductResponse;
import com.ecom.store.ecommerce_store.model.Product;
import com.ecom.store.ecommerce_store.model.ProductImage;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.service.ProductService;
import com.ecom.store.ecommerce_store.service.UserService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ProductService productService;
    private UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products != null) {
            List<ProductResponse> productResponses = products.stream().map(product -> {
                ProductResponse response = new ProductResponse.Builder(product.getId(), product.getName(),
                        product.getPrice(),
                        product.getSeller().getEmail(), product.getQuantity())
                        .setDescription(product.getDescription())
                        .setCategory(product.getCategory())
                        .setImages(product.getImages().stream().map(ProductImage::getImageUrl).toList()).build();
                return response;
            }).toList();
            return ResponseEntity.status(HttpStatus.OK).body(productResponses);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreationDto product) {
        if (product.getName() == null || product.getPrice() == null || product.getQuantity() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Product name,price and Inventory quantity are required");
        }
        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setCategory(product.getCategory());
        newProduct.setQuantity(product.getQuantity());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found. Please Login before Creating a product");
        }
        newProduct.setSeller(user);
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

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductCreationDto product) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        boolean isAdminOrOwner = getIsAdminOrOwner(existingProduct);
        if (!isAdminOrOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this product");
        }
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setUpdatedAt(LocalDateTime.now());
        if (product.getImages() != null) {
            List<ProductImage> images = product.getImages().stream().map(url -> {
                ProductImage image = new ProductImage();
                image.setImageUrl(url.toString());
                image.setProduct(existingProduct);
                return image;
            }).toList();
            existingProduct.setImages(images);
        }
        productService.updateProduct(id, existingProduct);
        ProductResponse response = new ProductResponse.Builder(existingProduct.getId(), existingProduct.getName(),
                existingProduct.getPrice(), existingProduct.getSeller().getEmail(), existingProduct.getQuantity())
                .setDescription(existingProduct.getDescription())
                .setCategory(existingProduct.getCategory())
                .setImages(existingProduct.getImages().stream().map(ProductImage::getImageUrl).toList()).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        boolean isAdminOrOwner = getIsAdminOrOwner(existingProduct);
        if (!isAdminOrOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this product");
        }
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product Successfully Deleted.");
    }

    private boolean getIsAdminOrOwner(Product existingProduct) {
        User seller = existingProduct.getSeller();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdminOrOwner = seller.getEmail().equals(email)
                || seller.getRoles().stream().filter(role -> role.getRoleType().equals("ROLE_ADMIN")).findAny()
                        .isPresent();
        return isAdminOrOwner;
    }

}
