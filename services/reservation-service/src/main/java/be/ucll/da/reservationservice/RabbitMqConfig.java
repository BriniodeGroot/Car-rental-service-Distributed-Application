package be.ucll.da.reservationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Jackson2JsonMessageConverter converter() {
        ObjectMapper mapper =
                new ObjectMapper()
                        .registerModule(new ParameterNamesModule())
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());

        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter, CachingConnectionFactory cachingConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public Declarables createValidateUserQueue(){
        return new Declarables(new Queue("q.user-service.validate-user"));
    }

    @Bean
    public Declarables createUserValidatedExchange(){
        return new Declarables(
                new FanoutExchange("x.user-validated"),
                new Queue("q.user-validated.reservation-service" ),
                new Binding("q.user-validated.reservation-service", Binding.DestinationType.QUEUE, "x.user-validated", "user-validated.reservation-service", null));
    }

    @Bean
    public Declarables createReserveCarQueue(){
        return new Declarables(new Queue("q.car-service.reserve-car"));
    }

    @Bean
    public Declarables createCarReservedExchange(){
        return new Declarables(
                new FanoutExchange("x.car-reserved"),
                new Queue("q.car-reserved.reservation-service" ),
                new Binding("q.car-reserved.reservation-service", Binding.DestinationType.QUEUE, "x.car-reserved", "q.car-reserved.reservation-service", null));
    }

//    @Bean
//    public Declarables createValidateDoctorQueue(){
//        return new Declarables(new Queue("q.doctor-service.check-employed-doctors"));
//    }
//
//    @Bean
//    public Declarables createDoctorValidatedExchange(){
//        return new Declarables(
//                new FanoutExchange("x.doctors-employed"),
//                new Queue("q.doctors-employed.appointment-service" ),
//                new Binding("q.doctors-employed.appointment-service", Binding.DestinationType.QUEUE, "x.doctors-employed", "doctors-employed.appointment-service", null));
//    }
//
//    @Bean
//    public Declarables createBookRoomQueue(){
//        return new Declarables(new Queue("q.room-service.book-room"));
//    }
//
//    @Bean
//    public Declarables createRoomBookedExchange(){
//        return new Declarables(
//                new FanoutExchange("x.room-bookings"),
//                new Queue("q.room-bookings.appointment-service" ),
//                new Binding("q.room-bookings.appointment-service", Binding.DestinationType.QUEUE, "x.room-bookings", "room-bookings.appointment-service", null));
//    }
//
//    @Bean
//    public Declarables createReleaseRoomQueue(){
//        return new Declarables(new Queue("q.room-service.release-room"));
//    }
//
//    @Bean
//    public Declarables createRoomReleasedExchange(){
//        return new Declarables(
//                new FanoutExchange("x.room-releases"));
//    }
//
//    @Bean
//    public Declarables createOpenAccountQueue(){
//        return new Declarables(new Queue("q.account-service.open-account"));
//    }
//
//    @Bean
//    public Declarables createAccountOpenedExchange(){
//        return new Declarables(
//                new FanoutExchange("x.account-openings"),
//                new Queue("q.account-openings.appointment-service" ),
//                new Binding("q.account-openings.appointment-service", Binding.DestinationType.QUEUE, "x.account-openings", "account-openings.appointment-service", null));
//    }
//
//    @Bean
//    public Declarables createCloseAccountQueue(){
//        return new Declarables(new Queue("q.account-service.close-account"));
//    }
//
//    @Bean
//    public Declarables createAccountTerminationsExchange(){
//        return new Declarables(
//                new FanoutExchange("x.account-terminations"));
//    }

    @Bean
    public Declarables createSendEmailQueue(){
        return new Declarables(new Queue("q.notification-service.send-email"));
    }

}
