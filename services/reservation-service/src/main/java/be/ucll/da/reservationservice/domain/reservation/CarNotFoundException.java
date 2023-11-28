package be.ucll.da.reservationservice.domain.reservation;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String message) {
        super(message);
    }
}
