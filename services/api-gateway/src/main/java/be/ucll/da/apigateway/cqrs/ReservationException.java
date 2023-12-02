package be.ucll.da.apigateway.cqrs;

public class ReservationException extends RuntimeException {

    public ReservationException(String message) {
        super(message);
    }
}
