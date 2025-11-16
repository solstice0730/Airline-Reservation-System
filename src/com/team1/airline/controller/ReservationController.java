package com.team1.airline.controller;

import com.team1.airline.entity.Reservation;
import com.team1.airline.service.ReservationManageable;

import java.util.List;

/**
 ---- 예약 관련 기능을 처리하는 컨트롤러
  GUI 계층과 Service 계층을 연결
 * - 항공편 예약 생성
 * - 예약 내역 조회
 * - 예약 취소
 */
public class ReservationController {
    
    private final ReservationManageable reservationService;
    private final UserController userController;
    
    /**
     * 생성자
     * @param reservationService ReservationManageable 인터페이스를 구현한 서비스
     * @param userController 사용자 컨트롤러 (로그인 상태 확인용)
     */
    public ReservationController(ReservationManageable reservationService, 
                                UserController userController) {
        this.reservationService = reservationService;
        this.userController = userController;
    }
    
    /**
     * 항공편 예약
     * @param flightId 항공편 ID
     * @param seatNumber 좌석 번호 
     * @return 생성된 예약 정보 (실패 시 null)
     */
    public Reservation makeReservation(String flightId, String seatNumber) {
        // 로그인 확인
        if (!userController.isLoggedIn()) {
            System.out.println("[ReservationController] 로그인이 필요합니다.");
            return null;
        }
        
        String userId = userController.getCurrentUserId();
        
        System.out.println("[ReservationController] 예약 시도: " + 
                         "User=" + userId + ", Flight=" + flightId + 
                         ", Seat=" + seatNumber);
        
        Reservation reservation = reservationService.makeReservation(userId, flightId, seatNumber);
        
        if (reservation != null) {
            System.out.println("[ReservationController] 예약 성공! 예약번호: " + 
                             reservation.getReservationId());
        } else {
            System.out.println("[ReservationController] 예약 실패");
        }
        
        return reservation;
    }
    
    /**
     * 내 예약 목록 조회
     * @return 현재 로그인한 사용자의 예약 목록
     */
    public List<Reservation> getMyReservations() {
        if (!userController.isLoggedIn()) {
            System.out.println("[ReservationController] 로그인이 필요합니다.");
            return List.of();
        }
        
        String userId = userController.getCurrentUserId();
        
        System.out.println("[ReservationController] 예약 목록 조회: " + userId);
        
        List<Reservation> reservations = reservationService.getMyReservations(userId);
        
        System.out.println("[ReservationController] " + reservations.size() + "개의 예약을 찾았습니다.");
        
        return reservations;
    }
    
    /**
     * 예약 취소
     * @param reservationId 예약 ID
     * @return 취소 성공 여부
     */
    public boolean cancelReservation(String reservationId) {
        if (!userController.isLoggedIn()) {
            System.out.println("[ReservationController] 로그인이 필요합니다.");
            return false;
        }
        
        String userId = userController.getCurrentUserId();
        
        System.out.println("[ReservationController] 예약 취소 시도: " + 
                         "User=" + userId + ", Reservation=" + reservationId);
        
        boolean success = reservationService.cancelReservation(reservationId, userId);
        
        if (success) {
            System.out.println("[ReservationController] 예약 취소 성공");
        } else {
            System.out.println("[ReservationController] 예약 취소 실패");
        }
        
        return success;
    }
}
