package com.practicesoftwaretesting.fixtures;

import com.practicesoftwaretesting.models.pojo.Address;
import com.practicesoftwaretesting.models.pojo.User;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class RegisterUserFixtureLoader {

    private static final String USER_PATH = "/data/user.txt";
    private static final String ADDRESS_PATH = "/data/address.txt";

    private RegisterUserFixtureLoader() {
    }

    public static User load() {
        Map<String, String> userRow = readMapping(USER_PATH);
        Map<String, String> addressRow = readMapping(ADDRESS_PATH);

        Address address = new Address();
        address.setStreet(addressRow.get("street"));
        address.setHouseNumber(addressRow.get("house_number"));
        address.setCity(addressRow.get("city"));
        address.setState(addressRow.get("state"));
        address.setCountry(addressRow.get("country"));
        address.setPostalCode(addressRow.get("postal_code"));

        User user = new User();
        user.setFirstName(userRow.get("firstName"));
        user.setLastName(userRow.get("lastName"));
        user.setPhone(userRow.get("phone"));
        user.setDob(userRow.get("dob"));
        user.setPassword(userRow.get("password"));
        user.setEmail(userRow.get("email"));
        user.setAddress(address);
        return user;
    }

    private static Map<String, String> readMapping(String resourcePath) {
        InputStream in = RegisterUserFixtureLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("Classpath resource not found: " + resourcePath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            String valueLine = reader.readLine();
            return getStringStringMap(resourcePath, headerLine, valueLine);
        } catch (Exception e) {
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new IllegalStateException("Failed to read " + resourcePath, e);
        }
    }

    private static @NonNull Map<String, String> getStringStringMap(String resourcePath, String headerLine, String valueLine) {
        if (headerLine == null || valueLine == null) {
            throw new IllegalStateException("Expected header and data line in " + resourcePath);
        }
        String[] keys = headerLine.split(";", -1);
        String[] values = valueLine.split(";", -1);
        if (keys.length != values.length) {
            throw new IllegalStateException(
                    "Column count mismatch in " + resourcePath + ": "
                            + Arrays.toString(keys) + " vs " + Arrays.toString(values));
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i].trim(), values[i].trim());
        }
        return map;
    }
}
