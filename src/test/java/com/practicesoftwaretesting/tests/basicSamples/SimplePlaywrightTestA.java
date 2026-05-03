package com.practicesoftwaretesting.tests.basicSamples;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimplePlaywrightTestA {

    @Test
    public void shouldShowThePageTitle() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();

        page.navigate("https://practicesoftwaretesting.com/");
        String title = page.title();

        Assertions.assertTrue(title.contains("Practice Software Testing"),
                "Something went wrong when trying to get the page title");

        browser.close();
        playwright.close();
    }
}
