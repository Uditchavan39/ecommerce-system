package com.ecom.store.ecommerce_store.service;

import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.model.Inventory;
import com.ecom.store.ecommerce_store.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Integer getAvailableQuantity(Long productId) {
        return inventoryRepository.findById(productId).map(Inventory::getAvailableQuantity).orElse(0);
    }

    @Transactional
    public void reserve(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));
        if (inventory.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Out Of Stock");
        }
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    // if payment succeeds
    @Transactional
    public void confirm(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));
        if (inventory.getReservedQuantity() < quantity) {
            throw new RuntimeException("Invalid confirm quantity");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    // if payment fails
    @Transactional
    public void release(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));
        if (inventory.getReservedQuantity() < quantity) {
            throw new RuntimeException("Invalid release quantity");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventoryRepository.save(inventory);
    }
}
