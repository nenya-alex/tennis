package ua.tennis.service;

import org.springframework.stereotype.Component;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.dto.SettDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchCache {

    private Map<Long, MatchDTO> matches = new ConcurrentHashMap<>();

    public void addToCache(Long matchId, MatchDTO matchDTO) {
        matches.putIfAbsent(matchId, matchDTO);
    }

    public MatchDTO getCachedMatch(Long matchId) {
        return matches.get(matchId);
    }

    public Map<Long, MatchDTO> getMatches() {
        return matches;
    }

    public void deleteFromCache(Long matchId){
        matches.remove(matchId);
    }
}
