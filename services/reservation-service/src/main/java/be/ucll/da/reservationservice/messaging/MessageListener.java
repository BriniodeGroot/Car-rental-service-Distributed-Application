package be.ucll.da.reservationservice.messaging;

//import be.ucll.da.reservationservice.client.accounting.api.model.PatientAccountCreatedEvent;
//import be.ucll.da.reservationservice.client.doctor.api.model.DoctorsOnPayrollEvent;
//import be.ucll.da.reservationservice.client.room.api.model.RoomReservedEvent;
import be.ucll.da.reservationservice.client.car.api.model.ReservedCarEvent;
import be.ucll.da.reservationservice.client.user.api.model.UserValidatedEvent;
import be.ucll.da.reservationservice.domain.reservation.ReservationRequestSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final ReservationRequestSaga saga;

    @Autowired
    public MessageListener(ReservationRequestSaga saga) {
        this.saga = saga;
    }

    @RabbitListener(queues = {"q.user-validated.reservation-service"})
    public void onOwnerValidated(UserValidatedEvent event) {
        LOGGER.info("Receiving event: " + event);
        this.saga.executeSaga(event.getReservationId(), event);
    }

    @RabbitListener(queues = {"q.car-reserved.reservation-service"})
    public void onCarReserved(ReservedCarEvent event) {
        LOGGER.info("Receiving event:" + event);
        this.saga.executeSaga(Math.toIntExact(event.getReservationId()), event);
    }

//    @RabbitListener(queues = {"q.doctors-employed.appointment-service"})
//    public void onDoctorsEmployed(DoctorsOnPayrollEvent event) {
//        LOGGER.info("Receiving event: " + event);
//        this.saga.executeSaga(event.getAppointmentId(), event);
//    }

//    @RabbitListener(queues = {"q.room-bookings.appointment-service"})
//    public void onRoomReserved(RoomReservedEvent event) {
//        LOGGER.info("Receiving event: " + event);
//        this.saga.executeSaga(event.getAppointmentId(), event);
//    }

//    @RabbitListener(queues = {"q.account-openings.appointment-service"})
//    public void onAccountOpened(PatientAccountCreatedEvent event) {
//        LOGGER.info("Receiving event: " + event);
//        this.saga.executeSaga(event.getAppointmentId(), event);
//    }
}