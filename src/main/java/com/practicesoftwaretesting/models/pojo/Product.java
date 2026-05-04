package com.practicesoftwaretesting.models.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {

    private String id;
    private String name;
    private String description;
    private double price;

    @JsonProperty("is_location_offer")
    private boolean locationOffer;

    @JsonProperty("is_rental")
    private boolean rental;

    @JsonProperty("co2_rating")
    private String co2Rating;

    @JsonProperty("in_stock")
    private boolean inStock;

    @JsonProperty("is_eco_friendly")
    private boolean ecoFriendly;

    @JsonProperty("product_image")
    private Image productImage;

    private Category category;
    private Brand brand;
}
