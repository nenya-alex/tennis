package ua.tennis.web.rest;

import ua.tennis.service.OddsService;
import ua.tennis.web.rest.errors.BadRequestAlertException;
import ua.tennis.service.dto.OddsDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link ua.tennis.domain.Odds}.
 */
@RestController
@RequestMapping("/api")
public class OddsResource {

    private final Logger log = LoggerFactory.getLogger(OddsResource.class);

    private static final String ENTITY_NAME = "odds";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OddsService oddsService;

    public OddsResource(OddsService oddsService) {
        this.oddsService = oddsService;
    }

    /**
     * {@code POST  /odds} : Create a new odds.
     *
     * @param oddsDTO the oddsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oddsDTO, or with status {@code 400 (Bad Request)} if the odds has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/odds")
    public ResponseEntity<OddsDTO> createOdds(@RequestBody OddsDTO oddsDTO) throws URISyntaxException {
        log.debug("REST request to save Odds : {}", oddsDTO);
        if (oddsDTO.getId() != null) {
            throw new BadRequestAlertException("A new odds cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OddsDTO result = oddsService.save(oddsDTO);
        return ResponseEntity.created(new URI("/api/odds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /odds} : Updates an existing odds.
     *
     * @param oddsDTO the oddsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oddsDTO,
     * or with status {@code 400 (Bad Request)} if the oddsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oddsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/odds")
    public ResponseEntity<OddsDTO> updateOdds(@RequestBody OddsDTO oddsDTO) throws URISyntaxException {
        log.debug("REST request to update Odds : {}", oddsDTO);
        if (oddsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OddsDTO result = oddsService.save(oddsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, oddsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /odds} : get all the odds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of odds in body.
     */
    @GetMapping("/odds")
    public List<OddsDTO> getAllOdds() {
        log.debug("REST request to get all Odds");
        return oddsService.findAll();
    }

    /**
     * {@code GET  /odds/:id} : get the "id" odds.
     *
     * @param id the id of the oddsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oddsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/odds/{id}")
    public ResponseEntity<OddsDTO> getOdds(@PathVariable Long id) {
        log.debug("REST request to get Odds : {}", id);
        Optional<OddsDTO> oddsDTO = oddsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oddsDTO);
    }

    /**
     * {@code DELETE  /odds/:id} : delete the "id" odds.
     *
     * @param id the id of the oddsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/odds/{id}")
    public ResponseEntity<Void> deleteOdds(@PathVariable Long id) {
        log.debug("REST request to delete Odds : {}", id);
        oddsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
