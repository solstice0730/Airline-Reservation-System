package com.team1.airline.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    
    private String routeId;
    private String departureAirportCode; 
    private String arrivalAirportCode;   
    private double price; 
    private int duration; 
    
}