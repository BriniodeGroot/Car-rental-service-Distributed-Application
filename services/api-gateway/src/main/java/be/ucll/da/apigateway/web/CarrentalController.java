package be.ucll.da.apigateway.web;

import be.ucll.da.apigateway.api.CarrentalApiDelegate;
import be.ucll.da.apigateway.api.model.ApiCarOverview;
import be.ucll.da.apigateway.api.model.ApiCar;
import be.ucll.da.apigateway.api.model.ApiReservationOverview;
import be.ucll.da.apigateway.api.model.ApiReservation;
import be.ucll.da.apigateway.api.model.ApiReservationUser;
import be.ucll.da.apigateway.api.model.ApiReservationReservation;
import be.ucll.da.apigateway.api.model.ApiReservationCar;
import be.ucll.da.apigateway.cqrs.*;
import be.ucll.da.apigateway.api.model.CarCreateCommand;
import be.ucll.da.apigateway.messaging.RabbitMqMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import be.ucll.da.apigateway.api.model.ApiReservationRequest;
import be.ucll.da.apigateway.api.model.ApiReservationRequestResponse;
import be.ucll.da.apigateway.api.model.ApiReservationConfirmation;
import be.ucll.da.apigateway.api.model.ApiPrice;

import java.util.List;

@Component
public class CarrentalController implements CarrentalApiDelegate {

    private final RabbitMqMessageSender eventSender;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public CarrentalController(RabbitMqMessageSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public ResponseEntity<ApiCarOverview> getCarsByOwner(String owner) {
        ApiCarOverview overview = new ApiCarOverview();
        List<Car> carsOverview = carRepository.findAllByOwner(owner);
        for (Car car : carsOverview) {
            ApiCar apiCar = toDto(car);
            //ApiCar apiCar = new ApiCar();
//            apiCar.id(car.getId());
//            apiCar.model(car.getModel());
//            apiCar.setOwner(car.getOwner());
//            apiCar.type(car.getType());
//            apiCar.location(car.getLocation());
//            apiCar.price(car.getPrice());
//            apiCar.seats(car.getSeats());
//            apiCar.setAvailable(car.getAvailable());
            overview.add(apiCar);
        }
        return ResponseEntity.ok(overview);
    }

    @Override
    public ResponseEntity<Void> createCar(ApiCar apiCar) {
        eventSender.sendCarCreatedCommand(apiCar);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiCar> switchAvailability(Integer id) {
        eventSender.sendCarSwitchAvailabilityCommand(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> createReservation(ApiReservationRequest apiReservationRequest) {
        //ApiReservationRequestResponse response = new ApiReservationRequestResponse();
        //response.reservationRequestNumber(eventSender.sendReservationCreateCommand(apiReservationRequest));
        eventSender.sendReservationCreateCommand(apiReservationRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiReservationOverview> getAllReservationsByUserId(Integer userId) {
        ApiReservationOverview overview = new ApiReservationOverview();

        List<Reservation> reservations = reservationRepository.findAllByUserOfId(userId);

        if (reservations == null || reservations.isEmpty()) {
            return ResponseEntity.ok(overview);
        }

        for (Reservation reservation : reservations) {
            ApiReservation apiReservation = new ApiReservation();

            apiReservation.price(reservation.getPrice());

            ApiReservationReservation apiReservationReservation = new ApiReservationReservation();
            apiReservationReservation.setId(reservation.getId());
            apiReservationReservation.setPreferredStart(reservation.getPreferredStart());
            apiReservationReservation.setPreferredEnd(reservation.getPreferredEnd());
            apiReservation.setReservation(apiReservationReservation);

            ApiReservationUser apiReservationUser = new ApiReservationUser();
            apiReservationUser.setId(reservation.getUser().getId());
            apiReservationUser.setFirstName(reservation.getUser().getFirstName());
            apiReservationUser.setLastName(reservation.getUser().getLastName());
            apiReservationUser.setEmail(reservation.getUser().getEmail());
            apiReservation.setUser(apiReservationUser);

            ApiReservationCar apiReservationCar = new ApiReservationCar();
            apiReservationCar.setId(reservation.getCar().getId());
            apiReservationCar.setModel(reservation.getCar().getModel());
            apiReservationCar.setType(reservation.getCar().getType());
            apiReservationCar.setLocation(reservation.getCar().getLocation());
            apiReservationCar.setSeats(reservation.getCar().getSeats());
            apiReservation.setCar(apiReservationCar);

            overview.add(apiReservation);
        }

        return ResponseEntity.ok(overview);
    }

    @Override
    public ResponseEntity<Void> createReservationConfirmation(ApiReservationConfirmation apiReservationConfirmation) {
        eventSender.sendFinalizeConfirmationCommand(apiReservationConfirmation);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ApiCarOverview> searchForCars(String type,
                                                        String location,
                                                        Integer seats) {
        ApiCarOverview overview = new ApiCarOverview();
        overview.addAll(
                carRepository.findByTypeAndLocationAndSeats(type, location, seats).stream()
                        .map(this::toDto)
                        .toList()
        );
        return ResponseEntity.ok(overview);
    }

    @Override
    public ResponseEntity<ApiPrice> getPriceReservation(Integer reservationId) {
        ApiPrice apiPrice = new ApiPrice();
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationException("Reservation with ID: " + reservationId + " not found"));
        apiPrice.setPrice(reservation.getPrice());
        return ResponseEntity.ok(apiPrice);
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
