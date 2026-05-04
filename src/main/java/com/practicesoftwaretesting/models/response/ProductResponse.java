package com.practicesoftwaretesting.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.practicesoftwaretesting.models.pojo.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {

    @JsonProperty("current_page")
    private int currentPage;

    private List<Product> data;

    private int from;

    @JsonProperty("last_page")
    private int lastPage;

    @JsonProperty("per_page")
    private int perPage;

    private int to;

    private int total;
}
