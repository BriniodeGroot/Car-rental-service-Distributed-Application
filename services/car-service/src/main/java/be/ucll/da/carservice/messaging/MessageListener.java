package be.ucll.da.carservice.messaging;

import be.ucll.da.carservice.api.model.*;
import be.ucll.da.carservice.domain.Car;
import be.ucll.da.carservice.domain.CarService;
import be.ucll.da.carservice.api.model.ApiCarId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final CarService carService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(CarService carservice, RabbitTemplate rabbitTemplate) {
        this.carService = carservice;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.car-service.reserve-car"})
    public void onReserveCar(ReserveCarCommand command) {
        LOGGER.info("Received command: " + command);

        System.out.println("de carneeded is: " + command.getCarNeeded());

        Car car = carService.reserveCar(command.getCarNeeded());

        ReservedCarEvent event = new ReservedCarEvent();
        event.reservationId(command.getReservationId());
        event.carId(car.getId());
        event.owner(car.getOwner());
        event.model(car.getModel());
        event.type(car.getType());
        event.location(car.getLocation());
        event.price(car.getPrice());
        event.seats(car.getPrice());
        event.available(car.getAvailable());


        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.car-reserved", "", event);
    }

    @RabbitListener(queues = {"q.car-service.getprice-car"})
    public void onGetPriceCar(GetPriceCarCommand command) {
        LOGGER.info("Received command: " + command);

        System.out.println("de carneeded is: " + command.getNeededCar());

        Car car = carService.getCarById(command.getNeededCar());

        GetPriceCarEvent event = new GetPriceCarEvent();
        event.reservationId(command.getReservationId());
        event.price(car.getPrice());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.car-price", "", event);
    }

    @RabbitListener(queues = {"q.car-service.create-car"})
    public void onCreateCar(CarCreatedCommand command) {
        LOGGER.info("Received command: " + command);
        ApiCar car = new ApiCar();
        car.setOwner(command.getOwner());
        car.setModel(command.getModel());
        car.setType(command.getType());
        car.setLocation(command.getLocation());
        car.setSeats(command.getSeats());
        car.setPrice(command.getPrice());
        car.setAvailable(command.getAvailable());

        Car car2 = carService.createCar(car);

        CarCreatedEvent event = new CarCreatedEvent();
        event.setId(car2.getId());
        event.setOwner(car2.getOwner());
        event.setModel(car2.getModel());
        event.setType(car2.getType());
        event.setLocation(car2.getLocation());
        event.setSeats(car2.getSeats());
        event.setPrice(car2.getPrice());
        event.setAvailable(car2.getAvailable());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.car-created", "", event);
    }

    @RabbitListener(queues = {"q.car-service.switch-availability"})
    public void OnSwtichAvailabilityCar(SwitchAvailabilityCommand command) {
        carService.switchAvailability(command.getCarId());

        SwitchAvailabilityEvent event = new SwitchAvailabilityEvent();
        event.setCarId(command.getCarId());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.car-switch", "", event);
    }

    @RabbitListener(queues = {"q.car-service.delete-car"})
    public void OnDeleteCar(ApiCarId command) {
        carService.deleteCar(command.getId());
    }
}
