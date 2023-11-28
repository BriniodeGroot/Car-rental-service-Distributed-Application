package be.ucll.da.reservationservice.domain.reservation;

//import be.ucll.da.reservationservice.client.billing.api.model.PatientAccountCreatedEvent;
//import be.ucll.da.reservationservice.client.car.api.model.DoctorOnPayroll;
//import be.ucll.da.reservationservice.client.doctor.api.model.DoctorsOnPayrollEvent;
//import be.ucll.da.reservationservice.client.patient.api.model.PatientValidatedEvent;
//import be.ucll.da.reservationservice.client.room.api.model.RoomReservedEvent;
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

        Reservation reservation = getReservationById(Long.valueOf(id));
        if (event.getIsClient()) {
            reservation.userValid(event.getEmail());
            eventSender.sendValidateCarCommand(Math.toIntExact(reservation.getId()), Math.toIntExact(reservation.getNeededCar()));
        } else {
            reservation.userInvalid();
            eventSender.sendEmail(event.getEmail(), generateMessage(Math.toIntExact(reservation.getId()), "You cannot book an reservation, you are not an user at this car rental service."));
        }
    }

    public void executeSaga(Integer id, ReservedCarEvent event) {
        boolean selectedCar = false;
        Reservation reservation = getReservationById(Long.valueOf(id));

        List<Reservation> reservations = repository.getReservationByNeededCar(event.getCarId());
        if (reservations.isEmpty() && event.getAvailable()) {
            selectedCar = true;
        } else {
            for (Reservation reservation2 : reservations) {
                LocalDate startRequest = reservation.getPreferredStart();
                LocalDate endRequest = reservation.getPreferredEnd();
                LocalDate startExist = reservation2.getPreferredStart();
                LocalDate endExist = reservation2.getPreferredEnd();
                if(!endRequest.isBefore(startExist) && !startRequest.isAfter(endExist)) {
                    reservation.doubleBooking();
                    eventSender.sendEmail(reservation.getUserEmail(), generateMessage(Math.toIntExact(reservation.getId()), "You cannot book an reservation, the car is not available during your chosen timeslot"));
                    repository.deleteById(id.longValue());
                    break;
                }
            }
        }

        if(selectedCar) {
            reservation.finalizeReservation();
            eventSender.sendEmail(reservation.getUserEmail(), generateMessage(Math.toIntExact(reservation.getId()), "Proposal for appointment registered. Please accept or decline."));
        }
    }

//    public void executeSaga(Integer id, DoctorsOnPayrollEvent event) {
//        Appointment appointment = getAppointmentById(id);
//
//        Integer selectedDoctor = null;
//        if (event.getDoctors() != null && !event.getDoctors().isEmpty()) {
//            for (DoctorOnPayroll doctor : event.getDoctors()) {
//                List<Appointment> appointments = repository.getAppointmentsForDoctorOnDay(doctor.getId(), appointment.getPreferredDay());
//
//                if (appointments.isEmpty()) {
//                    selectedDoctor = doctor.getId();
//                    break;
//                }
//            }
//        }
//
//        if (selectedDoctor != null) {
//            appointment.doctorSelected(selectedDoctor);
//            eventSender.sendBookRoomCommand(appointment.getId(), appointment.getPreferredDay());
//        } else {
//            appointment.noDoctorsFound();
//            eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, we have no doctors for your requested expertise available on this day."));
//        }
//    }

//    public void executeSaga(Integer id, RoomReservedEvent event) {
//        Appointment appointment = getAppointmentById(id);
//        if (event.getRoomAvailable()) {
//            appointment.roomAvailable(event.getRoomId());
//            eventSender.sendOpenAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getDoctor(), appointment.getRoomId(), appointment.getPreferredDay());
//        } else {
//            appointment.noRoomAvailable();
//            eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, we have no room available on your preferred day."));
//        }
//    }

//    public void executeSaga(Integer id, PatientAccountCreatedEvent event) {
//        Appointment appointment = getAppointmentById(id);
//
//        if (event.getAccountCreated()) {
//            // Final check to make sure that another request that went through in parallel does not also succeed
//            List<Appointment> appointments = repository.getAppointmentsForDoctorOnDay(appointment.getDoctor(), appointment.getPreferredDay());
//            if (appointments.isEmpty()) {
//                appointment.finalizeAppointment(event.getAccountId());
//                eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "Proposal for appointment registered. Please accept or decline."));
//            } else {
//                appointment.doubleBooking();
//                eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
//                eventSender.sendCloseAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getAccountId());
//                eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, there was an error in our system."));
//            }
//        } else {
//            appointment.noInsurance();
//            eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
//            eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, you are not insured."));
//        }
//
//
//    }

    public void accept(Long id) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.REQUEST_REGISTERED) {
            reservation.accept();
        } else {
            throw new RuntimeException("AppointmentRequest is not in a valid status to be accepted");
        }
    }

    public void decline(Long id) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.REQUEST_REGISTERED) {
            reservation.decline();
            //eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
            //eventSender.sendCloseAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getAccountId());
        } else {
            throw new RuntimeException("AppointmentRequest is not in a valid status to be declined");
        }
    }

    private Reservation getReservationById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    private String generateMessage(Integer id, String message) {
        return "Regarding appointment " + id + ". " + message;
    }
}
