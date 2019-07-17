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

    @Scheduled(cron = "0 0/1 * * * *") //every minutes
    public void getUpcomingMatches() {
        scheduledService.saveUpcomingMatches();
    }

    @Scheduled(cron = "0 0/1 * * * *") //every minutes
    public void getLiveMatches() {
        scheduledService.saveLiveMatches();
    }
}
