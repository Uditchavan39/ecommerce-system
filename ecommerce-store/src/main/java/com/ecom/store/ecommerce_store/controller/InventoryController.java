package com.ecom.store.ecommerce_store.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.store.ecommerce_store.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public void reserve(@RequestParam Long productId, @RequestParam Integer quantity) {
        inventoryService.reserve(productId, quantity);
    }

    @PostMapping("/confirm")
    public void confirm(@RequestParam Long productId, @RequestParam Integer quantity) {
        inventoryService.confirm(productId, quantity);
    }

    @PostMapping("/release")
    public void release(@RequestParam Long productId, @RequestParam Integer quantity) {
        inventoryService.release(productId, quantity);
    }
}
