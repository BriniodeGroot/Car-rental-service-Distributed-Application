package be.ucll.da.userservice.domain;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Faker faker = new Faker();

    public User validateUser(Integer id) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        System.out.println("user id is " + id);

        //String firstName = "Nick";
        //String lastName = "Hayen";

        String email = firstName + "." + lastName + "@google.com";

        double randomNumber = Math.random();
        System.out.println("het random nummer is: " + randomNumber);
        if (randomNumber > 0.3) {
            return new User(id, firstName, lastName, email, true);
        } else {
            return new User(id, firstName, lastName, email, false);
        }
    }
}
