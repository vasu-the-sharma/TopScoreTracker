package com.intuit.topscorerservice.dao.repository.impl;

import com.intuit.topscorerservice.dao.Queries;
import com.intuit.topscorerservice.dao.entities.PlayerScoreEntity;
import com.intuit.topscorerservice.dao.mapper.PlayerScoreDaoMapper;
import com.intuit.topscorerservice.dao.repository.PlayerScoreRepository;
import com.intuit.topscorerservice.exception.DaoServiceException;
import com.intuit.topscorerservice.util.exception.codes.DaoExceptionCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PlayerScoreRepositoryImpl implements PlayerScoreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PlayerScoreDaoMapper mapper;

    @Override
    public List<PlayerScoreEntity> getTopKScore(int k) {
        Assert.isTrue(k > 0, "The number of top scores to retrieve must be greater than zero");
        try {
            return jdbcTemplate.query(Queries.GET_TOP_K, mapper, k);
        } catch (EmptyResultDataAccessException e) {
            log.info("No data found", e);
            return List.of();
        } catch (Exception e) {
            log.error("Exception while getting top scores", e);
            throw new DaoServiceException(DaoExceptionCodes.DAO_ERROR);
        }
    }

    @Override
    public PlayerScoreEntity getPlayerScore(String playerId, String gameId) {
        try {
            Assert.hasText(playerId, "Player ID must not be empty");
            Assert.hasText(gameId, "Game ID must not be empty");
            return jdbcTemplate.queryForObject(Queries.GET_PLAYER_SCORE, mapper, playerId, gameId);
        } catch (EmptyResultDataAccessException e) {
            log.info("No data found");
            return null;
        } catch (Exception e) {
            log.error("Exception while getting player score", e);
            throw new DaoServiceException(DaoExceptionCodes.DAO_ERROR);
        }
    }

    @Override
    public int saveScore(PlayerScoreEntity scoreEntity) {
        Assert.notNull(scoreEntity, "Score entity must not be null");
        Assert.hasText(scoreEntity.getPlayerId(), "Player ID must not be empty");
        Assert.hasText(scoreEntity.getGameId(), "Game ID must not be empty");
        Assert.isTrue(scoreEntity.getScore() >= 0, "Score must not be negative");

        try {
            Integer count = jdbcTemplate.queryForObject(Queries.PLAYER_SCORE_EXIST, Integer.class, scoreEntity.getPlayerId(), scoreEntity.getGameId());
            count = (count == null) ? 0 : count;

            String query = (count < 1) ? Queries.SAVE_SCORE : Queries.UPDATE_SCORE;
            int rowsAffected = jdbcTemplate.update(query, scoreEntity.getScore(), scoreEntity.getPlayerId(), scoreEntity.getGameId());

            if (rowsAffected != 1) {
                String action = (count < 1) ? "creating" : "updating";
                log.error("Expected rows {} to be 1 but was {}", action, rowsAffected);
                throw new DaoServiceException(DaoExceptionCodes.DAO_UPDATE_ERROR);
            }
            return rowsAffected;
        } catch (Exception e) {
            log.error("Exception while saving score", e);
            throw new DaoServiceException(DaoExceptionCodes.DAO_UPDATE_ERROR);
        }
    }
}
