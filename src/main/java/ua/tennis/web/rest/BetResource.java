package ua.tennis.web.rest;

import ua.tennis.service.BetService;
import ua.tennis.web.rest.errors.BadRequestAlertException;
import ua.tennis.service.dto.BetDTO;

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
 * REST controller for managing {@link ua.tennis.domain.Bet}.
 */
@RestController
@RequestMapping("/api")
public class BetResource {

    private final Logger log = LoggerFactory.getLogger(BetResource.class);

    private static final String ENTITY_NAME = "bet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BetService betService;

    public BetResource(BetService betService) {
        this.betService = betService;
    }

    /**
     * {@code POST  /bets} : Create a new bet.
     *
     * @param betDTO the betDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new betDTO, or with status {@code 400 (Bad Request)} if the bet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bets")
    public ResponseEntity<BetDTO> createBet(@RequestBody BetDTO betDTO) throws URISyntaxException {
        log.debug("REST request to save Bet : {}", betDTO);
        if (betDTO.getId() != null) {
            throw new BadRequestAlertException("A new bet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BetDTO result = betService.save(betDTO);
        return ResponseEntity.created(new URI("/api/bets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bets} : Updates an existing bet.
     *
     * @param betDTO the betDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated betDTO,
     * or with status {@code 400 (Bad Request)} if the betDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the betDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bets")
    public ResponseEntity<BetDTO> updateBet(@RequestBody BetDTO betDTO) throws URISyntaxException {
        log.debug("REST request to update Bet : {}", betDTO);
        if (betDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BetDTO result = betService.save(betDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, betDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /bets} : get all the bets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bets in body.
     */
    @GetMapping("/bets")
    public List<BetDTO> getAllBets() {
        log.debug("REST request to get all Bets");
        return betService.findAll();
    }

    /**
     * {@code GET  /bets/:id} : get the "id" bet.
     *
     * @param id the id of the betDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the betDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bets/{id}")
    public ResponseEntity<BetDTO> getBet(@PathVariable Long id) {
        log.debug("REST request to get Bet : {}", id);
        Optional<BetDTO> betDTO = betService.findOne(id);
        return ResponseUtil.wrapOrNotFound(betDTO);
    }

    /**
     * {@code DELETE  /bets/:id} : delete the "id" bet.
     *
     * @param id the id of the betDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bets/{id}")
    public ResponseEntity<Void> deleteBet(@PathVariable Long id) {
        log.debug("REST request to delete Bet : {}", id);
        betService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
