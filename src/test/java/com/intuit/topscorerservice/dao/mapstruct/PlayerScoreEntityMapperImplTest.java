package com.intuit.topscorerservice.dao.mapstruct;

import com.intuit.topscorerservice.dao.entities.PlayerScoreEntity;
import com.intuit.topscorerservice.dao.mapstruct.PlayerScoreEntityMapperImpl;
import com.intuit.topscorerservice.dto.PlayerScoreRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
public class PlayerScoreEntityMapperImplTest {

    private PlayerScoreEntityMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new PlayerScoreEntityMapperImpl();
    }

    @Test
    void shouldConvertListOfRequestsToEntitiesSuccessfully() {
        List<PlayerScoreRequest> requests = Arrays.asList(
                new PlayerScoreRequest("player1", "game1", 100),
                new PlayerScoreRequest("player2", "game2", 200)
        );

        List<PlayerScoreEntity> entities = mapper.toEntity(requests);

        assertEquals(2, entities.size());
        assertEquals("player1", entities.get(0).getPlayerId());
        assertEquals(100, entities.get(0).getScore());
        assertEquals("player2", entities.get(1).getPlayerId());
        assertEquals(200, entities.get(1).getScore());
    }

    @Test
    void shouldReturnEmptyListWhenRequestsListIsEmpty() {
        List<PlayerScoreEntity> entities = mapper.toEntity(Collections.emptyList());

        assertEquals(0, entities.size());
    }
}