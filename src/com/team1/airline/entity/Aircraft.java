package com.team1.airline.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data //getter, setter 생성  (DTO 대체)
@NoArgsConstructor   // 생성자 생성(파라미터 없는)
@AllArgsConstructor  // 생성자 생성(파라미터 있는)

public class Aircraft {
    
    private String aircraftId;
    private String modelName; 
    private int totalSeats;   
    private int economy;      
    private int business;     
    
}