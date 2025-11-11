package com.team1.airline.dao;

import java.io.BufferedReader; 
import java.io.FileReader;     
import java.io.IOException;

public interface UserDAO {
    void openFile();
    void addUser(); // 회원가입하여 User의 파일에 추가할떄 쓰이는 메서드 
    void findByUserId(); // 로그인 비교할떄 쓰이는 메서드 
}
