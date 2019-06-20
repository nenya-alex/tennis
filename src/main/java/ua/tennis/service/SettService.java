package ua.tennis.service;

import ua.tennis.domain.Sett;
import ua.tennis.repository.SettRepository;
import ua.tennis.service.dto.SettDTO;
import ua.tennis.service.mapper.SettMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Sett.
 */
@Service
@Transactional
public class SettService {

    private final Logger log = LoggerFactory.getLogger(SettService.class);

    private final SettRepository settRepository;

    private final SettMapper settMapper;

    public SettService(SettRepository settRepository, SettMapper settMapper) {
        this.settRepository = settRepository;
        this.settMapper = settMapper;
    }

    /**
     * Save a sett.
     *
     * @param settDTO the entity to save
     * @return the persisted entity
     */
    public SettDTO save(SettDTO settDTO) {
        log.debug("Request to save Sett : {}", settDTO);
        Sett sett = settMapper.toEntity(settDTO);
        sett = settRepository.save(sett);
        return settMapper.toDto(sett);
    }

    /**
     * Get all the setts.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<SettDTO> findAll() {
        log.debug("Request to get all Setts");
        return settRepository.findAll().stream()
            .map(settMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one sett by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public SettDTO findOne(Long id) {
        log.debug("Request to get Sett : {}", id);
        Sett sett = settRepository.findOne(id);
        return settMapper.toDto(sett);
    }

    /**
     * Delete the sett by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Sett : {}", id);
        settRepository.delete(id);
    }
}
