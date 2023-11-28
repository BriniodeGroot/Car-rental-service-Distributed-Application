package be.ucll.da.carservice.web;

import be.ucll.da.carservice.api.CarApiDelegate;
import be.ucll.da.carservice.api.model.ApiCar;
import be.ucll.da.carservice.api.model.ApiCars;
import be.ucll.da.carservice.api.model.ApiSuccess;
import be.ucll.da.carservice.domain.Car;
import be.ucll.da.carservice.domain.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CarController implements CarApiDelegate {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Override
    public ResponseEntity<ApiCars> getCarsByType(String type) {
        ApiCars cars = new ApiCars();
        cars.addAll(
            carService.getCarsByType(type).stream()
                .map(this::toDto)
                .toList()
        );

        return ResponseEntity.ok(cars);
    }

    @Override
    public ResponseEntity<ApiCars> getCarsByOwner(String owner) {
        ApiCars cars = new ApiCars();
        cars.addAll(
                carService.getCarsByOwner(owner).stream()
                        .map(this::toDto)
                        .toList()
        );

        return ResponseEntity.ok(cars);
    }
    @Override
    public ResponseEntity<Void> createCar(ApiCar car) {
        carService.createCar(car);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiSuccess> deleteCar(Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiCar> switchAvailability(Long id) {
        carService.switchAvailability(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiCars> searchCars(String type,
                                              String location,
                                              Integer seats) {
        ApiCars cars = new ApiCars();
        cars.addAll(
                carService.searchCars(type, location, seats).stream()
                        .map(this::toDto)
                        .toList()
        );

        return ResponseEntity.ok(cars);
    }

    private ApiCar toDto(Car car) {
        return new ApiCar()
                .id(car.getId())
                .owner(car.getOwner())
                .model(car.getModel())
                .type(car.getType())
                .location(car.getLocation())
                .price(car.getPrice())
                .seats(car.getSeats())
                .available(car.getAvailable());
    }
}
