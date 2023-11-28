package be.ucll.da.userservice.domain;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Faker faker = new Faker();

    public User validateOwner(Integer id) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        String email = firstName + "." + lastName + "@google.com";

        if (Math.random() > 0.3) {
            return new User(id, firstName, lastName, email, true);
        } else {
            return new User(id, firstName, lastName, email, false);
        }
    }
}
