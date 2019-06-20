package ua.tennis.web.rest.vm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.tennis.domain.Sett;
import ua.tennis.service.ProbabilityService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProbabilityResource {

    private final ProbabilityService probabilityService;

    public ProbabilityResource(ProbabilityService probabilityService) {
        this.probabilityService = probabilityService;
    }

    @GetMapping("/probability/{homeOdds}/{awayOdds}/{numberOfSets}")
    public ResponseEntity<List<Sett>> getGameProbabilities(@PathVariable double homeOdds,
                                                           @PathVariable double awayOdds,
                                                           @PathVariable byte numberOfSets) {

        List<Sett> result = probabilityService.getGameProbabilities(homeOdds, awayOdds, numberOfSets);

        return ResponseEntity.ok(result);
    }
}
