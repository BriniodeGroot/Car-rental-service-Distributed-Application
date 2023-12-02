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
    public Declarables createReserveCarQueue(){
        return new Declarables(new Queue("q.car-service.reserve-car"));
    }

    @Bean
    public Declarables createGetPriceCarQueue(){
        return new Declarables(new Queue("q.car-service.getprice-car"));
    }

    @Bean
    public Declarables createCreateBillQueue(){
        return new Declarables(new Queue("q.bill-service.calculate-bill"));
    }

    @Bean
    public Declarables createCreateCarQueue(){
        return new Declarables(new Queue("q.car-service.create-car"));
    }


    @Bean
    public Declarables createSwitchAvailabilityQueue(){
        return new Declarables(new Queue("q.car-service.switch-availability"));
    }

    @Bean
    public Declarables createCreateReservationQueue(){
        return new Declarables(new Queue("q.reservation-service.create-reservation"));
    }

    @Bean
    public Declarables creatFinalizeReservationQueue(){
        return new Declarables(new Queue("q.reservation-service.finalize-reservation"));
    }

    @Bean
    public Declarables createUserValidatedExchange(){
        return new Declarables(
                new FanoutExchange("x.user-validated"),
                new Queue("q.user-validated.reservation-service" ),
                new Queue("q.user-validated.api-gateway"),
                new Binding("q.user-validated.reservation-service", Binding.DestinationType.QUEUE, "x.user-validated", "user-validated.reservation-service", null),
                new Binding("q.user-validated.api-gateway", Binding.DestinationType.QUEUE, "x.user-validated", "q.user-validated.api-gateway", null));
    }


    @Bean
    public Declarables createCarReservedExchange(){
        return new Declarables(
                new FanoutExchange("x.car-reserved"),
                new Queue("q.car-reserved.reservation-service" ),
                new Queue("q.car-reserved.api-gateway"),
                new Binding("q.car-reserved.reservation-service", Binding.DestinationType.QUEUE, "x.car-reserved", "q.car-reserved.reservation-service", null),
                new Binding("q.car-reserved.api-gateway", Binding.DestinationType.QUEUE, "x.car-reserved", "q.car-reserved.api-gateway", null));

    }


    @Bean
    public Declarables createPriceCarExchange(){
        return new Declarables(
                new FanoutExchange("x.car-price"),
                new Queue("q.car-priced.reservation-service" ),
                new Queue("q.car-priced.api-gateway"),
                new Binding("q.car-priced.reservation-service", Binding.DestinationType.QUEUE, "x.car-price", "q.car-priced.reservation-service", null),
                new Binding("q.car-priced.api-gateway", Binding.DestinationType.QUEUE, "x.car-price", "q.car-priced.api-gateway", null));
    }


    @Bean
    public Declarables createBillExchange(){
        return new Declarables(
                new FanoutExchange("x.bill-calculated"),
                new Queue("q.bill-calculated.reservation-service" ),
                new Queue("q.bill-created.api-gateway"),
                new Binding("q.bill-calculated.reservation-service", Binding.DestinationType.QUEUE, "x.bill-calculated", "q.car-priced.reservation-service", null),
                new Binding("q.bill-created.api-gateway", Binding.DestinationType.QUEUE, "x.bill-calculated", "q.bill-created.api-gateway", null));
    }

    @Bean
    public Declarables createReservationFinalizedExchange(){
        return new Declarables(
                new FanoutExchange("x.reservation-finalized"),
                new Queue("q.reservation-finalized.api-gateway" ),
                new Binding("q.reservation-finalized.api-gateway", Binding.DestinationType.QUEUE, "x.reservation-finalized", "q.reservation-finalized.api-gateway", null));
    }

    @Bean
    public Declarables createSendEmailQueue(){
        return new Declarables(new Queue("q.notification-service.send-email"));
    }

    @Bean
    public Declarables creatDeleteCarQueue(){
        return new Declarables(new Queue("q.car-service.delete-car"));
    }

}
