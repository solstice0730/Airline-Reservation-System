package com.team1.airline.controller;

import com.team1.airline.entity.Flight;
import com.team1.airline.service.FlightManageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 항공편 검색 및 조회를 처리하는 컨트롤러
 * GUI 계층과 Service 계층을 연결
 * - 항공편 검색 (출발지, 도착지, 날짜)
 * - 항공편 상세 정보 조회
 * - 잔여 좌석 확인
 */
public class FlightController {
    
    private final FlightManageable flightService;
    
    /**
     * 생성자
     * @param flightService FlightManageable 인터페이스를 구현한 서비스 (예: FlightManager)
     */
    public FlightController(FlightManageable flightService) {
        this.flightService = flightService;
    }
    
        /**
         * 항공편 검색
         * @param departureCode 출발지 공항 코드
         * @param arrivalCode 도착지 공항 코드
         * @param date 출발 날짜 (null일 수 있음)
         * @return 검색된 항공편 목록
         */
        public List<Flight> searchFlights(String departureCode, String arrivalCode, LocalDate date) {
            System.out.println("[FlightController] 항공편 검색: " +
                             departureCode + " -> " + arrivalCode +
                             " on " + (date != null ? date : "모든 날짜"));
            System.out.println("[FlightController] Calling flightService.searchFlights with: departureCode=" + departureCode + ", arrivalCode=" + arrivalCode + ", date=" + date);
    
            List<Flight> flights = flightService.searchFlights(departureCode, arrivalCode, date);
    
            if (flights.isEmpty()) {
                System.out.println("[FlightController] 검색 결과가 없습니다.");
            } else {
                System.out.println("[FlightController] " + flights.size() + "개의 항공편을 찾았습니다.");
            }
    
            return flights;
        }    /**
     * 항공편 상세 정보 조회
     * @param flightId 항공편 ID
     * @return 항공편 정보 (없으면 null)
     */
    public Flight getFlightDetail(String flightId) {
        System.out.println("[FlightController] 항공편 상세조회: " + flightId);
        
        Flight flight = flightService.getFlightById(flightId);
        
        if (flight == null) {
            System.out.println("[FlightController] 항공편을 찾을 수 없습니다: " + flightId);
        }
        
        return flight;
    }
    
    /**
     * 항공편의 좌석 현황 조회
     * @param flightId 항공편 ID
     * @return 좌석 정보 (total: 총 좌석, reserved: 예약된 좌석, available: 잔여 좌석)
     */
    public Map<String, Integer> getSeatAvailability(String flightId) {
        System.out.println("[FlightController] 좌석 현황 조회: " + flightId);
        
        Map<String, Integer> seatInfo = flightService.getSeatAvailability(flightId);
        
        if (seatInfo.isEmpty()) {
            System.out.println("[FlightController] 좌석 정보를 가져올 수 없습니다.");
        } else {
            System.out.println("[FlightController] 총 좌석: " + seatInfo.get("total") + 
                             ", 예약됨: " + seatInfo.get("reserved") + 
                             ", 잔여: " + seatInfo.get("available"));
        }
        
        return seatInfo;
    }
    
    /**
     * 항공편의 잔여 좌석 수만 반환
     * @param flightId 항공편 ID
     * @return 잔여 좌석 수
     */
    public int getAvailableSeatsCount(String flightId) {
        Map<String, Integer> seatInfo = flightService.getSeatAvailability(flightId);
        return seatInfo.getOrDefault("available", 0);
    }
}
