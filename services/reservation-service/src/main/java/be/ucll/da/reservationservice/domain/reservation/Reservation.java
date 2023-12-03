package be.ucll.da.reservationservice.domain.reservation;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    @GeneratedValue
    private int id;
    private int userId;

    boolean isValidUserId;

    private String userEmail;

    private int neededCar;

    private int price;

    private LocalDate preferredStart;

    private LocalDate preferredEnd;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    //protected Reservation(Integer ownerId, Long neededCar, String userFirstName, String userLastName, @Valid LocalDate preferredStart, @Valid LocalDate preferredEnd) {}


    public Reservation() {
    }

    public Reservation(Integer userId, Integer neededCar, LocalDate preferredStart, LocalDate preferredEnd) {
        this.userId = userId;
        this.neededCar = neededCar;
        this.preferredStart = preferredStart;
        this.preferredEnd = preferredEnd;
        this.status = ReservationStatus.REGISTERED;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() { return userId; }

    public String getUserEmail() { return userEmail; }

    public Integer getNeededCar() {
        return neededCar;
    }

    public Integer getPrice() { return price; }

    public void setPreferredStart(LocalDate preferredStart) {
        this.preferredStart = preferredStart;
    }

    public void setPreferredEnd(LocalDate preferredEnd) {
        this.preferredEnd = preferredEnd;
    }

    public LocalDate getPreferredStart() {
        return preferredStart;
    }

    public LocalDate getPreferredEnd() {
        return preferredEnd;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void validatingUser() { this.status = ReservationStatus.VALIDATING_USER; }

    public void userValid(String email) {
        this.status = ReservationStatus.VALIDATING_CAR;
        this.isValidUserId = true;
        this.userEmail = email;
    }

    public void userInvalid() {
        this.status = ReservationStatus.NO_USER;
        this.isValidUserId= false;
    }

    public void carSelected() {
        this.status = ReservationStatus.BOOKING_CAR;

    }

    public void setPrice(Integer price) {
        this.price = price;
        this.status = ReservationStatus.FINISHED;
    }

    public void noCarsFound() {
        this.status = ReservationStatus.NO_CAR;
    }

    public void finalizeReservation() {
        this.status = ReservationStatus.REQUEST_REGISTERED;
    }

    public void doubleBooking() {
        this.status = ReservationStatus.DOUBLE_BOOKING;
    }

    public void getPriceCar() {
        this.status = ReservationStatus.GET_PRICE_CAR;
    }

    public void calculateBill() {
        this.status = ReservationStatus.CALCULATE_BILL;
    }

    public void accept() {
        this.status = ReservationStatus.ACCEPTED;
    }

    public void decline() {
        this.status = ReservationStatus.DECLINED;
    }
}
