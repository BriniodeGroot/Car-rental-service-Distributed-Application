package be.ucll.da.billingservice.messaging;

import be.ucll.da.billingservice.api.messaging.model.CalculateUserBillCommand;
import be.ucll.da.billingservice.api.messaging.model.UserBillCreatedEvent;
import be.ucll.da.billingservice.domain.BillingService;
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

    private final BillingService billingService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(BillingService billingService, RabbitTemplate rabbitTemplate) {
        this.billingService = billingService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.bill-service.calculate-bill"})
    public void onCalculateUserBill(CalculateUserBillCommand command) {
        LOGGER.info("Received command: " + command);

        Integer billId = billingService.openBill(command.getUserId());
        Integer amount = billingService.calculateAmount(command.getPrice(), command.getPreferredStart(), command.getPreferredEnd());

        UserBillCreatedEvent event = new UserBillCreatedEvent();

        event.reservationId(command.getReservationId());
        event.userId(command.getUserId());
        event.billId(billId);
        event.amount(amount);

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.bill-calculated", "", event);
    }

//    @RabbitListener(queues = {"q.account-service.close-account"})
//    public void onCloseAccount(ClosePatientAccountCommand command) {
//        LOGGER.info("Received command: " + command);
//        accountingService.closeAccount(command.getAccountId());
//
//        PatientAccountTerminatedEvent event = new PatientAccountTerminatedEvent();
//        event.appointmentId(command.getAppointmentId());
//        event.accountId(command.getAccountId());
//        event.patientId(command.getPatientId());
//
//        LOGGER.info("Sending event: " + event);
//        this.rabbitTemplate.convertAndSend("x.account-terminations", "", event);
//    }
}
