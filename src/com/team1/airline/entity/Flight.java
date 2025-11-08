package com.team1.airline.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Flight {
    
    private String flightId;
    private String routeId;       
    private String aircraftId;    
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;   
         
}