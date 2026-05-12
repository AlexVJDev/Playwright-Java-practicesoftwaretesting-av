package com.practicesoftwaretesting.tests.base;

import com.practicesoftwaretesting.api.UserAPIClient;
import com.practicesoftwaretesting.config.ApiConfig;
import com.practicesoftwaretesting.fixtures.RegisterUserFixtureLoader;
import com.practicesoftwaretesting.models.pojo.Login;
import com.practicesoftwaretesting.models.pojo.User;
import com.practicesoftwaretesting.models.response.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.assertj.core.api.Assertions.assertThat;

public class BaseAPITest {

    private static final ConcurrentHashMap<String, String> TOKENS_BY_EMAIL = new ConcurrentHashMap<>();
    private static final AtomicBoolean TOKENS_BOOTSTRAPPED = new AtomicBoolean(false);
    private static final Object OPTIONAL_SETUP_LOCK = new Object();
    private static volatile boolean optionalSetupFinished;
    private final ThreadLocal<Login> currentLogin = new ThreadLocal<>();
    protected final UserAPIClient userAPIClient = new UserAPIClient();

    @BeforeAll
    static void prefetchTokensForUsersFromFixtureFile() {
        if (!TOKENS_BOOTSTRAPPED.compareAndSet(false, true)) {
            return;
        }
        List<Login> logins = RegisterUserFixtureLoader.loadAllUsers()
                .stream()
                .map(row -> new Login(row.getEmail(), row.getPassword()))
                .toList();

        UserAPIClient client = new UserAPIClient();
        for (Login login : logins) {
            loginAndStoreToken(client, login);
        }
    }

    protected Login authenticatedUser() {
        User user = RegisterUserFixtureLoader.load();
        return new Login(user.getEmail(), user.getPassword());
    }

    protected Login authenticatedUser(TestInfo testInfo) {
        return authenticatedUser();
    }

    protected final void authenticateAs(Login login) {
        Objects.requireNonNull(login, "login");
        currentLogin.set(login);
        tokenOrRefresh(login);
    }

    protected final String currentAccessToken() {
        Login login = currentLogin.get();
        if (login == null) {
            throw new IllegalStateException("No user in context");
        }
        return tokenOrRefresh(login);
    }

    /** Access token stored after prefetch (email key lower case). */
    protected static String cachedAccessTokenForEmail(String email) {
        String key = Objects.requireNonNull(email, "email").trim().toLowerCase(Locale.ROOT);
        String token = TOKENS_BY_EMAIL.get(key);
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "No cached token for " + email + "; extend BaseAPITest and ensure @BeforeAll prefetch ran.");
        }
        return token;
    }

    /**
     * Rest Assured specification: JSON API base URI, Bearer auth, single automatic re-login on HTTP 401.
     */
    protected final RequestSpecification authenticatedRequest() {
        return RestAssured.given()
                .baseUri(ApiConfig.INSTANCE.baseUri())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .filter(this::injectBearerAndRetryOnUnauthorized);
    }

    @BeforeEach
    void baseApiAuthenticate(TestInfo testInfo) {
        Login login = authenticatedUser(testInfo);
        currentLogin.set(login);
        tokenOrRefresh(login);
    }

    @AfterEach
    void baseApiClearLoginContext() {
        currentLogin.remove();
    }

    private Response injectBearerAndRetryOnUnauthorized(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext ctx) {
        Login login = currentLogin.get();
        if (login == null) {
            throw new IllegalStateException(
                    "authenticatedRequest() requires an active user (JUnit @BeforeEach on BaseAPITest)");
        }
        String token = tokenOrRefresh(login);
        requestSpec.header("Authorization", "Bearer " + token);
        Response response = ctx.next(requestSpec, responseSpec);
        if (response.getStatusCode() == 401) {
            TOKENS_BY_EMAIL.remove(emailKey(login));
            loginFreshToken(login);
            token = tokenOrRefresh(login);
            requestSpec.header("Authorization", "Bearer " + token);
            response = ctx.next(requestSpec, responseSpec);
        }
        return response;
    }

    private void loginFreshToken(Login login) {
        loginAndStoreToken(userAPIClient, login);
    }

    /**
     * If login responds with missing/forbidden-style codes (no account yet), registers every row from
     * {@code classpath:/data/user.txt} once per JVM (shared address from {@code address.txt}), then retries login.
     */
    private static void optionalSetupRegisterAllUsersFromFixture(UserAPIClient client) {
        synchronized (OPTIONAL_SETUP_LOCK) {
            if (optionalSetupFinished) {
                return;
            }
            for (User user : RegisterUserFixtureLoader.loadAllUsers()) {
                Response reg = client.register(user);
                int registerStatus = reg.getStatusCode();
                assertThat(registerStatus)
                        .as("optional-setup registration for %s", user.getEmail())
                        .isIn(201, 409);
            }
            optionalSetupFinished = true;
        }
    }

    private static boolean loginFailureMayMeanUserMissing(int httpStatus) {
        return httpStatus == 401 || httpStatus == 403 || httpStatus == 404;
    }

    private static void loginAndStoreToken(UserAPIClient client, Login login) {
        Response response = client.login(login);
        if (response.getStatusCode() != 200 && loginFailureMayMeanUserMissing(response.getStatusCode())) {
            optionalSetupRegisterAllUsersFromFixture(client);
            response = client.login(login);
        }
        if (response.getStatusCode() != 200) {
            throw new AssertionError(
                    "Login failed for " + login.getEmail() + ": HTTP " + response.getStatusCode()
                            + " body=" + response.asString());
        }
        LoginResponse body = response.as(LoginResponse.class, ObjectMapperType.JACKSON_2);
        TOKENS_BY_EMAIL.put(emailKey(login), body.getAccessToken());
    }

    private String tokenOrRefresh(Login login) {
        String cached = TOKENS_BY_EMAIL.get(emailKey(login));
        if (cached != null) {
            return cached;
        }
        loginFreshToken(login);
        return Objects.requireNonNull(TOKENS_BY_EMAIL.get(emailKey(login)), "token after login");
    }

    private static String emailKey(Login login) {
        return login.getEmail().toLowerCase(Locale.ROOT);
    }
}
