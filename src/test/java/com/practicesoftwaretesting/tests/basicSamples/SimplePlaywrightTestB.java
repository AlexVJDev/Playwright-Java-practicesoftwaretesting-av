package com.practicesoftwaretesting.tests.basicSamples;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.util.Arrays;

public class SimplePlaywrightTestB {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    protected Page page;

    @BeforeAll
    public static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox","--disable-extensions","--disable-gpu"))
        );
        browserContext = browser.newContext();
    }

    @AfterAll
    public static void tearDownBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    public void setUpBrowserContext() {
        page = browserContext.newPage();
    }

    @Test
    void shouldShowThePageTitle() {
        page.navigate("https://practicesoftwaretesting.com");

        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void shouldShowSearchTermsInTheTitle() {
        page.navigate("https://practicesoftwaretesting.com");
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();

        int matchingProductCount = page.locator(".card-title").count();
        Assertions.assertTrue(matchingProductCount > 0);
    }
}
