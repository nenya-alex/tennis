package ua.tennis.web.rest;

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
import ua.tennis.TennisApp;
import ua.tennis.domain.Sett;
import ua.tennis.repository.SettRepository;
import ua.tennis.service.SettService;
import ua.tennis.service.dto.SettDTO;
import ua.tennis.service.mapper.SettMapper;
import ua.tennis.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.tennis.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the SettResource REST controller.
 *
 * @see SettResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TennisApp.class)
public class SettResourceIntTest {

    private static final Integer DEFAULT_HOME_SCORE = 1;
    private static final Integer UPDATED_HOME_SCORE = 2;

    private static final Integer DEFAULT_AWAY_SCORE = 1;
    private static final Integer UPDATED_AWAY_SCORE = 2;

    private static final Double DEFAULT_HOME_PROBABILITY = 1D;
    private static final Double UPDATED_HOME_PROBABILITY = 2D;

    private static final Double DEFAULT_AWAY_PROBABILITY = 1D;
    private static final Double UPDATED_AWAY_PROBABILITY = 2D;

    @Autowired
    private SettRepository settRepository;

    @Autowired
    private SettMapper settMapper;

    @Autowired
    private SettService settService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSettMockMvc;

    private Sett sett;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SettResource settResource = new SettResource(settService);
        this.restSettMockMvc = MockMvcBuilders.standaloneSetup(settResource)
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
    public static Sett createEntity(EntityManager em) {
        Sett sett = new Sett()
            .homeScore(DEFAULT_HOME_SCORE)
            .awayScore(DEFAULT_AWAY_SCORE)
            .homeProbability(DEFAULT_HOME_PROBABILITY)
            .awayProbability(DEFAULT_AWAY_PROBABILITY);
        return sett;
    }

    @Before
    public void initTest() {
        sett = createEntity(em);
    }

