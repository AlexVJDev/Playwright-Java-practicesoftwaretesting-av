package com.practicesoftwaretesting.tests.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ProductsApiTest {

    private static final String PRODUCTS_URL = "https://api.practicesoftwaretesting.com/products";

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void getProductsReturnsStatus200() throws Exception {
        HttpResponse<String> response = sendGetProducts();

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getProductsJsonContainsExpectedShapeAndSampleValues() throws Exception {
        HttpResponse<String> response = sendGetProducts();

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode root = MAPPER.readTree(response.body());

        assertThat(root.path("current_page").asInt()).isEqualTo(1);
        assertThat(root.path("data").isArray()).isTrue();
        assertThat(root.path("data").size()).isPositive();
        assertThat(root.path("last_page").asInt()).isPositive();
        assertThat(root.path("per_page").asInt()).isPositive();
        assertThat(root.path("total").asInt()).isPositive();

        JsonNode first = root.path("data").get(0);
        assertThat(first.path("name").asText()).isEqualTo("Combination Pliers");
        assertThat(first.path("price").asDouble()).isCloseTo(14.15, within(0.001));
        assertThat(first.path("in_stock").asBoolean()).isTrue();
        assertThat(first.path("category").path("slug").asText()).isEqualTo("pliers");
        assertThat(first.path("brand").path("name").asText()).isEqualTo("ForgeFlex Tools");
    }

    private static HttpResponse<String> sendGetProducts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PRODUCTS_URL))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
