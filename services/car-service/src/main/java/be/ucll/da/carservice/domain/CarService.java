package be.ucll.da.carservice.domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import be.ucll.da.carservice.api.model.ApiCar;

import java.util.List;

@Component
public class CarService {

    private final CarRepository repository;


    @Autowired
    public CarService(CarRepository repository) {
        this.repository = repository;

    }

    public Car createCar(ApiCar data) {
        Car car = new Car(
             data.getOwner(),
             data.getModel(),
             data.getType(),
             data.getLocation(),
             data.getPrice(),
             data.getSeats(),
             data.getAvailable()
        );

        return repository.save(car);
    }

    public void deleteCar(Integer id) {
        repository.deleteById(id);
    }

    public List<Car> getCarsByType(String type) {
        if (type == null || type.isEmpty()) {
            throw new NoTypeException("Type is empty");
        }

        return repository.findAllByType(type);
    }

    public List<Car> getCarsByOwner(String owner) {
        if (owner == null || owner.isEmpty()) {
            throw new NoOwnerException("Owner is empty");
        }

        return repository.findAllByOwner(owner);
    }

    public Car switchAvailability(Integer id) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car with ID: " + id + " not found"));
        car.setAvailable(!car.getAvailable());
        return repository.save(car);
    }

    public List<Car> searchCars(String type, String location, Integer seats) {
        return repository.findByTypeAndLocationAndSeats(type, location, seats);
    }

    public Car reserveCar(Integer neededCar) {
        Car car = repository.findById(neededCar)
                .orElseThrow(() -> new CarNotFoundException("Car with ID: " + neededCar + " not found"));
        return car;
    }

    public Car getCarById(Integer neededCar) {
        Car car = repository.findById(neededCar)
                .orElseThrow(() -> new CarNotFoundException("Car with ID: " + neededCar + " not found"));
        return car;
    }
}
