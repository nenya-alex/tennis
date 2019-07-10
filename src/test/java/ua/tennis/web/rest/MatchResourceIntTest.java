package ua.tennis.web.rest;

import ua.tennis.TennisApp;

import ua.tennis.domain.Match;
import ua.tennis.repository.MatchRepository;
import ua.tennis.service.MatchService;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.mapper.MatchMapper;
import ua.tennis.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ua.tennis.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MatchResource REST controller.
 *
 * @see MatchResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TennisApp.class)
public class MatchResourceIntTest {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_HOME_NAME = "AAAAAAAAAA";
    private static final String UPDATED_HOME_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_AWAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AWAY_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_HOME_SCORE = 1;
    private static final Integer UPDATED_HOME_SCORE = 2;

    private static final Integer DEFAULT_AWAY_SCORE = 1;
    private static final Integer UPDATED_AWAY_SCORE = 2;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_OPEN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPEN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMatchMockMvc;

    private Match match;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MatchResource matchResource = new MatchResource(matchService);
        this.restMatchMockMvc = MockMvcBuilders.standaloneSetup(matchResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Match createEntity(EntityManager em) {
        Match match = new Match()
            .identifier(DEFAULT_IDENTIFIER)
            .homeName(DEFAULT_HOME_NAME)
            .awayName(DEFAULT_AWAY_NAME)
            .homeScore(DEFAULT_HOME_SCORE)
            .awayScore(DEFAULT_AWAY_SCORE)
            .startDate(DEFAULT_START_DATE)
            .name(DEFAULT_NAME)
            .openDate(DEFAULT_OPEN_DATE);
        return match;
    }

    @Before
    public void initTest() {
        match = createEntity(em);
    }

    @Test
    @Transactional
    public void createMatch() throws Exception {
        int databaseSizeBeforeCreate = matchRepository.findAll().size();

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);
        restMatchMockMvc.perform(post("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isCreated());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeCreate + 1);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testMatch.getHomeName()).isEqualTo(DEFAULT_HOME_NAME);
        assertThat(testMatch.getAwayName()).isEqualTo(DEFAULT_AWAY_NAME);
        assertThat(testMatch.getHomeScore()).isEqualTo(DEFAULT_HOME_SCORE);
        assertThat(testMatch.getAwayScore()).isEqualTo(DEFAULT_AWAY_SCORE);
        assertThat(testMatch.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testMatch.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMatch.getOpenDate()).isEqualTo(DEFAULT_OPEN_DATE);
    }

    @Test
    @Transactional
    public void createMatchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = matchRepository.findAll().size();

        // Create the Match with an existing ID
        match.setId(1L);
        MatchDTO matchDTO = matchMapper.toDto(match);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMatchMockMvc.perform(post("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMatches() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList
        restMatchMockMvc.perform(get("/api/matches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(match.getId().intValue())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].homeName").value(hasItem(DEFAULT_HOME_NAME.toString())))
            .andExpect(jsonPath("$.[*].awayName").value(hasItem(DEFAULT_AWAY_NAME.toString())))
            .andExpect(jsonPath("$.[*].homeScore").value(hasItem(DEFAULT_HOME_SCORE)))
            .andExpect(jsonPath("$.[*].awayScore").value(hasItem(DEFAULT_AWAY_SCORE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].openDate").value(hasItem(DEFAULT_OPEN_DATE.toString())));
    }

    @Test
    @Transactional
    public void getMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get the match
        restMatchMockMvc.perform(get("/api/matches/{id}", match.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(match.getId().intValue()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER.toString()))
            .andExpect(jsonPath("$.homeName").value(DEFAULT_HOME_NAME.toString()))
            .andExpect(jsonPath("$.awayName").value(DEFAULT_AWAY_NAME.toString()))
            .andExpect(jsonPath("$.homeScore").value(DEFAULT_HOME_SCORE))
            .andExpect(jsonPath("$.awayScore").value(DEFAULT_AWAY_SCORE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.openDate").value(DEFAULT_OPEN_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMatch() throws Exception {
        // Get the match
        restMatchMockMvc.perform(get("/api/matches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Update the match
        Match updatedMatch = matchRepository.findOne(match.getId());
        // Disconnect from session so that the updates on updatedMatch are not directly saved in db
        em.detach(updatedMatch);
        updatedMatch
            .identifier(UPDATED_IDENTIFIER)
            .homeName(UPDATED_HOME_NAME)
            .awayName(UPDATED_AWAY_NAME)
            .homeScore(UPDATED_HOME_SCORE)
            .awayScore(UPDATED_AWAY_SCORE)
            .startDate(UPDATED_START_DATE)
            .name(UPDATED_NAME)
            .openDate(UPDATED_OPEN_DATE);
        MatchDTO matchDTO = matchMapper.toDto(updatedMatch);

        restMatchMockMvc.perform(put("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isOk());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testMatch.getHomeName()).isEqualTo(UPDATED_HOME_NAME);
        assertThat(testMatch.getAwayName()).isEqualTo(UPDATED_AWAY_NAME);
        assertThat(testMatch.getHomeScore()).isEqualTo(UPDATED_HOME_SCORE);
        assertThat(testMatch.getAwayScore()).isEqualTo(UPDATED_AWAY_SCORE);
        assertThat(testMatch.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testMatch.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMatch.getOpenDate()).isEqualTo(UPDATED_OPEN_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMatchMockMvc.perform(put("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isCreated());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);
        int databaseSizeBeforeDelete = matchRepository.findAll().size();

        // Get the match
        restMatchMockMvc.perform(delete("/api/matches/{id}", match.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Match.class);
        Match match1 = new Match();
        match1.setId(1L);
        Match match2 = new Match();
        match2.setId(match1.getId());
        assertThat(match1).isEqualTo(match2);
        match2.setId(2L);
        assertThat(match1).isNotEqualTo(match2);
        match1.setId(null);
        assertThat(match1).isNotEqualTo(match2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatchDTO.class);
        MatchDTO matchDTO1 = new MatchDTO();
        matchDTO1.setId(1L);
        MatchDTO matchDTO2 = new MatchDTO();
        assertThat(matchDTO1).isNotEqualTo(matchDTO2);
        matchDTO2.setId(matchDTO1.getId());
        assertThat(matchDTO1).isEqualTo(matchDTO2);
        matchDTO2.setId(2L);
        assertThat(matchDTO1).isNotEqualTo(matchDTO2);
        matchDTO1.setId(null);
        assertThat(matchDTO1).isNotEqualTo(matchDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(matchMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(matchMapper.fromId(null)).isNull();
    }
}
