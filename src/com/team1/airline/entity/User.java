package com.team1.airline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String password;
    private String userName;
    private String passportNumber;
    private String phone;
    private int mileage; // [추가] 마일리지 필드
}