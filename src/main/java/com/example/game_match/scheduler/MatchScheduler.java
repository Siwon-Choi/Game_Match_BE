package com.example.game_match.scheduler;

import com.example.game_match.friendlymatch.service.FriendlyMatchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchScheduler {
    private final FriendlyMatchService friendlyMatchService;

    @PostConstruct
    public void onStartup() {
        friendlyMatchService.updateExpiredMatches();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkExpiredMatches() {
        friendlyMatchService.updateExpiredMatches();
    }
}
