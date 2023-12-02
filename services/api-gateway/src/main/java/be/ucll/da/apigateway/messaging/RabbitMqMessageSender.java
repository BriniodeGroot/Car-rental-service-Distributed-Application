package be.ucll.da.apigateway.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import be.ucll.da.apigateway.api.model.ApiCar;
import be.ucll.da.apigateway.api.model.CarCreateCommand;
import be.ucll.da.apigateway.api.model.SwitchAvailabilityCommand;
import be.ucll.da.apigateway.api.model.ApiReservationRequest;
import be.ucll.da.apigateway.api.model.ApiReservationCommand;
import be.ucll.da.apigateway.api.model.ApiReservationConfirmation;

import java.time.LocalDate;

@Component
public class RabbitMqMessageSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessageSender.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCarCreatedCommand(ApiCar apiCar) {

        CarCreateCommand command = new CarCreateCommand();
        command.setOwner(apiCar.getOwner());
        command.setModel(apiCar.getModel());
        command.setType(apiCar.getType());
        command.setLocation(apiCar.getLocation());
        command.setSeats(apiCar.getSeats());
        command.setPrice(apiCar.getPrice());
        command.setAvailable(apiCar.getAvailable());

        sendToQueue("q.car-service.create-car", command);
    }

    public void sendCarSwitchAvailabilityCommand(Integer carId) {
        SwitchAvailabilityCommand command = new SwitchAvailabilityCommand();
        command.setCarId(carId);

        sendToQueue("q.car-service.switch-availability", command);
    }

    public void sendReservationCreateCommand(ApiReservationRequest apiReservationRequest) {
         ApiReservationCommand command = new ApiReservationCommand();

         command.setUserId(apiReservationRequest.getUserId());
         command.neededCar(apiReservationRequest.getNeededCar());
         command.preferredStart(apiReservationRequest.getPreferredStart());
         command.preferredEnd(apiReservationRequest.getPreferredEnd());

         sendToQueue("q.reservation-service.create-reservation", command);

    }

    public void sendFinalizeConfirmationCommand(ApiReservationConfirmation command) {
        sendToQueue("q.reservation-service.finalize-reservation", command);
    }

    private void sendToQueue(String queue, Object message) {
        LOGGER.info("Sending message: " + message);

        this.rabbitTemplate.convertAndSend(queue, message);
    }
}
