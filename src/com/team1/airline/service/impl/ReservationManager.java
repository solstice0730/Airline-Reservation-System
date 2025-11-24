package com.team1.airline.service.impl;

// 필요한 DAO, Entity, Service 인터페이스들을 모두 임포트
import com.team1.airline.dao.*;
import com.team1.airline.entity.*;
import com.team1.airline.service.ReservationManageable;
import com.team1.airline.service.FlightManageable; // 좌석 확인을 위해

import java.util.List;
import java.util.Map;
import java.util.UUID; // 예약 ID 생성을 위해

public class ReservationManager implements ReservationManageable {

    // 예약 매니저는 거의 모든 DAO와 다른 서비스까지 필요로 합니다.
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;
    private FlightDAO flightDAO;
    private RouteDAO routeDAO;
    private AirportDAO airportDAO;
    
    // FlightManager의 '좌석 확인' 로직을 재사용하기 위해 주입받음
    private FlightManageable flightManager; 

    /**
     * 생성자 (Constructor) - 부품들을 주입받습니다.
     */
    public ReservationManager(ReservationDAO reservationDAO, UserDAO userDAO, FlightDAO flightDAO, RouteDAO routeDAO, AirportDAO airportDAO, FlightManageable flightManager) {
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
        this.flightDAO = flightDAO;
        this.routeDAO = routeDAO;
        this.airportDAO = airportDAO;
        this.flightManager = flightManager;
    }

    /**
     * C (Create) - 예약하기 로직 구현
     */
    @Override
    public Reservation makeReservation(String userId, String flightId, String seatNumber) {
        
        // ★★★ 예약 '비즈니스 로직' (가장 중요) ★★★

        // 1. 비즈니스 규칙: 사용자가 실존하는가?
        User user = userDAO.findByUserId(userId);
        if (user == null) {
            System.out.println("ReservationManager Error: 존재하지 않는 사용자입니다.");
            return null;
        }

        // 2. 비즈니스 규칙: 항공편이 실존하며 '예약 가능' 상태인가?
        Flight flight = flightDAO.findByFlightId(flightId);
        if (flight == null || !"Scheduled".equals(flight.getStatus()) && !"예약 가능".equals(flight.getStatus())) {
            // Note: FlightManager에서 '예약 가능'으로 필터링하므로 여기서도 체크
             System.out.println("ReservationManager Error: 항공편이 존재하지 않거나 예약 가능한 상태가 아닙니다.");
            return null;
        }

        // 3. 비즈니스 규칙: 좌석이 남아있는가? (FlightManager 로직 재사용)
        Map<String, Integer> seatInfo = flightManager.getSeatAvailability(flightId);
        if (seatInfo.getOrDefault("available", 0) <= 0) {
            System.out.println("ReservationManager Error: 만석입니다.");
            return null;
        }

        // 4. 비즈니스 규칙: 해당 좌석이 이미 점유되었는가?
        List<Reservation> reservations = reservationDAO.findReservationsByFlightId(flightId);
        boolean seatTaken = reservations.stream()
                .filter(r -> "Confirmed".equals(r.getStatus()))
                .anyMatch(r -> seatNumber.equals(r.getSeatNumber()));
        
        if (seatTaken) {
            System.out.println("ReservationManager Error: 해당 좌석은 이미 예약되었습니다.");
            return null;
        }

        // 5. 가격 계산 (Route 정보에서 가져오기)
        Route route = routeDAO.findByRouteId(flight.getRouteId());
        double price = (route != null) ? route.getPrice() : 0.0;

        // 6. 모든 규칙 통과 -> 예약 객체 생성
        String reservationId = UUID.randomUUID().toString(); // 고유 예약 ID 생성
        Reservation newReservation = new Reservation(
            reservationId,
            userId,
            flightId,
            seatNumber,
            price, // 최종 가격 (할인 등 추가 로직 가능)
            "Confirmed" // 초기 상태 '확정'
        );

        // 7. DAO를 통해 저장
        reservationDAO.addReservation(newReservation);
        
        // [추가] 8. 마일리지 적립 로직 (결제 금액의 5%)
        int mileageEarned = (int) (price * 0.05);
        user.setMileage(user.getMileage() + mileageEarned);
        userDAO.updateUser(user);
        
        System.out.println("ReservationManager: 예약 성공! (마일리지 " + mileageEarned + "점 적립됨)");
        System.out.println("ReservationManager: 현재 총 마일리지: " + user.getMileage());

        return newReservation; // 성공
    }

