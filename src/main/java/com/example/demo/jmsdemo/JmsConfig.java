//package com.example.demo.jmsdemo;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.annotation.EnableJms;
//import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
//import org.springframework.jms.core.JmsTemplate;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableJms
//public class JmsConfig {
//    @Value("${jms.broker.url:vm://embedded}")
//    private String brokerUrl;
//
//    @Value("${jms.queue.name}")
//    private String queueName;
//
//    @Bean
//    public ActiveMQConnectionFactory connectionFactory() {
//        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
//        factory.setTrustedPackages(Arrays.asList("com.example.demo", "java.util"));
//        return factory;
//    }
//
//    @Bean
//    public JmsTemplate jmsTemplate() {
//        JmsTemplate template = new JmsTemplate(connectionFactory());
//        template.setDefaultDestinationName(queueName);
//        return template;
//    }
//
//    @Bean
//    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory());
//        factory.setConcurrency("1-5");
//        return factory;
//    }
//}
//
