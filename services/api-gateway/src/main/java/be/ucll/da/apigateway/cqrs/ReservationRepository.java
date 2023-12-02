package be.ucll.da.apigateway.cqrs;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    List<Reservation> getReservationByNeededCar(Integer car);

    List<Reservation> findAllByUserOfId(Integer userOfId);
}
