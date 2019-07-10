package ua.tennis.service;

import org.springframework.stereotype.Service;
import ua.tennis.domain.Game;
import ua.tennis.domain.Sett;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProbabilityService {

    public List<Sett> getGameProbabilities(double homeOdds, double awayOdds, byte numberOfSets){
        List<Sett> result = new ArrayList<>();
        Game game = new Game().awayProbability(1.0).homeProbability(0.7);
        Sett set = new Sett().addGame(game).awayScore(1).homeScore(2).awayProbability(0.5)
            .homeProbability(0.33);

        result.add(set);
        return result;
    }
}
