package be.ucll.da.reservationservice.messaging;


import be.ucll.da.reservationservice.domain.car.Car;
import be.ucll.da.reservationservice.domain.car.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import be.ucll.da.reservationservice.client.car.api.model.CarCreatedEvent;

@Component
public class CarCreatedEventListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(CarCreatedEventListener.class);

    private final CarRepository repository;

    @Autowired
    public CarCreatedEventListener(CarRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = {"q.car-reservation-service"})
    public void onCarCreated(CarCreatedEvent event) {
        LOGGER.info("Received carCreatedEvent...");
        this.repository.save(new Car(event.getCar().getId(), event.getCar().getOwner(), event.getCar().getModel(), event.getCar().getType(), event.getCar().getLocation(), event.getCar().getPrice(), event.getCar().getSeats(), event.getCar().getAvailable()));
    }
}
