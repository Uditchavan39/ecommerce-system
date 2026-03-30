package com.ecom.store.ecommerce_store.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.model.Inventory;
import com.ecom.store.ecommerce_store.model.Product;
import com.ecom.store.ecommerce_store.repository.InventoryRepository;
import com.ecom.store.ecommerce_store.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product createProduct(Product product, Integer initialQuantity) {
        product.setCreatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        Inventory inventory = new Inventory();
        inventory.setProductId(savedProduct.getId());
        inventory.setAvailableQuantity(initialQuantity != null ? initialQuantity : 0);
        inventory.setReservedQuantity(0);
        inventoryRepository.save(inventory);
        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long id, Product product, Integer availableQuantity) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setUpdatedAt(LocalDateTime.now());
            Product savedProduct = productRepository.save(existingProduct);
            if (availableQuantity != null) {
                Inventory inventory = inventoryRepository.findById(id).orElse(null);
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setProductId(savedProduct.getId());
                    inventory.setReservedQuantity(0);
                }
                inventory.setAvailableQuantity(availableQuantity);
                inventoryRepository.save(inventory);
            }
            return savedProduct;
        }
        return existingProduct;
    }

    @Transactional
    public Product deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            inventoryRepository.findById(id).ifPresent(inventoryRepository::delete);
            productRepository.delete(existingProduct);
        }
        return existingProduct;
    }

}
