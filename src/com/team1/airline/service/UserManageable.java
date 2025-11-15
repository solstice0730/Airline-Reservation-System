package com.team1.airline.service;

import com.team1.airline.entity.User;

public interface UserManageable {

    /**
     * C (Create) - 회원가입 비즈니스 로직
     * @param user 회원가입할 사용자 정보
     * @return 회원가입 성공 시 true, (ID 중복 등으로) 실패 시 false
     */
    boolean addUser(User user);

    /**
     * R (Read) - 로그인 비즈니스 로직
     * @param userId 사용자가 입력한 ID
     * @param password 사용자가 입력한 PW
     * @return 로그인 성공 시 해당 User 객체 반환, 실패 시 null 반환
     */
    User login(String userId, String password);
    
}