package com.practicesoftwaretesting.tests.api;

import com.practicesoftwaretesting.api.UserApiClient;
import com.practicesoftwaretesting.fixtures.RegisterUserFixtureLoader;
import com.practicesoftwaretesting.models.pojo.Login;
import com.practicesoftwaretesting.models.pojo.User;
import com.practicesoftwaretesting.models.response.LoginResponse;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LoginApiTest {

    private final UserApiClient userApi = new UserApiClient();

    @Test
    void loginAndGetBearerAccessToken() {
        User user = RegisterUserFixtureLoader.load();
        Login login = new Login(user.getEmail(), user.getPassword());

        Response firstLogin = userApi.login(login);
        LoginResponse auth;

        if (firstLogin.getStatusCode() == 200) {
            auth = firstLogin.as(LoginResponse.class, ObjectMapperType.JACKSON_2);
        } else {
            int registerStatus = registerUser(user);
            assertThat(registerStatus)
                    .as("login failed: user registration is expected to return 201")
                    .isEqualTo(201);

            Response secondLogin = userApi.login(login);
            assertThat(secondLogin.getStatusCode())
                    .as("after successful registration, login is expected to return 200")
                    .isEqualTo(200);

            auth = secondLogin.as(LoginResponse.class, ObjectMapperType.JACKSON_2);
        }

        assertThat(auth.getAccessToken()).isNotBlank();
        assertThat(auth.getTokenType()).isEqualToIgnoringCase("bearer");
        assertThat(auth.getExpiresIn()).isPositive();
    }

    private int registerUser(User user) {
        return userApi.register(user).getStatusCode();
    }
}
