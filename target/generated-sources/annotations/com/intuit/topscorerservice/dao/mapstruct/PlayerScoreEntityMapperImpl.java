package com.intuit.topscorerservice.dao.mapstruct;

import com.intuit.topscorerservice.dao.entities.PlayerScoreEntity;
import com.intuit.topscorerservice.dto.PlayerScoreRequest;
import com.intuit.topscorerservice.dto.PlayerScoreResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-07T22:44:20+0530",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 21.0.3 (Amazon.com Inc.)"
)
public class PlayerScoreEntityMapperImpl implements PlayerScoreEntityMapper {

    @Override
    public PlayerScoreResponse toDto(PlayerScoreEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PlayerScoreResponse.PlayerScoreResponseBuilder playerScoreResponse = PlayerScoreResponse.builder();

        playerScoreResponse.score( entity.getScore() );
        playerScoreResponse.playerId( entity.getPlayerId() );

        return playerScoreResponse.build();
    }

    @Override
    public List<PlayerScoreResponse> toDto(List<PlayerScoreEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<PlayerScoreResponse> list = new ArrayList<PlayerScoreResponse>( entity.size() );
        for ( PlayerScoreEntity playerScoreEntity : entity ) {
            list.add( toDto( playerScoreEntity ) );
        }

        return list;
    }

    @Override
    public PlayerScoreEntity toEntity(PlayerScoreRequest request) {
        if ( request == null ) {
            return null;
        }

        PlayerScoreEntity.PlayerScoreEntityBuilder playerScoreEntity = PlayerScoreEntity.builder();

        playerScoreEntity.score( request.score() );
        playerScoreEntity.playerId( request.playerId() );
        playerScoreEntity.gameId( request.gameId() );

        return playerScoreEntity.build();
    }

    @Override
    public List<PlayerScoreEntity> toEntity(List<PlayerScoreRequest> requests) {
        if ( requests == null ) {
            return null;
        }

        List<PlayerScoreEntity> list = new ArrayList<PlayerScoreEntity>( requests.size() );
        for ( PlayerScoreRequest playerScoreRequest : requests ) {
            list.add( toEntity( playerScoreRequest ) );
        }

        return list;
    }
}
