package ua.tennis.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.tennis.service.ScheduledService;

@Component
public class MatchScheduler {

    private final ScheduledService scheduledService;

    public MatchScheduler(ScheduledService scheduledService) {
        this.scheduledService = scheduledService;
    }

    @Scheduled(fixedRate = 5000)
    public void getUpcomingMatches() {
        scheduledService.saveUpcomingMatches();
    }
}
