package ua.tennis.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.tennis.service.MatchService;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.web.rest.errors.BadRequestAlertException;
import ua.tennis.web.rest.util.HeaderUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Match.
 */
@RestController
@RequestMapping("/api")
public class MatchResource {

    private final Logger log = LoggerFactory.getLogger(MatchResource.class);

    private static final String ENTITY_NAME = "match";

    private final MatchService matchService;

    public MatchResource(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * POST  /matches : Create a new match.
     *
     * @param matchDTO the matchDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new matchDTO, or with status 400 (Bad Request) if the match has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/matches")
    @Timed
    public ResponseEntity<MatchDTO> createMatch(@RequestBody MatchDTO matchDTO) throws URISyntaxException {
        log.debug("REST request to save Match : {}", matchDTO);
        if (matchDTO.getId() != null) {
            throw new BadRequestAlertException("A new match cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MatchDTO result = matchService.save(matchDTO);
        return ResponseEntity.created(new URI("/api/matches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /matches : Updates an existing match.
     *
     * @param matchDTO the matchDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated matchDTO,
     * or with status 400 (Bad Request) if the matchDTO is not valid,
     * or with status 500 (Internal Server Error) if the matchDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/matches")
    @Timed
    public ResponseEntity<MatchDTO> updateMatch(@RequestBody MatchDTO matchDTO) throws URISyntaxException {
        log.debug("REST request to update Match : {}", matchDTO);
        if (matchDTO.getId() == null) {
            return createMatch(matchDTO);
        }
        MatchDTO result = matchService.save(matchDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, matchDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /matches : get all the matches.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of matches in body
     */
    @GetMapping("/matches")
    @Timed
    public List<MatchDTO> getAllMatches() {
        log.debug("REST request to get all Matches");
        return matchService.findAll();
        }

    /**
     * GET  /matches/:id : get the "id" match.
     *
     * @param id the id of the matchDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the matchDTO, or with status 404 (Not Found)
     */
    @GetMapping("/matches/{id}")
    @Timed
    public ResponseEntity<MatchDTO> getMatch(@PathVariable Long id) {
        log.debug("REST request to get Match : {}", id);
        MatchDTO matchDTO = matchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(matchDTO));
    }

    /**
     * DELETE  /matches/:id : delete the "id" match.
     *
     * @param id the id of the matchDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/matches/{id}")
    @Timed
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        log.debug("REST request to delete Match : {}", id);
        matchService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
