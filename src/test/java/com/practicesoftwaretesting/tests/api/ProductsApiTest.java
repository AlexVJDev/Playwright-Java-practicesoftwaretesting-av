package com.practicesoftwaretesting.tests.api;

import com.practicesoftwaretesting.api.ProductsApiClient;
import com.practicesoftwaretesting.models.pojo.Product;
import com.practicesoftwaretesting.models.response.ProductResponse;
import io.restassured.mapper.ObjectMapperType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ProductsApiTest {

    private final ProductsApiClient productsApi = new ProductsApiClient();

    @Test
    void getProductsReturnsStatus200() {
        assertThat(productsApi.getProductsResponse().getStatusCode()).isEqualTo(200);
    }

    @Test
    void getProductsJsonContainsExpectedShapeAndSampleValues() {
        ProductResponse response = productsApi.getProductsResponse().as(ProductResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(response.getCurrentPage()).isEqualTo(1);
        assertThat(response.getData()).isNotNull().isNotEmpty();
        assertThat(response.getLastPage()).isPositive();
        assertThat(response.getPerPage()).isPositive();
        assertThat(response.getTotal()).isPositive();

        Product first = response.getData().getFirst();
        assertThat(first.getName()).isEqualTo("Combination Pliers");
        assertThat(first.getPrice()).isCloseTo(14.15, within(0.001));
        assertThat(first.isInStock()).isTrue();
        assertThat(first.getCategory().getSlug()).isEqualTo("pliers");
        assertThat(first.getBrand().getName()).isEqualTo("ForgeFlex Tools");
    }
}
