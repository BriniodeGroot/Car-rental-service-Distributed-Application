package be.ucll.da.carservice.domain;

public interface EventSender {

    void sendCarCreatedEvent(Car car);
}
