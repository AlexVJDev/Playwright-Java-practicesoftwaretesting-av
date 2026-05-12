package com.practicesoftwaretesting.tests.api;

import com.practicesoftwaretesting.fixtures.RegisterUserFixtureLoader;
import com.practicesoftwaretesting.models.pojo.User;
import com.practicesoftwaretesting.models.response.UserProfileResponse;
import com.practicesoftwaretesting.tests.base.BaseAPITest;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAnnaApiTest extends BaseAPITest {

    private static final String USER_EMAIL = "john@ggmail.com";

    @Test
    void getUsersMeReturnsProfileMatchingRegisteredUserFixture() {
        String token = cachedAccessTokenForEmail(USER_EMAIL);

        Response response = userAPIClient.getUsersMe(token);
        assertThat(response.getStatusCode()).as("GET /users/me").isEqualTo(200);

        UserProfileResponse me = response.as(UserProfileResponse.class, ObjectMapperType.JACKSON_2);
        User expectedUser = RegisterUserFixtureLoader.loadAllUsers().stream()
                .filter(u -> USER_EMAIL.equalsIgnoreCase(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fixture must contain " + USER_EMAIL));

        assertThat(me.getId()).matches("^[0-9a-z]{20,}$");
        assertThat(me.getProvider()).isNull();

        assertThat(me.getFirstName()).isEqualTo(expectedUser.getFirstName());
        assertThat(me.getLastName()).isEqualTo(expectedUser.getLastName());
        assertThat(me.getPhone()).isEqualTo(expectedUser.getPhone());
        assertThat(me.getDob()).isEqualTo(expectedUser.getDob());
        assertThat(me.getEmail()).isEqualTo(expectedUser.getEmail());

        assertThat(me.isTotpEnabled()).isFalse();
        assertThat(me.getCreatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        assertThat(me.getAddress()).usingRecursiveComparison().isEqualTo(expectedUser.getAddress());
    }
}
