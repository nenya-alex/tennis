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

    @Scheduled(cron = "0 0/1 * * * *") //every minute
//    @Scheduled(fixedDelay = 30000)
    public void getUpcomingMatches() {
        scheduledService.saveUpcomingMatches();
    }

    //    @Scheduled(cron = "0 0/1 * * * *") //every minute
    @Scheduled(fixedDelay = 2000) //every 2 seconds
    public void getLiveMatches() {
        scheduledService.saveLiveMatches();
    }

    @Scheduled(cron = "0 0/59 * * * *") //every 59 minutes
    public void prepareMatchesToFinish() {
        scheduledService.prepareMatchesToFinish();
    }

    @Scheduled(cron = "0 0 0/1 * * *") //every hour
    public void finishMatchesAndSettleBets() {
        scheduledService.finishMatchesAndSettleBets();
    }
}
