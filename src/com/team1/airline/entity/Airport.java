package com.team1.airline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Airport {
    
    private String airportCode;
    private String airportName;
    private String city;
    private String country;
}
