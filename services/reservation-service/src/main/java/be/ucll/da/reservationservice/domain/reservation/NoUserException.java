package be.ucll.da.reservationservice.domain.reservation;

public class NoUserException extends RuntimeException {

    public NoUserException(String message) {
        super(message);
    }
}
