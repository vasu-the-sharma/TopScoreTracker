package com.intuit.topscorerservice.service.impl;

import com.intuit.topscorerservice.dao.entities.PlayerScoreEntity;
import com.intuit.topscorerservice.dao.mapstruct.PlayerScoreEntityMapper;
import com.intuit.topscorerservice.dao.repository.PlayerScoreRepository;
import com.intuit.topscorerservice.dto.PlayerScoreRequest;
import com.intuit.topscorerservice.dto.PlayerScoreResponse;
import com.intuit.topscorerservice.service.PlayerScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PlayerScoreServiceImpl implements PlayerScoreService {

    private final PlayerScoreEntityMapper mapper = PlayerScoreEntityMapper.mapper;

    private final int topK;

    private final PlayerScoreRepository scoreRepository;
    private final CacheHandler cacheHandler;

    public PlayerScoreServiceImpl(@Value("${topK.count:5}") int topK, PlayerScoreRepository scoreRepository, CacheHandler cacheHandler) {
        this.topK = topK;
        this.scoreRepository = scoreRepository;
        this.cacheHandler = cacheHandler;
    }

    @Override
    public List<PlayerScoreResponse> getTopK(String gameId) {
        log.info("Getting top {} scores", topK);
        try {
            List<PlayerScoreResponse> cacheData = cacheHandler.getSortedSetRange(gameId);
            if (cacheData == null || cacheData.isEmpty()) {
                var data = scoreRepository.getTopKScore(topK);
                cacheHandler.addAll(data);

                return mapper.toDto(data);
            }
            log.info("Returning data from cache");
            return cacheData;
        } catch (Exception e) {
            log.error("Error getting topK", e);
            throw e;
        }
    }


    @Override
    @Transactional
    public void save(PlayerScoreEntity entity) {
        log.info("Saving score");
        scoreRepository.saveScore(entity);
        log.info("Score saved");
    }

    @Transactional
    @Override
    public PlayerScoreResponse saveScore(PlayerScoreRequest request) {
        PlayerScoreEntity playerScore = scoreRepository.getPlayerScore(request.playerId(), request.gameId());

        if (playerScore == null || request.score() > playerScore.getScore()) {
            PlayerScoreEntity entity = mapper.toEntity(request);
            save(entity);
            cacheHandler.add(entity);
            return mapper.toDto(entity);
        }

        return mapper.toDto(playerScore);
    }
}
