package com.practicesoftwaretesting.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.practicesoftwaretesting.models.pojo.User;

public class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://practicesoftwaretesting.com/auth/login");
    }

    public void loginAs(User user) {
        page.getByPlaceholder("Your email").fill(user.getEmail());
        page.getByPlaceholder("Your password").fill(user.getPassword());
        page.getByRole(AriaRole.BUTTON,
                       new Page.GetByRoleOptions().setName("Login")).click();

    }

    public String title() {
        return page.getByTestId("page-title").textContent();
    }

    public String loginErrorMessage() {
        return page.getByTestId("login-error").textContent();
    }
}