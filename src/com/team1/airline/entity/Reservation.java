package com.team1.airline.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    private String reservationId;
    private String userId;     
    private String flightId;  
    private String seatNumber; 
    private double finalPrice;
    private String status;    
    
}