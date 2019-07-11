package ua.tennis.service;

import ua.tennis.domain.Odds;
import ua.tennis.repository.OddsRepository;
import ua.tennis.service.dto.OddsDTO;
import ua.tennis.service.mapper.OddsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Odds}.
 */
@Service
@Transactional
public class OddsService {

    private final Logger log = LoggerFactory.getLogger(OddsService.class);

    private final OddsRepository oddsRepository;

    private final OddsMapper oddsMapper;

    public OddsService(OddsRepository oddsRepository, OddsMapper oddsMapper) {
        this.oddsRepository = oddsRepository;
        this.oddsMapper = oddsMapper;
    }

    /**
     * Save a odds.
     *
     * @param oddsDTO the entity to save.
     * @return the persisted entity.
     */
    public OddsDTO save(OddsDTO oddsDTO) {
        log.debug("Request to save Odds : {}", oddsDTO);
        Odds odds = oddsMapper.toEntity(oddsDTO);
        odds = oddsRepository.save(odds);
        return oddsMapper.toDto(odds);
    }

    /**
     * Get all the odds.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OddsDTO> findAll() {
        log.debug("Request to get all Odds");
        return oddsRepository.findAll().stream()
            .map(oddsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one odds by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OddsDTO> findOne(Long id) {
        log.debug("Request to get Odds : {}", id);
        return oddsRepository.findById(id)
            .map(oddsMapper::toDto);
    }

    /**
     * Delete the odds by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Odds : {}", id);
        oddsRepository.deleteById(id);
    }
}