    /**
     * R (Read) - 내 예약 조회
     */
    @Override
    public List<Reservation> getMyReservations(String userId) {
        // DAO에게 단순 전달
        return reservationDAO.findReservationsByUserId(userId);
    }

    @Override
    public List<com.team1.airline.gui.PaymentHistoryPanel.PaymentRow> getMyReservationDetails(String userId) {
        List<Reservation> reservations = getMyReservations(userId);
        List<com.team1.airline.gui.PaymentHistoryPanel.PaymentRow> details = new java.util.ArrayList<>();

        for (Reservation r : reservations) {
            // "Confirmed" 상태인 예약만 목록에 포함
            if (!"Confirmed".equals(r.getStatus())) {
                continue;
            }
            
            Flight f = flightDAO.findByFlightId(r.getFlightId());
            if (f == null) continue;

            Route route = routeDAO.findByRouteId(f.getRouteId());
            if (route == null) continue;

            com.team1.airline.entity.Airport dep = airportDAO.findByAirportCode(route.getDepartureAirportCode());
            com.team1.airline.entity.Airport arr = airportDAO.findByAirportCode(route.getArrivalAirportCode());
            if (dep == null || arr == null) continue;

            String airline = f.getFlightId().substring(0, 2);
            String flightNo = f.getFlightId();
            String routeStr = dep.getAirportName() + " -> " + arr.getAirportName();
            String timeInfo = f.getDepartureTime().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + " ~ " + f.getArrivalTime().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            String seatInfo = r.getSeatNumber();
            String priceText = String.format("₩%,.0f", r.getFinalPrice());

            details.add(new com.team1.airline.gui.PaymentHistoryPanel.PaymentRow(r.getReservationId(), airline, flightNo, routeStr, timeInfo, seatInfo, priceText));
        }
        return details;
    }

    /**
     * U (Update) - 예약 취소 로직 구현
     */
    @Override
    public boolean cancelReservation(String reservationId, String userId) {
        
        // 1. DAO에서 예약 정보를 가져옵니다.
        Reservation reservation = reservationDAO.findByReservationId(reservationId);

        // ★★★ 예약 취소 '비즈니스 로직' ★★★
        
        // 2. 비즈니스 규칙: 예약이 존재하지 않는가?
        if (reservation == null) {
            System.out.println("ReservationManager Error: 존재하지 않는 예약입니다.");
            return false;
        }

        // 3. 비즈니스 규칙: 예약자 본인이 맞는가?
        if (!reservation.getUserId().equals(userId)) {
            System.out.println("ReservationManager Error: 예약자 본인만 취소할 수 있습니다.");
            return false;
        }
        
        // 4. 비즈니스 규칙: 이미 취소된 예약인가?
        if ("Cancelled".equals(reservation.getStatus())) {
            System.out.println("ReservationManager Error: 이미 취소된 예약입니다.");
            return false;
        }
        
        // (추가 로직: 출발 24시간 전인지 등... )

        // 5. 모든 규칙 통과 -> 상태 변경
        reservation.setStatus("Cancelled");
        
        // [추가] 6. 마일리지 회수 로직
        User user = userDAO.findByUserId(userId);
        if (user != null) {
            int mileageToDeduct = (int) (reservation.getFinalPrice() * 0.05); // 적립했던 5% 계산
            int currentMileage = user.getMileage();
            
            // 마일리지가 음수가 되지 않도록 처리
            user.setMileage(Math.max(0, currentMileage - mileageToDeduct));
            userDAO.updateUser(user);
            System.out.println("ReservationManager: 예약 취소로 마일리지 " + mileageToDeduct + "점이 차감되었습니다.");
            System.out.println("ReservationManager: 현재 총 마일리지: " + user.getMileage());
        }
        
        // 7. DAO를 통해 '수정'
        reservationDAO.updateReservation(reservation);
        
        System.out.println("ReservationManager: 예약이 성공적으로 취소되었습니다.");
        return true;
    }
}