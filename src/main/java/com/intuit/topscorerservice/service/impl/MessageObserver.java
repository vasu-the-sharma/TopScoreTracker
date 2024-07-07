package com.intuit.topscorerservice.service.impl;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.topscorerservice.dto.PlayerScoreRequest;
import com.intuit.topscorerservice.service.PlayerScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public final class MessageObserver {

    public static final String PLAYER_SCORE_REQUEST_CHANNEL = "playerScoreRequestChannel";
    public static final String SINK = "sink";
    public static final String PLAYER_REQUEST_QUEUE = "player_request_queue";

    private final PlayerScoreService playerScoreService;

    public MessageObserver(PlayerScoreService playerScoreService) {
        this.playerScoreService = playerScoreService;
    }
    @Bean
    public Queue queue() {
        return new Queue(PLAYER_REQUEST_QUEUE, false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jsonMessageConverter) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PLAYER_REQUEST_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(MessageObserver receiver) {
        return new MessageListenerAdapter(receiver, "playerScoreRequestObserver");
    }

    public void playerScoreRequestObserver(byte[] payload) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            var data = mapper.readValue(payload, PlayerScoreRequest.class);

            playerScoreService.saveScore(data);
        } catch (Exception e) {
            log.error("Error while processing message", e);
        }

    }

}
