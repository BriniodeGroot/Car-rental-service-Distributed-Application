package be.ucll.da.reservationservice.domain.reservation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    List<Reservation> getReservationByNeededCar(Integer car);

    List<Reservation> findAllByUserId(Integer userId);
}
