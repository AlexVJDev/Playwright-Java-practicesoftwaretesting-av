package com.practicesoftwaretesting.api;

import com.practicesoftwaretesting.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ProductsApiClient {

    private final RequestSpecification requestSpec;

    public ProductsApiClient() {
        this(ApiConfig.INSTANCE.baseUri());
    }

    public ProductsApiClient(String baseUri) {
        this.requestSpec = RestAssured.given()
                .baseUri(baseUri)
                .accept(ContentType.JSON);
    }

    public Response getProductsResponse() {
        return requestSpec
                .when()
                .get("/products");
    }
}
