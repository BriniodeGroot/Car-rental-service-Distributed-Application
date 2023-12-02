package be.ucll.da.apigateway.cqrs;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Car {


    @Id
    private Integer id;
    private String owner;

    private String model;

    private String type;

    private String location;

    private Integer price;

    private Integer seats;

    private boolean available;

    public Car(Integer id ,String owner, String model, String type, String location, Integer price, Integer seats, boolean available) {
        this.id = id;
        this.owner = owner;
        this.model = model;
        this.type = type;
        this.location = location;
        this.price = price;
        this.seats = seats;
        this.available = available;
    }

    public Car() {

    }

    public Integer getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public Integer getPrice() {
        return price;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getSeats() {
        return seats;
    }

    public boolean getAvailable() { return available; }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
