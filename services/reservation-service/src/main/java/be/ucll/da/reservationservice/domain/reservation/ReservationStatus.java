package be.ucll.da.reservationservice.domain.reservation;

public enum ReservationStatus {

    // Happy Flow
    REGISTERED,
    BOOKING_CAR,
    VALIDATING_USER,
    VALIDATING_CAR,
    OPENING_ACCOUNT,
    REQUEST_REGISTERED,

    GET_PRICE_CAR,

    CALCULATE_BILL,

    FINISHED,

    // Failure States
    NO_USER,
    NO_CAR,
    DOUBLE_BOOKING,

    // End States
    ACCEPTED,
    DECLINED;
}