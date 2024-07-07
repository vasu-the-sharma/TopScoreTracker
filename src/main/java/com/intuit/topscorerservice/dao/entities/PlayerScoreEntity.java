package com.intuit.topscorerservice.dao.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;

import java.time.OffsetDateTime;

@Builder
@Getter
@Value
@JsonDeserialize(builder = PlayerScoreEntity.PlayerScoreEntityBuilder.class)
public class PlayerScoreEntity {


    @NotNull(message = "ID must not be null")
    @Min(value = 1, message = "ID must be greater than 0")
    long id;

    @NotNull(message = "Score must not be null")
    @Min(value = 0, message = "Score must be non-negative")
    long score;

    @NotBlank(message = "Player ID must not be blank")
    String playerId;

    @NotBlank(message = "Game ID must not be blank")
    String gameId;

    @NotNull(message = "Date created must not be null")
    OffsetDateTime dateCreated;

    @NotNull(message = "Date updated must not be null")
    OffsetDateTime dateUpdated;
}
