package com.ecom.store.ecommerce_store.dto;

import java.util.List;

public class ProductResponse extends ProductCreationDto {
    private String sellerEmail;
    private Long id;

    public ProductResponse(ProductResponse.Builder builder) {
        this.setName(builder.name);
        this.setDescription(builder.description);
        this.setPrice(builder.price);
        this.setCategory(builder.category);
        this.setSellerEmail(builder.sellerEmail);
        this.setImages(builder.images);
        this.setId(builder.id);
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static class Builder {
        String name;
        String description;
        Double price;
        String category;
        String sellerEmail;
        List<String> images;
        Long id;

        public Builder(Long id, String name, Double price, String sellerEmail) {
            this.name = name;
            this.price = price;
            this.sellerEmail = sellerEmail;
            this.id = id;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setImages(List<String> images) {
            this.images = images;
            return this;
        }

        public ProductResponse build() {
            return new ProductResponse(this);
        }
    }

}
