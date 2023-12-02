package be.ucll.da.apigateway.messaging;

import be.ucll.da.apigateway.cqrs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import be.ucll.da.apigateway.client.user.model.UserValidatedEvent;
import be.ucll.da.apigateway.client.car.model.ReservedCarEvent;
import be.ucll.da.apigateway.client.reservation.model.ReservationCreatedEvent;
import be.ucll.da.apigateway.api.model.CarCreatedEvent;
import be.ucll.da.apigateway.api.model.SwitchAvailabilityEvent;

import javax.transaction.Transactional;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public MessageListener(UserRepository userRepository, CarRepository carRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    @RabbitListener(queues = {"q.user-validated.api-gateway"})
    public void onUserValidated(UserValidatedEvent event) {
        LOGGER.info("Receiving event: " + event);

        User user = new User(event.getUserId(), event.getFirstName(), event.getLastName(), event.getEmail());
        userRepository.save(user);
    }

//    @RabbitListener(queues = {"q.car-reserved.api-gateway"})
//    public void onCarReserved(ReservedCarEvent event) {
//        LOGGER.info("Receiving event: " + event);
//
//        Car car = new Car(event.getCarId(), event.getOwner(), event.getModel(), event.getType(), event.getLocation(), event.getPrice(), event.getSeats(), event.getAvailable());
//        carRepository.save(car);
//    }

    @RabbitListener(queues = {"q.reservation-finalized.api-gateway"})
    public void onReservationValidated(ReservationCreatedEvent event) {
        LOGGER.info("Receiving event: " + event);
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new RuntimeException("User should have been added through prior event"));
        Car car = carRepository.findById(event.getCarId()).orElseThrow(() -> new RuntimeException("Doctor should have been added through prior event"));
        Reservation reservation = new Reservation(event.getId(), event.getUserId(), event.getCarId(), event.getPreferredStart(), event.getPreferredEnd(), event.getPrice(), user, car);
        reservationRepository.save(reservation);
    }

    @RabbitListener(queues = {"q.car-created.api-gateway"})
    public void onCarCreated(CarCreatedEvent event) {
        LOGGER.info("Receiving event: " + event);

        Car car = new Car(event.getId(), event.getOwner(), event.getModel(), event.getType(), event.getLocation(), event.getPrice(), event.getSeats(), event.getAvailable());
        carRepository.save(car);
    }

    @RabbitListener(queues = {"q.availability-switched.api-gateway"})
    public void onSwitchAvailabilityCar(SwitchAvailabilityEvent event) {
        Car car = carRepository.findById(event.getCarId()).orElseThrow(() -> new RuntimeException("Doctor should have been added through prior event"));
        car.setAvailable(!car.getAvailable());
        carRepository.save(car);
    }
}


