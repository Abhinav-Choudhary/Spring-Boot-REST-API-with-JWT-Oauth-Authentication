// package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.configuration;

// import org.springframework.amqp.core.*;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// //H

// @Configuration
// public class MessagingConfig {
//     public static final java.lang.String MESSAGE_QUEUE_NAME = "medical_plan_indexing_queue";
//     public static final java.lang.String MESSAGE_EXCHANGE_NAME = "medical_plan_exchange";
//     public static final java.lang.String ROUTING_KEY = "medical_plan_routing_key";

//     @Bean
//     public Queue queue() {
//         return new Queue(MESSAGE_QUEUE_NAME);
//     }

//     @Bean
//     public TopicExchange exchange() {
//         return new TopicExchange(MESSAGE_EXCHANGE_NAME);
//     }

//     @Bean
//     public Binding binding(Queue queue, TopicExchange exchange) {
//         return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//     }

//     public Jackson2JsonMessageConverter converter() {
//         return new Jackson2JsonMessageConverter();
//     }

//     public AmqpTemplate template(ConnectionFactory connectionFactory) {
//         RabbitTemplate template = new RabbitTemplate(connectionFactory);
//         template.setMessageConverter(converter());
//         return template;
//     }
// }
