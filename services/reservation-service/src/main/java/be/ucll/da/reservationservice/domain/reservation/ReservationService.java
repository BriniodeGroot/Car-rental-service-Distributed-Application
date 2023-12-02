package be.ucll.da.reservationservice.domain.reservation;

import be.ucll.da.reservationservice.api.model.ApiPrice;
import be.ucll.da.reservationservice.api.model.ApiReservationConfirmation;
import be.ucll.da.reservationservice.api.model.ApiReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationRequestSaga requestSaga;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, ReservationRequestSaga requestSaga) {
        this.reservationRepository = reservationRepository;
        this.requestSaga = requestSaga;
    }

    public String registerRequest(ApiReservationRequest request) {
        var reservation = new Reservation(request.getUserId(), request.getNeededCar(), request.getPreferredStart(), request.getPreferredEnd());

        reservation = reservationRepository.save(reservation);
        System.out.println("reservation service");
        requestSaga.executeSaga(reservation);

        return reservation.getId().toString();
    }

    public void finalizeReservation(ApiReservationConfirmation apiReservationConfirmation) {
        if (apiReservationConfirmation.getAcceptProposedReservation()) {
            requestSaga.accept(Integer.valueOf(apiReservationConfirmation.getReservationRequestNumber()));
        } else {
            requestSaga.decline(Integer.valueOf(apiReservationConfirmation.getReservationRequestNumber()));
        }
    }

    public Iterable<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public String getStatus(Integer reservationId) {
        Reservation reservation =  reservationRepository.findById(reservationId) .orElseThrow(() -> new ReservationException("Reservation with ID: " + reservationId + " not found"));
        return reservation.getStatus().name();
    }

    public List<Reservation> searchReservationsByUserId(Integer userId) {
        if (userId == null) {
            throw new NoUserException("User is empty");
        }

        return reservationRepository.findAllByUserId(userId);
    }

    public Integer getPrice(Integer reservationId) {
        Reservation reservation =  reservationRepository.findById(reservationId) .orElseThrow(() -> new ReservationException("Reservation with ID: " + reservationId + " not found"));
        return reservation.getPrice();
    }
}
