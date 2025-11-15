package com.team1.airline.service;

import com.team1.airline.entity.Flight;
import com.team1.airline.entity.Airport;
import com.team1.airline.entity.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FlightManageable {

    /**
     * R (Read) - 항공편 검색 비즈니스 로직
     * @param departureCode 출발 공항 코드 (예: "ICN")
     * @param arrivalCode 도착 공항 코드 (예: "JFK")
     * @param date 출발 날짜
     * @return 검색 조건에 맞는 항공편 리스트
     */
    List<Flight> searchFlights(String departureCode, String arrivalCode, LocalDate date);

    /**
     * R (Read) - 항공편 상세 조회
     * @param flightId 조회할 항공편 ID
     * @return Flight 객체 (없으면 null)
     */
    Flight getFlightById(String flightId);

    /**
     * R (Read) - 특정 항공편의 좌석 점유 현황 조회
     * (예약된 좌석 수, 총 좌석 수, 잔여 좌석 수 등)
     * @param flightId 항공편 ID
     * @return 좌석 정보 (예: Map<String, Integer> - "total", "reserved", "available")
     */
    Map<String, Integer> getSeatAvailability(String flightId);

    // --- (관리자용) ---
    // C (Create) - 신규 노선 및 항공편 추가 로직
    // U (Update) - 항공편 상태 변경 로직
    // ...
}