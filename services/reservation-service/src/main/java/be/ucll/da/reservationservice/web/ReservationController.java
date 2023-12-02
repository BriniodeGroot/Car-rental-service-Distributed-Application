package be.ucll.da.reservationservice.web;

import be.ucll.da.reservationservice.api.ReservationApiDelegate;
import be.ucll.da.reservationservice.api.model.*;
import be.ucll.da.reservationservice.domain.reservation.Reservation;
import be.ucll.da.reservationservice.domain.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationController implements ReservationApiDelegate {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public ResponseEntity<ApiReservationRequestResponse> createReservationRequest(ApiReservationRequest apiReservationRequest) {
        ApiReservationRequestResponse response = new ApiReservationRequestResponse();
        response.reservationRequestNumber(reservationService.registerRequest(apiReservationRequest));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> apiV1ReservationConfirmationPost(ApiReservationConfirmation apiReservationConfirmation) {
        reservationService.finalizeReservation(apiReservationConfirmation);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiReservations> getAllReservations() {
        ApiReservations reservations = new ApiReservations();
        Iterable<Reservation> reservationList = reservationService.getAllReservations();
        for (Reservation reservation : reservationList) {
            reservations.add(toDto(reservation));
        }

        return ResponseEntity.ok(reservations);
    }

    @Override
    public ResponseEntity<ApiReservationStatus> getReservationStatus(Integer reservationId) {
        ApiReservationStatus apiReservationStatus = new ApiReservationStatus();
        apiReservationStatus.setStatus(reservationService.getStatus(reservationId));
        return ResponseEntity.ok(apiReservationStatus);
    }

    @Override
    public ResponseEntity<ApiReservations> searchReservationsByUserId(Integer userId) {
        ApiReservations reservations = new ApiReservations();
        reservations.addAll(
                reservationService.searchReservationsByUserId(userId).stream()
                        .map(this::toDto)
                        .toList()
        );

        return ResponseEntity.ok(reservations);
    }

    @Override
    public ResponseEntity<ApiPrice> getPriceByReservationId(Integer reservationId) {
        ApiPrice apiPrice = new ApiPrice();
        apiPrice.setPrice(reservationService.getPrice(reservationId));
        return ResponseEntity.ok(apiPrice);
    }

//    @Override
//    public ResponseEntity<ApiReservations> searchReservationsByLastName(String userLastName) {
//        ApiReservations reservations = new ApiReservations();
//        reservations.addAll(
//                reservationService.searchReservationsByLastName(userLastName).stream()
//                        .map(this::toDto)
//                        .toList()
//        );
//
//        return ResponseEntity.ok(reservations);
//    }

    private ApiReservation toDto(Reservation reservation) {
        return new ApiReservation()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .neededCar(reservation.getNeededCar())
                .preferredStart(reservation.getPreferredStart())
                .preferredEnd(reservation.getPreferredEnd());
    }
}
