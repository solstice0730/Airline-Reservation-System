package com.team1.airline.dao.impl;

import com.team1.airline.dao.UserDAO;
import com.team1.airline.entity.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDAOImpl implements UserDAO {

    private String userFilePath = "data/User.txt";

    private String userToLine(User user) {
        return String.join(" ",
                user.getUserId(),
                user.getPassword(),
                user.getUserName(),
                user.getPassportNumber(),
                user.getPhone());
    }

    @Override
    public void addUser(User user) {
        String userLine = userToLine(user) + System.lineSeparator();
        try {
            Files.write(Paths.get(userFilePath), userLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error adding user to file: " + e.getMessage());
        }
    }

    @Override
    public User findByUserId(String userId) {
        return readAllUsersFromFile().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return readAllUsersFromFile();
    }

    @Override
    public void updateUser(User user) {
        List<User> users = readAllUsersFromFile();
        List<String> lines = users.stream()
                .map(u -> u.getUserId().equals(user.getUserId()) ? userToLine(user) : userToLine(u))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(userFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error updating user in file: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(String userId) {
        List<User> users = readAllUsersFromFile();
        List<String> lines = users.stream()
                .filter(u -> !u.getUserId().equals(userId))
                .map(this::userToLine)
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(userFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error deleting user from file: " + e.getMessage());
        }
    }

    private List<User> readAllUsersFromFile() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(userFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 5) {
                    User user = new User(
                            values[0], // userId
                            values[1], // password
                            values[2], // userName
                            values[3], // passportNumber
                            values[4]  // phone
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            // Don't print error if file just doesn't exist yet
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing User.txt: " + e.getMessage());
            }
        }
        return users;
    }
}