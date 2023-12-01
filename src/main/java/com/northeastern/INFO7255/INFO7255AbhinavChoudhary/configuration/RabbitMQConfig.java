// package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.configuration;


// import org.springframework.amqp.core.TopicExchange;
// import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
// import org.springframework.context.annotation.Bean;
// // import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
// // import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// // import org.springframework.amqp.rabbit.core.RabbitTemplate;
// // import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service.IndexingListener;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.Queue;
// import org.springframework.amqp.core.BindingBuilder;
// // import org.springframework.amqp.core.TopicExchange;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// // import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
// import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

// @Configuration
// public class RabbitMQConfig {

// //     @Bean
// //     public ConnectionFactory connectionFactory()
// // {
// //         ConnectionFactory factory = new CachingConnectionFactory();
// //         System.out.println(factory.getUsername());
// //         // Configure connection factory properties here (e.g., hostname, port, credentials)
// //         return factory;
//     // }
    
//     // @Bean
//     // public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//     //     return new RabbitTemplate(connectionFactory);
//     // }

//     public static final String topicExchangeName = "spring-boot-exchange";
//     public static final String queueName = "indexing-queue";

//     @Bean
//     Queue queue() {
//         return new Queue(queueName, false);
//     }

//     @Bean
//     TopicExchange exchange() {
//         return new TopicExchange(topicExchangeName);
//     }

//     @Bean
//     Binding binding(Queue queue, TopicExchange exchange) {
//         return BindingBuilder.bind(queue).to(exchange).with(queueName);
//     }

//     @Bean
//     MessageListenerAdapter listenerAdapter(IndexingListener receiver) {
//         return new MessageListenerAdapter(receiver, "receiveMessage");
//     }


//     // @Bean
//     // SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//     //                                          MessageListenerAdapter listenerAdapter) {
//     //     SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//     //     container.setConnectionFactory(connectionFactory);
//     //     container.setQueueNames(queueName);
//     //     container.setMessageListener(listenerAdapter);
//     //     return container;
//     // }
// }
