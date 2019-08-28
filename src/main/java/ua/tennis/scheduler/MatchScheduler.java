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
    public void getUpcomingMatches() {
        scheduledService.saveUpcomingMatches();
    }

    @Scheduled(fixedDelay = 2000) //every 2 seconds
    public void getLiveMatches() {
        scheduledService.saveLiveMatches();
    }

    @Scheduled(cron = "0 0/29 * * * *") //every 29 minutes
    public void prepareMatchesToFinish() {
        scheduledService.prepareMatchesToFinish();
    }

    @Scheduled(cron = "0 0/30 * * * *") //every 30 minutes
    public void finishMatchesAndSettleBets() {
        scheduledService.finishMatchesAndSettleBets();
    }

    //    @Scheduled(cron = "0 0 0/3 * * *") //every 3 hours
    @Scheduled(cron = "0 0/1 * * * *") //every 10 minutes
    public void sendEmail() {
        scheduledService.sendEmail();
    }

}
