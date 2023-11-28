package be.ucll.da.carservice.messaging;

import be.ucll.da.carservice.api.model.ReserveCarCommand;
import be.ucll.da.carservice.api.model.ReservedCarEvent;
import be.ucll.da.carservice.domain.Car;
import be.ucll.da.carservice.domain.CarService;
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
}
