package be.ucll.da.reservationservice.messaging;

//import be.ucll.da.reservationservice.client.accounting.api.model.PatientAccountCreatedEvent;
//import be.ucll.da.reservationservice.client.doctor.api.model.DoctorsOnPayrollEvent;
//import be.ucll.da.reservationservice.client.room.api.model.RoomReservedEvent;
import be.ucll.da.reservationservice.api.model.ApiReservationCommand;
import be.ucll.da.reservationservice.api.model.ApiReservationConfirmation;
import be.ucll.da.reservationservice.api.model.ApiReservationRequest;
import be.ucll.da.reservationservice.client.bill.api.model.UserBillCreatedEvent;
import be.ucll.da.reservationservice.client.car.api.model.GetPriceCarEvent;
import be.ucll.da.reservationservice.client.car.api.model.ReservedCarEvent;
import be.ucll.da.reservationservice.client.user.api.model.UserValidatedEvent;
import be.ucll.da.reservationservice.domain.reservation.ReservationRequestSaga;
import be.ucll.da.reservationservice.domain.reservation.ReservationService;
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

    private final ReservationService reservationService;

    @Autowired
    public MessageListener(ReservationRequestSaga saga, ReservationService reservationService) {
        this.saga = saga;
        this.reservationService = reservationService;
    }

    @RabbitListener(queues = {"q.user-validated.reservation-service"})
    public void onUserValidated(UserValidatedEvent event) {
        LOGGER.info("Receiving event: " + event);
        this.saga.executeSaga(event.getReservationId(), event);
    }

    @RabbitListener(queues = {"q.car-reserved.reservation-service"})
    public void onCarReserved(ReservedCarEvent event) {
        LOGGER.info("Receiving event: " + event);
        this.saga.executeSaga(Math.toIntExact(event.getReservationId()), event);
    }

    @RabbitListener(queues = {"q.car-priced.reservation-service"})
    public void onCarPriced(GetPriceCarEvent event) {
        LOGGER.info("Receiving event: " + event);
        this.saga.executeSaga(event.getReservationId(), event);
    }

    @RabbitListener(queues = {"q.bill-calculated.reservation-service"})
    public void onBillCalculated(UserBillCreatedEvent event) {
        LOGGER.info("Receiving event: " + event);
        this.saga.executeSaga(event.getReservationId(), event);
    }

    @RabbitListener(queues = {"q.reservation-service.create-reservation"})
    public void onCreateReservation(ApiReservationCommand command) {
        LOGGER.info("Receiving event: " + command);

        ApiReservationRequest apiReservationRequest = new ApiReservationRequest();
        apiReservationRequest.setUserId(command.getUserId());
        apiReservationRequest.setNeededCar(command.getNeededCar());
        apiReservationRequest.setPreferredStart(command.getPreferredStart());
        apiReservationRequest.setPreferredEnd(command.getPreferredEnd());

        reservationService.registerRequest(apiReservationRequest);
    }

    @RabbitListener(queues = {"q.reservation-service.finalize-reservation"})
    public void onFinalizeReservation(ApiReservationConfirmation command) {
        LOGGER.info("Receiving event: " + command);
        reservationService.finalizeReservation(command);
    }


}