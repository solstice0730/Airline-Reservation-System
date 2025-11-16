package com.team1.airline.dao;

import com.team1.airline.entity.User;
import java.util.List;

public interface UserDAO {
    void addUser(User user);
    User findByUserId(String userId);
    List<User> findAll();
    void updateUser(User user);
    void deleteUser(String userId);
}
