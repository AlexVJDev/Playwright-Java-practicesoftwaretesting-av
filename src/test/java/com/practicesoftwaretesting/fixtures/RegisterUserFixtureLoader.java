package com.practicesoftwaretesting.fixtures;

import com.practicesoftwaretesting.models.pojo.Address;
import com.practicesoftwaretesting.models.pojo.User;
import lombok.NonNull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegisterUserFixtureLoader {

    private static final String USER_PATH = "/data/user.txt";
    private static final String ADDRESS_PATH = "/data/address.txt";

    private static int indexOf(String[] keys, String name) {
        for (int i = 0; i < keys.length; i++) {
            if (name.equalsIgnoreCase(keys[i])) {
                return i;
            }
        }
        return -1;
    }

    public static User load() {
        Map<String, String> userRow = readMapping(USER_PATH);
        return mapUserRow(userRow, loadAddressFromFixture());
    }

    public static List<User> loadAllUsers() {
        Address address = loadAddressFromFixture();
        InputStream in = RegisterUserFixtureLoader.class.getResourceAsStream(USER_PATH);
        if (in == null) {
            throw new IllegalStateException("Classpath resource not found: " + USER_PATH);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalStateException("Expected header line in " + USER_PATH);
            }
            String[] keys = Arrays.stream(headerLine.split(";", -1)).map(String::trim).toArray(String[]::new);
            int fnIx = indexOf(keys, "firstName");
            int lnIx = indexOf(keys, "lastName");
            int phoneIx = indexOf(keys, "phone");
            int dobIx = indexOf(keys, "dob");
            int emailIx = indexOf(keys, "email");
            int passwordIx = indexOf(keys, "password");
            if (fnIx < 0 || lnIx < 0 || phoneIx < 0 || dobIx < 0 || emailIx < 0 || passwordIx < 0) {
                throw new IllegalStateException(
                        USER_PATH + " must define firstName, lastName, phone, dob, email, password; got "
                                + Arrays.toString(keys));
            }
            List<User> users = new ArrayList<>();
            String valueLine;
            while ((valueLine = reader.readLine()) != null) {
                if (valueLine.isBlank()) {
                    continue;
                }
                String[] values = valueLine.split(";", -1);
                if (keys.length != values.length) {
                    throw new IllegalStateException(
                            "Column count mismatch in " + USER_PATH + " for line: " + valueLine);
                }
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < keys.length; i++) {
                    row.put(keys[i], values[i].trim());
                }
                users.add(mapUserRow(row, address));
            }
            if (users.isEmpty()) {
                throw new IllegalStateException("No user rows in " + USER_PATH + " after header");
            }
            return users;
        } catch (Exception e) {
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new IllegalStateException("Failed to read all users from " + USER_PATH, e);
        }
    }

    private static Address loadAddressFromFixture() {
        Map<String, String> addressRow = readMapping(ADDRESS_PATH);
        Address address = new Address();
        address.setStreet(addressRow.get("street"));
        address.setHouseNumber(addressRow.get("house_number"));
        address.setCity(addressRow.get("city"));
        address.setState(addressRow.get("state"));
        address.setCountry(addressRow.get("country"));
        address.setPostalCode(addressRow.get("postal_code"));
        return address;
    }

    private static User mapUserRow(Map<String, String> userRow, Address address) {
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
