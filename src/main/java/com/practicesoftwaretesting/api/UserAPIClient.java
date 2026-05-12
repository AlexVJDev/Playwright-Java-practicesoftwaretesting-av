package com.practicesoftwaretesting.api;

import com.practicesoftwaretesting.config.ApiConfig;
import com.practicesoftwaretesting.models.pojo.Login;
import com.practicesoftwaretesting.models.pojo.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserAPIClient {

    private final RequestSpecification requestSpec;

    public UserAPIClient() {
        this(ApiConfig.INSTANCE.baseUri());
    }

    public UserAPIClient(String baseUri) {
        this.requestSpec = RestAssured.given()
                .baseUri(baseUri)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
    }

    public Response register(User request) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/users/register");
    }

    public Response login(Login request) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/users/login");
    }

    public Response getUsersMe(String accessToken) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users/me");
    }
}
