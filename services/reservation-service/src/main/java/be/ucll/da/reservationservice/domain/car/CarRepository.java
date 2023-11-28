package be.ucll.da.reservationservice.domain.car;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends CrudRepository<Car, Long> {

    List<Car> findAllByType(String type);
    List<Car> findAllByOwner(String owner);
    //List<Car> findByTypeAndLocationAndSeats(String type, String location, int seats);


}
