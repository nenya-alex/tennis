package ua.tennis.service;

import ua.tennis.domain.Match;
import ua.tennis.repository.MatchRepository;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.mapper.MatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Match.
 */
@Service
@Transactional
public class MatchService {

    private final Logger log = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;

    private final MatchMapper matchMapper;

    public MatchService(MatchRepository matchRepository, MatchMapper matchMapper) {
        this.matchRepository = matchRepository;
        this.matchMapper = matchMapper;
    }

    /**
     * Save a match.
     *
     * @param matchDTO the entity to save
     * @return the persisted entity
     */
    public MatchDTO save(MatchDTO matchDTO) {
        log.debug("Request to save Match : {}", matchDTO);
        Match match = matchMapper.toEntity(matchDTO);
        match = matchRepository.save(match);
        return matchMapper.toDto(match);
    }

    /**
     * Get all the matches.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MatchDTO> findAll() {
        log.debug("Request to get all Matches");
        return matchRepository.findAll().stream()
            .map(matchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one match by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MatchDTO findOne(Long id) {
        log.debug("Request to get Match : {}", id);
        Match match = matchRepository.findOne(id);
        return matchMapper.toDto(match);
    }

    /**
     * Delete the match by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Match : {}", id);
        matchRepository.delete(id);
    }
}
