package be.ucll.da.reservationservice.domain.reservation;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;

    boolean isValidUserId;

    private String userEmail;

    private Long neededCar;

    private String userFirstName;

    private String userLastName;

    private LocalDate preferredStart;

    private LocalDate preferredEnd;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    //protected Reservation(Integer ownerId, Long neededCar, String userFirstName, String userLastName, @Valid LocalDate preferredStart, @Valid LocalDate preferredEnd) {}

    public Reservation(Long userId, Long neededCar, String userFirstName, String userLastName, LocalDate preferredStart, LocalDate preferredEnd) {
        this.userId = userId;
        this.neededCar = neededCar;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.preferredStart = preferredStart;
        this.preferredEnd = preferredEnd;
        this.status = ReservationStatus.REGISTERED;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() { return userId; }

    public String getUserEmail() { return userEmail; }

    public Long getNeededCar() {
        return neededCar;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
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

    public void carSelected(Long selectedCar) {
        this.status = ReservationStatus.BOOKING_CAR;
        this.neededCar = selectedCar;
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

    public void accept() {
        this.status = ReservationStatus.ACCEPTED;
    }

    public void decline() {
        this.status = ReservationStatus.DECLINED;
    }
}