package be.ucll.da.reservationservice.domain.reservation;

//import be.ucll.da.reservationservice.client.billing.api.model.PatientAccountCreatedEvent;
//import be.ucll.da.reservationservice.client.car.api.model.DoctorOnPayroll;
//import be.ucll.da.reservationservice.client.doctor.api.model.DoctorsOnPayrollEvent;
//import be.ucll.da.reservationservice.client.patient.api.model.PatientValidatedEvent;
//import be.ucll.da.reservationservice.client.room.api.model.RoomReservedEvent;
import be.ucll.da.reservationservice.client.bill.api.model.UserBillCreatedEvent;
import be.ucll.da.reservationservice.client.car.api.model.GetPriceCarEvent;
import be.ucll.da.reservationservice.client.car.api.model.ReservedCarEvent;
import be.ucll.da.reservationservice.client.user.api.model.UserValidatedEvent;
import be.ucll.da.reservationservice.messaging.RabbitMqMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReservationRequestSaga {

    private final RabbitMqMessageSender eventSender;
    private final ReservationRepository repository;

    @Autowired
    public ReservationRequestSaga(RabbitMqMessageSender eventSender, ReservationRepository repository) {
        this.eventSender = eventSender;
        this.repository = repository;
    }

    public void executeSaga(Reservation reservation) {
        reservation.validatingUser();
        eventSender.sendValidateUserCommand(reservation.getId(), reservation.getUserId());
    }

    public void executeSaga(Integer id, UserValidatedEvent event) {

        Reservation reservation = getReservationById(id);
        if (event.getIsClient()) {
            reservation.userValid(event.getEmail());
            eventSender.sendValidateCarCommand(reservation.getId(), reservation.getNeededCar());
        } else {
            reservation.userInvalid();
            eventSender.sendEmail(event.getEmail(), generateMessage(reservation.getId(), "You cannot book an reservation, you are not an user at this car rental service."));
//            reservation.setPreferredStart(null);
//            reservation.setPreferredEnd(null);
            repository.deleteById(id);
        }
    }

    public void executeSaga(Integer id, ReservedCarEvent event) {
        Reservation reservation = getReservationById(id);
        reservation.carSelected();

        List<Reservation> reservations = repository.getReservationByNeededCar(event.getCarId());
        reservations.remove(reservation);
        if(!reservations.isEmpty()) {
            for (Reservation reservation2 : reservations) {
                LocalDate startRequest = reservation.getPreferredStart();
                LocalDate endRequest = reservation.getPreferredEnd();
                LocalDate startExist = reservation2.getPreferredStart();
                LocalDate endExist = reservation2.getPreferredEnd();
                if(!endRequest.isBefore(startExist) && !startRequest.isAfter(endExist)) {
                    reservation.doubleBooking();
                    eventSender.sendEmail(reservation.getUserEmail(), generateMessage(Math.toIntExact(reservation.getId()), "You cannot book an reservation, the car is already booked for that timeslot"));
                    //LocalDate specificDate = LocalDate.of(1212, 12, 12);
                    //reservation.setPreferredStart(specificDate);
                    //reservation.setPreferredEnd(specificDate);
                    repository.deleteById(id);
                    break;
                }
            }
        } else {
            if(event.getAvailable()) {
                eventSender.sendEmail(reservation.getUserEmail(), generateMessage(Math.toIntExact(reservation.getId()), "Proposal for appointment registered. Please accept or decline."));
                reservation.finalizeReservation();
            } else {
                eventSender.sendEmail(reservation.getUserEmail(), generateMessage(Math.toIntExact(reservation.getId()), "You cannot book an reservation, the car is not available"));
                reservation.noCarsFound();
                //LocalDate specificDate = LocalDate.of(1212, 12, 12);
                //reservation.setPreferredStart(specificDate);
                //reservation.setPreferredEnd(specificDate);
                repository.deleteById(id);
            }
        }
    }

    public void executeSaga(Integer id, GetPriceCarEvent event) {
        Reservation reservation = getReservationById(id);
        reservation.calculateBill();
        eventSender.sendCalculateUserBillCommand(reservation.getId(), reservation.getUserId(), event.getPrice(), reservation.getPreferredStart(), reservation.getPreferredEnd());
    }

    public void executeSaga(Integer reservationId, UserBillCreatedEvent event) {
        Reservation reservation = getReservationById(reservationId);
        reservation.setPrice(event.getAmount());
        eventSender.sendReservationFinalizedEvent(reservation, true);
        eventSender.sendEmail(reservation.getUserEmail(), "This is the price of your booking: " + event.getAmount());
    }
    public void accept(Integer reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() == ReservationStatus.REQUEST_REGISTERED) {
            eventSender.sendGetPriceCarCommand(reservationId, reservation.getNeededCar());
            reservation.accept();
            reservation.getPriceCar();
        } else {
            throw new RuntimeException("AppointmentRequest is not in a valid status to be accepted");
        }
    }

    public void decline(Integer id) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.REQUEST_REGISTERED) {
            reservation.decline();
            repository.delete(reservation);
            //eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
            //eventSender.sendCloseAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getAccountId());
        } else {
            throw new RuntimeException("AppointmentRequest is not in a valid status to be declined");
        }
    }

    private Reservation getReservationById(Integer id) {
        return repository.findById(id) .orElseThrow(() -> new ReservationException("Reservation with ID: " + id + " not found"));
    }

    private String generateMessage(Integer id, String message) {
        return "Regarding appointment " + id + ". " + message;
    }
}
