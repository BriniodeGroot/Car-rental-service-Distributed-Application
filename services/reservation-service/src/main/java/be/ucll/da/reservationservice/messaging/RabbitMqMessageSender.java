package be.ucll.da.reservationservice.messaging;

//import be.ucll.da.reservationservice.client.accounting.api.model.ClosePatientAccountCommand;
//import be.ucll.da.reservationservice.client.accounting.api.model.OpenPatientAccountCommand;
//import be.ucll.da.reservationservice.client.doctor.api.model.CheckDoctorEmployedCommand;
//import be.ucll.da.reservationservice.client.notification.api.model.SendEmailCommand;
//import be.ucll.da.reservationservice.client.owner.api.model.ValidateOwnerCommand;
//import be.ucll.da.reservationservice.client.room.api.model.ReleaseRoomCommand;
//import be.ucll.da.reservationservice.client.room.api.model.ReserveRoomCommand;
import be.ucll.da.reservationservice.api.model.ApiReservation;
import be.ucll.da.reservationservice.api.model.ReservationCreatedEvent;
import be.ucll.da.reservationservice.client.bill.api.model.CalculateUserBillCommand;
import be.ucll.da.reservationservice.client.car.api.model.GetPriceCarCommand;
import be.ucll.da.reservationservice.client.car.api.model.ReserveCarCommand;
import be.ucll.da.reservationservice.client.notification.api.model.SendEmailCommand;
import be.ucll.da.reservationservice.client.user.api.model.ValidateUserCommand;

import be.ucll.da.reservationservice.domain.reservation.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RabbitMqMessageSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessageSender.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendValidateUserCommand(Integer reservationId, Integer userId) {
        var command = new ValidateUserCommand();
        command.reservationId(Math.toIntExact(reservationId));
        command.userId(userId);
        sendToQueue("q.user-service.validate-user", command);
    }

    public void sendValidateCarCommand(Integer reservationId, Integer neededCar) {
        var command = new ReserveCarCommand();
        command.reservationId(reservationId);
        command.carNeeded(neededCar);
        sendToQueue("q.car-service.reserve-car", command);
    }

    public void sendGetPriceCarCommand(Integer reservationId,Integer neededCar) {
        var command = new GetPriceCarCommand();
        command.reservationId(reservationId);
        command.neededCar(neededCar);
        sendToQueue("q.car-service.getprice-car", command);
    }

    public void sendCalculateUserBillCommand(Integer reservationId, Integer userId, Integer price, LocalDate preferredStart, LocalDate preferredEnd) {
        var command = new CalculateUserBillCommand();
        command.reservationId(reservationId);
        command.userId(userId);
        command.price(price);
        command.preferredStart(preferredStart);
        command.preferredEnd(preferredEnd);
        sendToQueue("q.bill-service.calculate-bill", command);

    }

    public void sendReservationFinalizedEvent(Reservation reservation, boolean isAccepted) {
        var event = new ReservationCreatedEvent();
        event.id(reservation.getId());
        event.userId(reservation.getUserId());
        event.carId(reservation.getNeededCar());
        event.preferredStart(reservation.getPreferredStart());
        event.preferredEnd(reservation.getPreferredEnd());
        event.price(reservation.getPrice());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.reservation-finalized", "", event);
    }

//    public void sendValidateDoctorCommand(Integer appointmentId, String fieldOfExpertise) {
//        var command = new CheckDoctorEmployedCommand();
//        command.appointmentId(appointmentId);
//        command.fieldOfExpertise(fieldOfExpertise);
//        sendToQueue("q.doctor-service.check-employed-doctors", command);
//    }

//    public void sendBookRoomCommand(Integer appointmentId, LocalDate preferredDay) {
//        var command = new ReserveRoomCommand();
//        command.appointmentId(appointmentId);
//        command.day(preferredDay);
//        sendToQueue("q.room-service.book-room", command);
//    }

//    public void sendReleaseRoomCommand(Integer appointmentId, Integer roomId, LocalDate preferredDay) {
//        var command = new ReleaseRoomCommand();
//        command.appointmentId(appointmentId);
//        command.roomId(roomId);
//        command.day(preferredDay);
//        sendToQueue("q.room-service.release-room", command);
//    }

//    public void sendOpenAccountCommand(Integer appointmentId, Integer patientId, Integer doctorId, Integer roomId, LocalDate day) {
//        var command = new OpenPatientAccountCommand();
//        command.appointmentId(appointmentId);
//        command.patientId(patientId);
//        command.doctorId(doctorId);
//        command.roomId(roomId);
//        command.dayOfAppointment(day);
//        sendToQueue("q.account-service.open-account", command);
//    }

//    public void sendCloseAccountCommand(Integer id, Integer patientId, Integer accountId) {
//        var command = new ClosePatientAccountCommand();
//        command.appointmentId(id);
//        command.patientId(patientId);
//        command.setAccountId(accountId);
//        sendToQueue("q.account-service.close-account", command);
//    }

    public void sendEmail(String recipient, String message) {
        var command = new SendEmailCommand();
        command.recipient(recipient);
        command.message(message);
        sendToQueue("q.notification-service.send-email", command);
    }

    private void sendToQueue(String queue, Object message) {
        LOGGER.info("Sending message: " + message);

        this.rabbitTemplate.convertAndSend(queue, message);
    }
}
