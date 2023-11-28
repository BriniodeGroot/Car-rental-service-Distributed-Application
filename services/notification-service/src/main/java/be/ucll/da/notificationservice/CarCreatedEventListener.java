package be.ucll.da.notificationservice;

import be.ucll.da.notificationservice.client.car.api.model.CarCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CarCreatedEventListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(CarCreatedEventListener.class);

    @RabbitListener(queues = {"q.car-notification-service"})
    public void onCarCreated(CarCreatedEvent event) {
        LOGGER.info("Trying...");
        throw new RuntimeException("Cannot send notification");

        // LOGGER.info("Sending a notification...");
    }

    @RabbitListener(queues = {"q.fall-back-notification"})
    public void onFailedNotificationSend(CarCreatedEvent event) {
        LOGGER.info("Executing fallback code...");

        // LOGGER.info("Sending a notification...");
    }
}
