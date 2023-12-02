package be.ucll.da.apigateway.cqrs;

public enum ReservationStatus {

    // Happy Flow
    REGISTERED,
    BOOKING_CAR,
    VALIDATING_USER,
    VALIDATING_CAR,
    OPENING_ACCOUNT,
    REQUEST_REGISTERED,

    // Failure States
    NO_USER,
    NO_CAR,
    DOUBLE_BOOKING,

    // End States
    ACCEPTED,
    DECLINED;
}