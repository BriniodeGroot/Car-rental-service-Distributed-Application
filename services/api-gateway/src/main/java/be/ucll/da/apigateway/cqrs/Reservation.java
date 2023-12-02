package be.ucll.da.apigateway.cqrs;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    private int id;
    private int userOfId;

    boolean isValidUserId;

    private String userEmail;

    private int neededCar;

    private int price;

    private LocalDate preferredStart;

    private LocalDate preferredEnd;

    @ManyToOne
    private User user;

    @ManyToOne
    private Car car;


    //protected Reservation(Integer ownerId, Long neededCar, String userFirstName, String userLastName, @Valid LocalDate preferredStart, @Valid LocalDate preferredEnd) {}


    public Reservation() {
    }

    public Reservation(Integer id, Integer userOfId, Integer neededCar, LocalDate preferredStart, LocalDate preferredEnd, Integer price, User user, Car car) {
        this.id = id;
        this.userOfId = userOfId;
        this.neededCar = neededCar;
        this.preferredStart = preferredStart;
        this.preferredEnd = preferredEnd;
        this.price = price;
        this.user = user;
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public Car getCar() {
        return car;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userOfId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getNeededCar() {
        return neededCar;
    }

    public Integer getPrice() {
        return price;
    }

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
}