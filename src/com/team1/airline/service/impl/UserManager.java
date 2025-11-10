package com.team1.airline.service.impl;

// DAO 패키지와 entity, service 인터페이스를 임포트합니다.
import com.team1.airline.dao.UserDAO;
import com.team1.airline.entity.User;
import com.team1.airline.service.UserManageable;

public class UserManager implements UserManageable {

    // '부품'으로 DAO(인터페이스)를 가집니다. (다른 팀원이 구현할 부분)
    private UserDAO userDAO;

    /**
     * 생성자 (Constructor)
     * (추후 Main 또는 팩토리에서) 이 생성자를 통해 
     * 실제 DAO 구현체(CsvUserDAO 등)를 주입(Inject)해 줍니다.
     */
    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * C (Create) - 회원가입 로직 구현
     */
    @Override
    public boolean addUser(User user) {
        
        // ★★★ 회원가입 '비즈니스 로직' ★★★
        // 1. DAO를 통해 ID가 이미 존재하는지 확인합니다.
        User existingUser = userDAO.findByUserId(user.getUserId());
        
        // 2. 비즈니스 규칙: ID가 중복되면 가입시키지 않습니다.
        if (existingUser != null) {
            System.out.println("UserManager Error: ID가 이미 존재합니다.");
            return false; // 가입 실패
        }

        // 3. 중복이 없으면 DAO에게 "저장"하라고 명령합니다.
        userDAO.addUser(user);
        return true; // 가입 성공
    }

    /**
     * R (Read) - 로그인 로직 구현
     */
    @Override
    public User login(String userId, String password) {
        
        // 1. DAO에게 "ID로 User 정보 찾아와"라고 시킵니다.
        User foundUser = userDAO.findByUserId(userId);

        // ★★★ 로그인 '비즈니스 로직' (ID/PW 비교) ★★★
        
        // 2. 비즈니스 규칙 1: ID가 존재하지 않는가?
        if (foundUser == null) {
            System.out.println("UserManager: ID가 존재하지 않습니다.");
            return null; // 로그인 실패
        }

        // 3. 비즈니스 규칙 2: ID는 있는데, PW가 틀렸는가?
        if (foundUser.getPassword().equals(password)) {
            // ID도 맞고, PW도 맞음!
            System.out.println("UserManager: 로그인 성공!");
            return foundUser; // 로그인 성공 (User 객체 반환)
        } else {
            // ID는 맞았지만, PW가 틀림
            System.out.println("UserManager: 비밀번호가 틀렸습니다.");
            return null; // 로그인 실패
        }
    }
}