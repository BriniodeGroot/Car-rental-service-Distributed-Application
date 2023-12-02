package be.ucll.da.apigateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
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
                new FanoutExchange("x.car-created"),
                new Queue("q.car-created.api-gateway"),
                new Binding("q.car-created.api-gateway", Binding.DestinationType.QUEUE, "x.car-created", "q.car-created.api-gateway", null));
    }

    @Bean
    public Declarables createSwitchAvailabilityExchange(){
        return new Declarables(
                new FanoutExchange("x.car-switch"),
                new Queue("q.availability-switched.api-gateway"),
                new Binding("q.availability-switched.api-gateway", Binding.DestinationType.QUEUE, "x.car-switch", "q.availability-switched.api-gateway", null));
    }

    @Bean
    public Declarables createReservationFinalizedExchange(){
        return new Declarables(
                new FanoutExchange("x.reservation-finalized"),
                new Queue("q.reservation-finalized.api-gateway" ),
                new Binding("q.reservation-finalized.api-gateway", Binding.DestinationType.QUEUE, "x.reservation-finalized", "q.reservation-finalized.api-gateway", null));
    }

    @Bean
    public Declarables creatDeleteCarQueue(){
        return new Declarables(new Queue("q.car-service.delete-car"));
    }




}
