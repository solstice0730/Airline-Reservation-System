package com.team1.airline.dao.impl;

import com.team1.airline.dao.UserDAO;
import com.team1.airline.entity.User;
import com.team1.airline.dao.impl.DataManager;

import java.util.List;
import java.util.stream.Collectors;

public class UserDAOImpl implements UserDAO {

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
        DataManager.getInstance().getUsers().add(user);
    }

    @Override
    public User findByUserId(String userId) {
        return DataManager.getInstance().getUsers().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return DataManager.getInstance().getUsers();
    }

    @Override
    public void updateUser(User user) {
        List<User> users = DataManager.getInstance().getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                return;
            }
        }
    }

    @Override
    public void deleteUser(String userId) {
        List<User> users = DataManager.getInstance().getUsers();
        users.removeIf(u -> u.getUserId().equals(userId));
    }
}