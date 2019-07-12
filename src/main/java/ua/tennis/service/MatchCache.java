package ua.tennis.service;

import org.springframework.stereotype.Component;
import ua.tennis.service.dto.SettDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchCache {

    private Map<Long, List<SettDTO>> matches = new ConcurrentHashMap<>();

    public void addToCache(Long matchId, List<SettDTO> sets) {
        matches.putIfAbsent(matchId, sets);
    }

    public List<SettDTO> getCachedMatch(Long matchId) {
        return matches.get(matchId);
    }

    public Map<Long, List<SettDTO>> getMatches() {
        return matches;
    }
}
