package com.intuit.topscorerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Builder
public record PlayerScoreRequest(
        @NotBlank String playerId,
        @NotBlank String gameId,
        long score
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 94321242319L;

}
