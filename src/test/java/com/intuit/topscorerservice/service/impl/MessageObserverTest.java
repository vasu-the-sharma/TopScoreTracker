package com.intuit.topscorerservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.topscorerservice.dto.PlayerScoreRequest;
import com.intuit.topscorerservice.service.PlayerScoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


@Testcontainers
@SpringBootTest
class MessageObserverTest {

    @MockBean
    private PlayerScoreService playerScoreService;

    @Autowired
    private MessageObserver messageObserver;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management");

    static PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("root")
            .withPassword("password")
            .withDatabaseName("craft_demo");

    private static GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:6.2.6")
            .withAccessToHost(true)
            .withExposedPorts(6379);

    static {
        REDIS_CONTAINER.start();
        POSTGRESQL_CONTAINER.start();
        rabbitMQContainer.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);

        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessValidPlayerScoreRequest() throws Exception {
        byte[] payload = objectMapper.writeValueAsBytes(new PlayerScoreRequest("player1", "game1", 100));
        messageObserver.playerScoreRequestObserver(payload);
        verify(playerScoreService).saveScore(any(PlayerScoreRequest.class));
    }

    @Test
    void shouldHandleInvalidPayloadGracefully() {
        byte[] invalidPayload = "invalid json".getBytes();
        messageObserver.playerScoreRequestObserver(invalidPayload);
        // Expecting no exception to be thrown, hence no assertion is needed here
    }

    @Test
    void shouldConvertAndSendPlayerScoreRequest() throws InterruptedException {
        PlayerScoreRequest request = new PlayerScoreRequest("player2", "game2", 150);
        rabbitTemplate.convertAndSend(MessageObserver.PLAYER_REQUEST_QUEUE, request);

        // Wait a bit for the message to be consumed and processed
        Thread.sleep(1000);

        verify(playerScoreService, timeout(1000)).saveScore(any(PlayerScoreRequest.class));
    }

    @Test
    void listenerAdapterShouldUseCorrectMethod() throws Exception {
        MessageListenerAdapter adapter = messageObserver.listenerAdapter(messageObserver);
        Method getDefaultListenerMethod = MessageListenerAdapter.class.getDeclaredMethod("getDefaultListenerMethod");
        getDefaultListenerMethod.setAccessible(true);
        String methodName = (String) getDefaultListenerMethod.invoke(adapter);
        assertEquals("playerScoreRequestObserver", methodName);
    }

    @Test
    void testQueueCreation() {
        Queue queue = messageObserver.queue();
        assertNotNull(queue);
        assertEquals(MessageObserver.PLAYER_REQUEST_QUEUE, queue.getName());
        assertFalse(queue.isDurable());
    }

    @Test
    void testRabbitTemplateCreation() {
        RabbitTemplate rabbitTemplate = messageObserver.rabbitTemplate(connectionFactory, new Jackson2JsonMessageConverter());
        assertNotNull(rabbitTemplate);
        assertEquals(Jackson2JsonMessageConverter.class, rabbitTemplate.getMessageConverter().getClass());
    }

    @Test
    void testJsonMessageConverterCreation() {
        Jackson2JsonMessageConverter converter = messageObserver.jsonMessageConverter();
        assertNotNull(converter);
    }

    @Test
    void testContainerCreation() {
        MessageListenerAdapter listenerAdapter = messageObserver.listenerAdapter(messageObserver);
        SimpleMessageListenerContainer container = messageObserver.container(connectionFactory, listenerAdapter);
        assertNotNull(container);
        assertEquals(connectionFactory, container.getConnectionFactory());
        assertArrayEquals(new String[]{MessageObserver.PLAYER_REQUEST_QUEUE}, container.getQueueNames());
        assertEquals(listenerAdapter, container.getMessageListener());
    }
}