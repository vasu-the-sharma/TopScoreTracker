package com.intuit.topscorerservice.service.impl;

import com.intuit.topscorerservice.dao.entities.PlayerScoreEntity;
import com.intuit.topscorerservice.dto.PlayerScoreResponse;
import com.intuit.topscorerservice.exception.CacheUpdateFailureException;
import com.intuit.topscorerservice.util.exception.codes.CacheExceptionCodes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private int topK;
    public static final String KEY = "playerScore:";

    @Value("${topK.count:5}")
    public void setTopK(int topK) {
        this.topK = topK;
    }

    public void add(String value, double score) {
        redisTemplate.opsForZSet().add(KEY, value, score);
    }

    public void add(PlayerScoreEntity scoreEntity) {
        log.info("Cache updated for {} with {} for game {}", scoreEntity.getPlayerId(), scoreEntity.getScore(), scoreEntity.getGameId());
        try {
            redisTemplate.opsForZSet().add(getKey(scoreEntity.getGameId()), scoreEntity.getPlayerId(), scoreEntity.getScore());
        } catch (Exception e) {
            log.error("Failed to update cache - {}", e.getMessage());
            throw new CacheUpdateFailureException(CacheExceptionCodes.CACHE_UPDATE_ERROR);
        }
    }

    public void addAll(List<PlayerScoreEntity> scoreEntities) {
        scoreEntities.forEach(this::add);
    }

    public List<PlayerScoreResponse> getSortedSetRange(String gameId) {
        String cacheKey = getKey(gameId);
        try {
            long totalElements = redisTemplate.opsForZSet().size(cacheKey);
            log.info("Cache has data size of {} for key {}", totalElements, cacheKey);
            Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(cacheKey, 0, topK - 1);

            if (typedTuples != null && !typedTuples.isEmpty()) {
                return typedTuples.stream()
                        .map(e -> PlayerScoreResponse.builder().playerId(e.getValue()).score(Objects.requireNonNull(e.getScore()).longValue()).build())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Something wrong with the Cache", e);
        }
        return null;
    }

    private String getKey(String gameId) {
        return KEY + gameId;
    }

}
