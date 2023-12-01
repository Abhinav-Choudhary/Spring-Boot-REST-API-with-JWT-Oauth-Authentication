package com.northeastern.INFO7255.INFO7255AbhinavChoudhary;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.Queue;
// import org.springframework.amqp.core.BindingBuilder;
// import org.springframework.amqp.core.TopicExchange;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
// import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

// import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service.IndexingListener;

@SpringBootApplication
@ComponentScan(basePackages = "com.northeastern.INFO7255.INFO7255AbhinavChoudhary.*")
public class Info7255AbhinavChoudharyApplication {

	public static final String topicExchangeName = "spring-boot-exchange";

    public static final String queueName = "indexing-queue";

	// @Bean
    // Queue queue() {
    //     return new Queue(queueName, false);
    // }

    // @Bean
    // TopicExchange exchange() {
    //     return new TopicExchange(topicExchangeName);
    // }

    // @Bean
    // Binding binding(Queue queue, TopicExchange exchange) {
    //     return BindingBuilder.bind(queue).to(exchange).with(queueName);
    // }

    // @Bean
    // MessageListenerAdapter listenerAdapter(IndexingListener receiver) {
    //     return new MessageListenerAdapter(receiver, "receiveMessage");
    // }


    // @Bean
    // SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
    //                                          MessageListenerAdapter listenerAdapter) {
    //     SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    //     container.setConnectionFactory(connectionFactory);
    //     container.setQueueNames(queueName);
    //     container.setMessageListener(listenerAdapter);
    //     return container;
    // }

    public static void main(String[] args) {
		SpringApplication.run(Info7255AbhinavChoudharyApplication.class, args);
	}

}
