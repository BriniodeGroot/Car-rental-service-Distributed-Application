package be.ucll.da.carservice.messaging;

import be.ucll.da.carservice.api.model.ApiCar;
import be.ucll.da.carservice.api.model.CarCreatedEvent;
import be.ucll.da.carservice.domain.Car;
import be.ucll.da.carservice.domain.EventSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqEventSender implements EventSender {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMqEventSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendCarCreatedEvent(Car car) {
        this.rabbitTemplate.convertAndSend("x.car-created", "", toEvent(car));
    }

    private CarCreatedEvent toEvent(Car car) {
        return new CarCreatedEvent()
                .car(new ApiCar()
                        .id(car.getId())
                        .owner(car.getOwner())
                        .model(car.getModel())
                        .type(car.getType())
                        .location(car.getLocation())
                        .price(car.getPrice())
                        .seats(car.getPrice())
                        .available(car.getAvailable()));
    }
}
