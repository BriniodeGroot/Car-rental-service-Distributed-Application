package be.ucll.da.carservice.domain;

public class NoOwnerException extends RuntimeException {

    public NoOwnerException(String message) {
        super(message);
    }
}
