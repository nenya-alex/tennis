package ua.tennis.web.rest;

import ua.tennis.TennisApp;
import ua.tennis.domain.Odds;
import ua.tennis.repository.OddsRepository;
import ua.tennis.service.OddsService;
import ua.tennis.service.dto.OddsDTO;
import ua.tennis.service.mapper.OddsMapper;
import ua.tennis.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

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
 * Integration tests for the {@Link OddsResource} REST controller.
 */
@SpringBootTest(classes = TennisApp.class)
public class OddsResourceIT {

    private static final Double DEFAULT_HOME_ODDS = 1D;
    private static final Double UPDATED_HOME_ODDS = 2D;

    private static final Double DEFAULT_AWAY_ODDS = 1D;
    private static final Double UPDATED_AWAY_ODDS = 2D;

    private static final Instant DEFAULT_CHECK_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHECK_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private OddsRepository oddsRepository;

    @Autowired
    private OddsMapper oddsMapper;

    @Autowired
    private OddsService oddsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restOddsMockMvc;

    private Odds odds;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OddsResource oddsResource = new OddsResource(oddsService);
        this.restOddsMockMvc = MockMvcBuilders.standaloneSetup(oddsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Odds createEntity(EntityManager em) {
        Odds odds = new Odds()
            .homeOdds(DEFAULT_HOME_ODDS)
            .awayOdds(DEFAULT_AWAY_ODDS)
            .checkDate(DEFAULT_CHECK_DATE);
        return odds;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Odds createUpdatedEntity(EntityManager em) {
        Odds odds = new Odds()
            .homeOdds(UPDATED_HOME_ODDS)
            .awayOdds(UPDATED_AWAY_ODDS)
            .checkDate(UPDATED_CHECK_DATE);
        return odds;
    }

    @BeforeEach
    public void initTest() {
        odds = createEntity(em);
    }

    @Test
    @Transactional
    public void createOdds() throws Exception {
        int databaseSizeBeforeCreate = oddsRepository.findAll().size();

        // Create the Odds
        OddsDTO oddsDTO = oddsMapper.toDto(odds);
        restOddsMockMvc.perform(post("/api/odds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oddsDTO)))
            .andExpect(status().isCreated());

        // Validate the Odds in the database
        List<Odds> oddsList = oddsRepository.findAll();
        assertThat(oddsList).hasSize(databaseSizeBeforeCreate + 1);
        Odds testOdds = oddsList.get(oddsList.size() - 1);
        assertThat(testOdds.getHomeOdds()).isEqualTo(DEFAULT_HOME_ODDS);
        assertThat(testOdds.getAwayOdds()).isEqualTo(DEFAULT_AWAY_ODDS);
        assertThat(testOdds.getCheckDate()).isEqualTo(DEFAULT_CHECK_DATE);
    }

    @Test
    @Transactional
    public void createOddsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = oddsRepository.findAll().size();

        // Create the Odds with an existing ID
        odds.setId(1L);
        OddsDTO oddsDTO = oddsMapper.toDto(odds);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOddsMockMvc.perform(post("/api/odds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oddsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Odds in the database
        List<Odds> oddsList = oddsRepository.findAll();
        assertThat(oddsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllOdds() throws Exception {
        // Initialize the database
        oddsRepository.saveAndFlush(odds);

        // Get all the oddsList
        restOddsMockMvc.perform(get("/api/odds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(odds.getId().intValue())))
            .andExpect(jsonPath("$.[*].homeOdds").value(hasItem(DEFAULT_HOME_ODDS.doubleValue())))
            .andExpect(jsonPath("$.[*].awayOdds").value(hasItem(DEFAULT_AWAY_ODDS.doubleValue())))
            .andExpect(jsonPath("$.[*].checkDate").value(hasItem(DEFAULT_CHECK_DATE.toString())));
    }

    @Test
    @Transactional
    public void getOdds() throws Exception {
        // Initialize the database
        oddsRepository.saveAndFlush(odds);

        // Get the odds
        restOddsMockMvc.perform(get("/api/odds/{id}", odds.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(odds.getId().intValue()))
            .andExpect(jsonPath("$.homeOdds").value(DEFAULT_HOME_ODDS.doubleValue()))
            .andExpect(jsonPath("$.awayOdds").value(DEFAULT_AWAY_ODDS.doubleValue()))
            .andExpect(jsonPath("$.checkDate").value(DEFAULT_CHECK_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOdds() throws Exception {
        // Get the odds
        restOddsMockMvc.perform(get("/api/odds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOdds() throws Exception {
        // Initialize the database
        oddsRepository.saveAndFlush(odds);

        int databaseSizeBeforeUpdate = oddsRepository.findAll().size();

        // Update the odds
        Odds updatedOdds = oddsRepository.findById(odds.getId()).get();
        // Disconnect from session so that the updates on updatedOdds are not directly saved in db
        em.detach(updatedOdds);
        updatedOdds
            .homeOdds(UPDATED_HOME_ODDS)
            .awayOdds(UPDATED_AWAY_ODDS)
            .checkDate(UPDATED_CHECK_DATE);
        OddsDTO oddsDTO = oddsMapper.toDto(updatedOdds);

        restOddsMockMvc.perform(put("/api/odds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oddsDTO)))
            .andExpect(status().isOk());

        // Validate the Odds in the database
        List<Odds> oddsList = oddsRepository.findAll();
        assertThat(oddsList).hasSize(databaseSizeBeforeUpdate);
        Odds testOdds = oddsList.get(oddsList.size() - 1);
        assertThat(testOdds.getHomeOdds()).isEqualTo(UPDATED_HOME_ODDS);
        assertThat(testOdds.getAwayOdds()).isEqualTo(UPDATED_AWAY_ODDS);
        assertThat(testOdds.getCheckDate()).isEqualTo(UPDATED_CHECK_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingOdds() throws Exception {
        int databaseSizeBeforeUpdate = oddsRepository.findAll().size();

        // Create the Odds
        OddsDTO oddsDTO = oddsMapper.toDto(odds);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOddsMockMvc.perform(put("/api/odds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oddsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Odds in the database
        List<Odds> oddsList = oddsRepository.findAll();
        assertThat(oddsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteOdds() throws Exception {
        // Initialize the database
        oddsRepository.saveAndFlush(odds);

        int databaseSizeBeforeDelete = oddsRepository.findAll().size();

        // Delete the odds
        restOddsMockMvc.perform(delete("/api/odds/{id}", odds.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Odds> oddsList = oddsRepository.findAll();
        assertThat(oddsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Odds.class);
        Odds odds1 = new Odds();
        odds1.setId(1L);
        Odds odds2 = new Odds();
        odds2.setId(odds1.getId());
        assertThat(odds1).isEqualTo(odds2);
        odds2.setId(2L);
        assertThat(odds1).isNotEqualTo(odds2);
        odds1.setId(null);
        assertThat(odds1).isNotEqualTo(odds2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OddsDTO.class);
        OddsDTO oddsDTO1 = new OddsDTO();
        oddsDTO1.setId(1L);
        OddsDTO oddsDTO2 = new OddsDTO();
        assertThat(oddsDTO1).isNotEqualTo(oddsDTO2);
        oddsDTO2.setId(oddsDTO1.getId());
        assertThat(oddsDTO1).isEqualTo(oddsDTO2);
        oddsDTO2.setId(2L);
        assertThat(oddsDTO1).isNotEqualTo(oddsDTO2);
        oddsDTO1.setId(null);
        assertThat(oddsDTO1).isNotEqualTo(oddsDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(oddsMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(oddsMapper.fromId(null)).isNull();
    }
}
