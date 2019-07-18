package ua.tennis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.tennis.domain.Bet;
import ua.tennis.repository.BetRepository;
import ua.tennis.service.dto.BetDTO;
import ua.tennis.service.mapper.BetMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Bet}.
 */
@Service
@Transactional
public class BetService {

    private final Logger log = LoggerFactory.getLogger(BetService.class);

    private final BetRepository betRepository;

    private final BetMapper betMapper;

    public BetService(BetRepository betRepository, BetMapper betMapper) {
        this.betRepository = betRepository;
        this.betMapper = betMapper;
    }

    /**
     * Save a bet.
     *
     * @param betDTO the entity to save.
     * @return the persisted entity.
     */
    public BetDTO save(BetDTO betDTO) {
        log.debug("Request to save Bet : {}", betDTO);
        Bet bet = betMapper.toEntity(betDTO);
        bet = betRepository.save(bet);
        return betMapper.toDto(bet);
    }

    /**
     * Get all the bets.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BetDTO> findAll() {
        log.debug("Request to get all Bets");
        return betRepository.findAll().stream()
            .map(betMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

}
