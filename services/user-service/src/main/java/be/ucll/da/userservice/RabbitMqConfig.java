package be.ucll.da.userservice;

import org.springframework.amqp.core.Declarables;
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
        return new Jackson2JsonMessageConverter();
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
    public Declarables creatDeleteCarQueue(){
        return new Declarables(new Queue("q.car-service.delete-car"));
    }
}
