package be.ucll.da.userservice.messaging;

import be.ucll.da.userservice.api.messaging.model.UserValidatedEvent;
import be.ucll.da.userservice.api.messaging.model.ValidateUserCommand;
import be.ucll.da.userservice.domain.User;
import be.ucll.da.userservice.domain.UserService;
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

    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(UserService userService, RabbitTemplate rabbitTemplate) {
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.user-service.validate-user"})
    public void onValidateOwner(ValidateUserCommand command) {
        LOGGER.info("Received command: " + command);

        User user = userService.validateUser(command.getUserId());
        UserValidatedEvent event = new UserValidatedEvent();
        event.reservationId(command.getReservationId());
        event.userId(user.id());
        event.firstName(user.firstName());
        event.lastName(user.lastName());
        event.email(user.email());
        event.isClient(user.isClient());


        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.user-validated", "", event);
    }
}
