package ua.tennis.service;

import org.springframework.stereotype.Service;
import ua.tennis.domain.Game;
import ua.tennis.domain.Sett;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProbabilityService {

    public List<Sett> getGameProbabilities(double homeOdds, double awayOdds, byte numberOfSets){
        List<Sett> result = new ArrayList<>();
        Game game = new Game().probabilityAway(BigDecimal.ONE).probabilityHome(BigDecimal.TEN);
        Sett set = new Sett().addGame(game).awayScore(1).homeScore(2).probabilityAway(BigDecimal.valueOf(11))
            .probabilityHome(BigDecimal.valueOf(13));
        result.add(set);
        return result;
    }
}
