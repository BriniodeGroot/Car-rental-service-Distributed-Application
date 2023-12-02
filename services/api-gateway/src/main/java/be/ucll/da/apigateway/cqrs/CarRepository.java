package be.ucll.da.apigateway.cqrs;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarRepository extends CrudRepository<Car, Integer> {

    //List<Car> findAllByType(String type);
    List<Car> findAllByOwner(String owner);

    List<Car> findByTypeAndLocationAndSeats(String type, String location, int seats);

}
