package ua.tennis.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.tennis.service.GameService;
import ua.tennis.service.dto.GameDTO;
import ua.tennis.web.rest.errors.BadRequestAlertException;
import ua.tennis.web.rest.util.HeaderUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Game.
 */
@RestController
@RequestMapping("/api")
public class GameResource {

    private final Logger log = LoggerFactory.getLogger(GameResource.class);

    private static final String ENTITY_NAME = "game";

    private final GameService gameService;

    public GameResource(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * POST  /games : Create a new game.
     *
     * @param gameDTO the gameDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new gameDTO, or with status 400 (Bad Request) if the game has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/games")
    @Timed
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) throws URISyntaxException {
        log.debug("REST request to save Game : {}", gameDTO);
        if (gameDTO.getId() != null) {
            throw new BadRequestAlertException("A new game cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GameDTO result = gameService.save(gameDTO);
        return ResponseEntity.created(new URI("/api/games/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /games : Updates an existing game.
     *
     * @param gameDTO the gameDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated gameDTO,
     * or with status 400 (Bad Request) if the gameDTO is not valid,
     * or with status 500 (Internal Server Error) if the gameDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/games")
    @Timed
    public ResponseEntity<GameDTO> updateGame(@RequestBody GameDTO gameDTO) throws URISyntaxException {
        log.debug("REST request to update Game : {}", gameDTO);
        if (gameDTO.getId() == null) {
            return createGame(gameDTO);
        }
        GameDTO result = gameService.save(gameDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, gameDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /games : get all the games.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of games in body
     */
    @GetMapping("/games")
    @Timed
    public List<GameDTO> getAllGames() {
        log.debug("REST request to get all Games");
        return gameService.findAll();
        }

    /**
     * GET  /games/:id : get the "id" game.
     *
     * @param id the id of the gameDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the gameDTO, or with status 404 (Not Found)
     */
    @GetMapping("/games/{id}")
    @Timed
    public ResponseEntity<GameDTO> getGame(@PathVariable Long id) {
        log.debug("REST request to get Game : {}", id);
        GameDTO gameDTO = gameService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(gameDTO));
    }

    /**
     * DELETE  /games/:id : delete the "id" game.
     *
     * @param id the id of the gameDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/games/{id}")
    @Timed
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        log.debug("REST request to delete Game : {}", id);
        gameService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
