package ua.tennis.service;

import org.springframework.stereotype.Component;
import ua.tennis.service.dto.MatchDTO;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchCache {

    private Map<Long, MatchDTO> matches = new ConcurrentHashMap<>();

    public void addToCache(Long matchId, MatchDTO matchDTO) {
        matches.putIfAbsent(matchId, matchDTO);
    }

    public Optional<MatchDTO> getCachedMatch(Long matchId) {
        return Optional.ofNullable(matches.get(matchId));
    }

    public Map<Long, MatchDTO> getMatches() {
        return matches;
    }

    public void deleteFromCache(Long matchId){
        matches.remove(matchId);
    }
}
