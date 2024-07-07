package com.intuit.topscorerservice.scheduler;

import com.intuit.topscorerservice.dao.entities.PlayerScoreEntity;
import com.intuit.topscorerservice.service.impl.CacheHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class CacheScheduler {
//
//    private final CacheHandler cacheHandler;
//
//    @Scheduled(fixedRateString = "${cache.refresh.interval:60000}")
//    public void updateCache() {
//        log.info("Starting cache update...");
//        try {
//            // Assuming you have a method to fetch all scores from the database
//            List<PlayerScoreEntity> allScores = fetchAllScoresFromDatabase();
//            for (PlayerScoreEntity scoreEntity : allScores) {
//                cacheHandler.add(scoreEntity);
//            }
//            log.info("Cache update completed.");
//        } catch (Exception e) {
//            log.error("Failed to update cache - {}", e.getMessage());
//        }
//    }
//
//    private List<PlayerScoreEntity> fetchAllScoresFromDatabase() {
//        // Implement your logic to fetch scores from the database
//        // For now, returning an empty list for illustration
//        return List.of();
//    }
//}