    @Test
    @Transactional
    public void createSett() throws Exception {
        int databaseSizeBeforeCreate = settRepository.findAll().size();

        // Create the Sett
        SettDTO settDTO = settMapper.toDto(sett);
        restSettMockMvc.perform(post("/api/setts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(settDTO)))
            .andExpect(status().isCreated());

        // Validate the Sett in the database
        List<Sett> settList = settRepository.findAll();
        assertThat(settList).hasSize(databaseSizeBeforeCreate + 1);
        Sett testSett = settList.get(settList.size() - 1);
        assertThat(testSett.getHomeScore()).isEqualTo(DEFAULT_HOME_SCORE);
        assertThat(testSett.getAwayScore()).isEqualTo(DEFAULT_AWAY_SCORE);
        assertThat(testSett.getHomeProbability()).isEqualTo(DEFAULT_HOME_PROBABILITY);
        assertThat(testSett.getAwayProbability()).isEqualTo(DEFAULT_AWAY_PROBABILITY);
    }

    @Test
    @Transactional
    public void createSettWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = settRepository.findAll().size();

        // Create the Sett with an existing ID
        sett.setId(1L);
        SettDTO settDTO = settMapper.toDto(sett);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSettMockMvc.perform(post("/api/setts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(settDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sett in the database
        List<Sett> settList = settRepository.findAll();
        assertThat(settList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllSetts() throws Exception {
        // Initialize the database
        settRepository.saveAndFlush(sett);

        // Get all the settList
        restSettMockMvc.perform(get("/api/setts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sett.getId().intValue())))
            .andExpect(jsonPath("$.[*].homeScore").value(hasItem(DEFAULT_HOME_SCORE)))
            .andExpect(jsonPath("$.[*].awayScore").value(hasItem(DEFAULT_AWAY_SCORE)))
            .andExpect(jsonPath("$.[*].homeProbability").value(hasItem(DEFAULT_HOME_PROBABILITY.doubleValue())))
            .andExpect(jsonPath("$.[*].awayProbability").value(hasItem(DEFAULT_AWAY_PROBABILITY.doubleValue())));
    }

    @Test
    @Transactional
    public void getSett() throws Exception {
        // Initialize the database
        settRepository.saveAndFlush(sett);

        // Get the sett
        restSettMockMvc.perform(get("/api/setts/{id}", sett.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sett.getId().intValue()))
            .andExpect(jsonPath("$.homeScore").value(DEFAULT_HOME_SCORE))
            .andExpect(jsonPath("$.awayScore").value(DEFAULT_AWAY_SCORE))
            .andExpect(jsonPath("$.homeProbability").value(DEFAULT_HOME_PROBABILITY.doubleValue()))
            .andExpect(jsonPath("$.awayProbability").value(DEFAULT_AWAY_PROBABILITY.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSett() throws Exception {
        // Get the sett
        restSettMockMvc.perform(get("/api/setts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSett() throws Exception {
        // Initialize the database
        settRepository.saveAndFlush(sett);
        int databaseSizeBeforeUpdate = settRepository.findAll().size();

        // Update the sett
        Sett updatedSett = settRepository.findOne(sett.getId());
        // Disconnect from session so that the updates on updatedSett are not directly saved in db
        em.detach(updatedSett);
        updatedSett
            .homeScore(UPDATED_HOME_SCORE)
            .awayScore(UPDATED_AWAY_SCORE)
            .homeProbability(UPDATED_HOME_PROBABILITY)
            .awayProbability(UPDATED_AWAY_PROBABILITY);
        SettDTO settDTO = settMapper.toDto(updatedSett);

        restSettMockMvc.perform(put("/api/setts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(settDTO)))
            .andExpect(status().isOk());

        // Validate the Sett in the database
        List<Sett> settList = settRepository.findAll();
        assertThat(settList).hasSize(databaseSizeBeforeUpdate);
        Sett testSett = settList.get(settList.size() - 1);
        assertThat(testSett.getHomeScore()).isEqualTo(UPDATED_HOME_SCORE);
        assertThat(testSett.getAwayScore()).isEqualTo(UPDATED_AWAY_SCORE);
        assertThat(testSett.getHomeProbability()).isEqualTo(UPDATED_HOME_PROBABILITY);
        assertThat(testSett.getAwayProbability()).isEqualTo(UPDATED_AWAY_PROBABILITY);
    }

    @Test
    @Transactional
    public void updateNonExistingSett() throws Exception {
        int databaseSizeBeforeUpdate = settRepository.findAll().size();

        // Create the Sett
        SettDTO settDTO = settMapper.toDto(sett);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSettMockMvc.perform(put("/api/setts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(settDTO)))
            .andExpect(status().isCreated());

        // Validate the Sett in the database
        List<Sett> settList = settRepository.findAll();
        assertThat(settList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSett() throws Exception {
        // Initialize the database
        settRepository.saveAndFlush(sett);
        int databaseSizeBeforeDelete = settRepository.findAll().size();

        // Get the sett
        restSettMockMvc.perform(delete("/api/setts/{id}", sett.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Sett> settList = settRepository.findAll();
        assertThat(settList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sett.class);
        Sett sett1 = new Sett();
        sett1.setId(1L);
        Sett sett2 = new Sett();
        sett2.setId(sett1.getId());
        assertThat(sett1).isEqualTo(sett2);
        sett2.setId(2L);
        assertThat(sett1).isNotEqualTo(sett2);
        sett1.setId(null);
        assertThat(sett1).isNotEqualTo(sett2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SettDTO.class);
        SettDTO settDTO1 = new SettDTO();
        settDTO1.setId(1L);
        SettDTO settDTO2 = new SettDTO();
        assertThat(settDTO1).isNotEqualTo(settDTO2);
        settDTO2.setId(settDTO1.getId());
        assertThat(settDTO1).isEqualTo(settDTO2);
        settDTO2.setId(2L);
        assertThat(settDTO1).isNotEqualTo(settDTO2);
        settDTO1.setId(null);
        assertThat(settDTO1).isNotEqualTo(settDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(settMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(settMapper.fromId(null)).isNull();
    }
}
