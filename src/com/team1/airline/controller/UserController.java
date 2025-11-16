package com.team1.airline.controller;

import com.team1.airline.entity.User;
import com.team1.airline.service.UserManageable;

/**
 1 사용자 관련 기능을 처리하는 컨트롤러
 2 GUI 계층과 Service 계층을 연결
 3  회원가입, 로그인, 로그아웃
 4  세션 관리 (현재 로그인한 사용자)
 */
public class UserController {
    
    private final UserManageable userService;
    private User currentUser; // 현재 로그인한 사용자 (세션)
    
    /**
       생성자
     * @param userServicer
     */
    public UserController(UserManageable userService) {
        this.userService = userService;
        this.currentUser = null;
    }
    
    /**
     * 회원가입
      @param userId 사용자 ID
      @param password 비밀번호
      @param userName 사용자 이름
      @param passportNumber 여권번호
      @param phone 전화번호
      @return 회원가입 성공 여부
     */
    public boolean register(String userId, String password, String userName, 
                          String passportNumber, String phone) {
        User newUser = new User(userId, password, userName, passportNumber, phone);
        boolean success = userService.addUser(newUser);
        
        if (success) {
            System.out.println("[UserController] 회원가입 성공: " + userId);
        } else {
            System.out.println("[UserController] 회원가입 실패: " + userId);
        }
        
        return success;
    }
    
    /**
     * 로그인
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @return 로그인 성공 여부
     */
    public boolean login(String userId, String password) {
        User user = userService.login(userId, password);
        
        if (user != null) {
            this.currentUser = user;
            System.out.println("[UserController] 로그인 성공: " + userId);
            return true;
        } else {
            System.out.println("[UserController] 로그인 실패: " + userId);
            return false;
        }
    }
    
    /**
     * 로그아웃
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("[UserController] 로그아웃: " + currentUser.getUserId());
            this.currentUser = null;
        }
    }
    
    /**
     * 현재 로그인 상태 확인
     * @return 로그인 여부
     */
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }
    
    /**
     * 현재 로그인한 사용자 정보 조회
     * @return 현재 사용자 (null이면 로그인하지 않은 상태)
     */
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    /**
     * 현재 로그인한 사용자의 ID 반환
     * @return 사용자 ID (로그인하지 않았으면 null)
     */
    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }
}
