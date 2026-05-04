package com.practicesoftwaretesting.tests.ui;

import com.practicesoftwaretesting.api.UserAPIClient;
import com.practicesoftwaretesting.fixtures.PlaywrightTestCase;
import com.practicesoftwaretesting.fixtures.RegisterUserFixtureLoader;
import com.practicesoftwaretesting.models.pojo.User;
import com.practicesoftwaretesting.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginWithRegisteredUserTest extends PlaywrightTestCase {

    private final UserAPIClient userAPIClient = new UserAPIClient();

    @Test
    @DisplayName("Should be able to login with a registered user")
    void should_login_with_registered_user() {
        User user = RegisterUserFixtureLoader.load();
        userAPIClient.register(user);

        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user);

        assertThat(loginPage.title()).isEqualTo("My account");
    }

    @Test
    @DisplayName("Should reject a user if they provide a wrong password")
    void should_reject_user_with_invalid_password() {
        User user = RegisterUserFixtureLoader.load();
        userAPIClient.register(user);
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user.withPassword("wrong-password"));

        assertThat(loginPage.loginErrorMessage()).isEqualTo("Invalid email or password");
    }
}