package be.ucll.da.reservationservice.domain.reservation;

import be.ucll.da.reservationservice.api.model.ApiReservation;
import be.ucll.da.reservationservice.api.model.ApiReservationConfirmation;
import be.ucll.da.reservationservice.api.model.ApiReservationRequest;
import be.ucll.da.reservationservice.domain.car.Car;
import be.ucll.da.reservationservice.domain.car.CarRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ReservationRequestSaga requestSaga;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, CarRepository carRepository, ReservationRequestSaga requestSaga) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.requestSaga = requestSaga;
    }

    public String registerRequest(ApiReservationRequest request) {
        var reservation = new Reservation(request.getUserId(), request.getNeededCar(), request.getUserFirstName(), request.getUserLastName(), request.getPreferredStart(), request.getPreferredEnd());

        reservation = reservationRepository.save(reservation);
        requestSaga.executeSaga(reservation);

        return reservation.getId().toString();
    }

    public void finalizeReservation(ApiReservationConfirmation apiReservationConfirmation) {
        if (apiReservationConfirmation.getAcceptProposedReservation()) {
            requestSaga.accept(Long.valueOf(apiReservationConfirmation.getReservationRequestNumber()));
        } else {
            requestSaga.decline(Long.valueOf(apiReservationConfirmation.getReservationRequestNumber()));
        }
    }

    public void createReservation(ApiReservation data) {
        Car car = carRepository.findById(data.getNeededCar())
                .orElseThrow(() -> new CarNotFoundException("Car with ID: " + data.getNeededCar() + " not found"));

        boolean selectedCar = false;
        List<Reservation> reservations = reservationRepository.getReservationByNeededCar(car.getId());
        if (reservations.isEmpty() && car.getAvailable()) {
            selectedCar = true;
        } else {
            for (Reservation reservation : reservations) {
                LocalDate startRequest = data.getPreferredStart();
                LocalDate endRequest = data.getPreferredEnd();
                LocalDate startExist = reservation.getPreferredStart();
                LocalDate endExist = reservation.getPreferredEnd();
                if(!endRequest.isBefore(startExist) && !startRequest.isAfter(endExist)) {
                    if(car.getAvailable()) {
                        throw new ReservationException("No car available for timeslot");
                    }
                }
            }
            selectedCar = true;
        }

        if(selectedCar) {
            Reservation reservation = new Reservation(
                    data.getUserId(),
                    data.getNeededCar(),
                    data.getUserFirstName(),
                    data.getUserLastName(),
                    data.getPreferredStart(),
                    data.getPreferredEnd());

            reservationRepository.save(reservation);
        }
    }

    public List<Reservation> searchReservationsByLastName(String userLastName) {
        if (userLastName == null || userLastName.isEmpty()) {
            throw new NoUserException("User is empty");
        }

        return reservationRepository.findAllByUserLastName(userLastName);
    }
}
