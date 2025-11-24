package com.team1.airline.service;

import com.team1.airline.entity.Reservation;
import com.team1.airline.gui.PaymentHistoryPanel.PaymentRow;
import java.util.List;

public interface ReservationManageable {

    /**
     * C (Create) - 예약하기 비즈니스 로직
     * @param userId 예약하는 사용자 ID
     * @param flightId 예약할 항공편 ID
     * @param seatNumber 지정 좌석 번호
     * @return 예약 성공 시 생성된 Reservation 객체, 실패 시 null
     */
    Reservation makeReservation(String userId, String flightId, String seatNumber);

    /**
     * R (Read) - 내 예약 조회
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 모든 예약 리스트
     */
    List<Reservation> getMyReservations(String userId);

    /**
     * R (Read) - 내 예약 상세 정보 조회 (GUI 표시용)
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 모든 예약 상세 정보 리스트
     */
    List<PaymentRow> getMyReservationDetails(String userId);

    /**
     * U (Update) / D (Delete) - 예약 취소 비즈니스 로직
     * @param reservationId 취소할 예약 ID
     * @param userId 예약자 본인 확인용 ID
     * @return 취소 성공 시 true, 실패 시 false
     */
    boolean cancelReservation(String reservationId, String userId);

}