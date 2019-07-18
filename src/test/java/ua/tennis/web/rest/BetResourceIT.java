package ua.tennis.web.rest;

import ua.tennis.TennisApp;
import ua.tennis.domain.Bet;
import ua.tennis.repository.BetRepository;
import ua.tennis.service.BetService;
import ua.tennis.service.dto.BetDTO;
import ua.tennis.service.mapper.BetMapper;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ua.tennis.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link BetResource} REST controller.
 */
@SpringBootTest(classes = TennisApp.class)
public class BetResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final Double DEFAULT_ODDS = 1D;
    private static final Double UPDATED_ODDS = 2D;

    private static final MatchWinner DEFAULT_PLACED_BET_MATCH_WINNER = MatchWinner.Q;
    private static final MatchWinner UPDATED_PLACED_BET_MATCH_WINNER = MatchWinner.Q;

    private static final BetStatus DEFAULT_STATUS = BetStatus.A;
    private static final BetStatus UPDATED_STATUS = BetStatus.A;

    private static final Instant DEFAULT_PLACED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLACED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private BetMapper betMapper;

    @Autowired
    private BetService betService;

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

    private MockMvc restBetMockMvc;

    private Bet bet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BetResource betResource = new BetResource(betService);
        this.restBetMockMvc = MockMvcBuilders.standaloneSetup(betResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bet createEntity(EntityManager em) {
        Bet bet = new Bet()
            .amount(DEFAULT_AMOUNT)
            .odds(DEFAULT_ODDS)
            .placedBetMatchWinner(DEFAULT_PLACED_BET_MATCH_WINNER)
            .status(DEFAULT_STATUS)
            .placedDate(DEFAULT_PLACED_DATE);
        return bet;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bet createUpdatedEntity(EntityManager em) {
        Bet bet = new Bet()
            .amount(UPDATED_AMOUNT)
            .odds(UPDATED_ODDS)
            .placedBetMatchWinner(UPDATED_PLACED_BET_MATCH_WINNER)
            .status(UPDATED_STATUS)
            .placedDate(UPDATED_PLACED_DATE);
        return bet;
    }

    @BeforeEach
    public void initTest() {
        bet = createEntity(em);
    }

    @Test
    @Transactional
    public void createBet() throws Exception {
        int databaseSizeBeforeCreate = betRepository.findAll().size();

        // Create the Bet
        BetDTO betDTO = betMapper.toDto(bet);
        restBetMockMvc.perform(post("/api/bets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(betDTO)))
            .andExpect(status().isCreated());

        // Validate the Bet in the database
        List<Bet> betList = betRepository.findAll();
        assertThat(betList).hasSize(databaseSizeBeforeCreate + 1);
        Bet testBet = betList.get(betList.size() - 1);
        assertThat(testBet.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testBet.getOdds()).isEqualTo(DEFAULT_ODDS);
        assertThat(testBet.getPlacedBetMatchWinner()).isEqualTo(DEFAULT_PLACED_BET_MATCH_WINNER);
        assertThat(testBet.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBet.getPlacedDate()).isEqualTo(DEFAULT_PLACED_DATE);
    }

    @Test
    @Transactional
    public void createBetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = betRepository.findAll().size();

        // Create the Bet with an existing ID
        bet.setId(1L);
        BetDTO betDTO = betMapper.toDto(bet);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBetMockMvc.perform(post("/api/bets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(betDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bet in the database
        List<Bet> betList = betRepository.findAll();
        assertThat(betList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllBets() throws Exception {
        // Initialize the database
        betRepository.saveAndFlush(bet);

        // Get all the betList
        restBetMockMvc.perform(get("/api/bets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bet.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].odds").value(hasItem(DEFAULT_ODDS.doubleValue())))
            .andExpect(jsonPath("$.[*].placedBetMatchWinner").value(hasItem(DEFAULT_PLACED_BET_MATCH_WINNER.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].placedDate").value(hasItem(DEFAULT_PLACED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getBet() throws Exception {
        // Initialize the database
        betRepository.saveAndFlush(bet);

        // Get the bet
        restBetMockMvc.perform(get("/api/bets/{id}", bet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bet.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.odds").value(DEFAULT_ODDS.doubleValue()))
            .andExpect(jsonPath("$.placedBetMatchWinner").value(DEFAULT_PLACED_BET_MATCH_WINNER.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.placedDate").value(DEFAULT_PLACED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBet() throws Exception {
        // Get the bet
        restBetMockMvc.perform(get("/api/bets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBet() throws Exception {
        // Initialize the database
        betRepository.saveAndFlush(bet);

        int databaseSizeBeforeUpdate = betRepository.findAll().size();

        // Update the bet
        Bet updatedBet = betRepository.findById(bet.getId()).get();
        // Disconnect from session so that the updates on updatedBet are not directly saved in db
        em.detach(updatedBet);
        updatedBet
            .amount(UPDATED_AMOUNT)
            .odds(UPDATED_ODDS)
            .placedBetMatchWinner(UPDATED_PLACED_BET_MATCH_WINNER)
            .status(UPDATED_STATUS)
            .placedDate(UPDATED_PLACED_DATE);
        BetDTO betDTO = betMapper.toDto(updatedBet);

        restBetMockMvc.perform(put("/api/bets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(betDTO)))
            .andExpect(status().isOk());

        // Validate the Bet in the database
        List<Bet> betList = betRepository.findAll();
        assertThat(betList).hasSize(databaseSizeBeforeUpdate);
        Bet testBet = betList.get(betList.size() - 1);
        assertThat(testBet.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBet.getOdds()).isEqualTo(UPDATED_ODDS);
        assertThat(testBet.getPlacedBetMatchWinner()).isEqualTo(UPDATED_PLACED_BET_MATCH_WINNER);
        assertThat(testBet.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBet.getPlacedDate()).isEqualTo(UPDATED_PLACED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingBet() throws Exception {
        int databaseSizeBeforeUpdate = betRepository.findAll().size();

        // Create the Bet
        BetDTO betDTO = betMapper.toDto(bet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBetMockMvc.perform(put("/api/bets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(betDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bet in the database
        List<Bet> betList = betRepository.findAll();
        assertThat(betList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBet() throws Exception {
        // Initialize the database
        betRepository.saveAndFlush(bet);

        int databaseSizeBeforeDelete = betRepository.findAll().size();

        // Delete the bet
        restBetMockMvc.perform(delete("/api/bets/{id}", bet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Bet> betList = betRepository.findAll();
        assertThat(betList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bet.class);
        Bet bet1 = new Bet();
        bet1.setId(1L);
        Bet bet2 = new Bet();
        bet2.setId(bet1.getId());
        assertThat(bet1).isEqualTo(bet2);
        bet2.setId(2L);
        assertThat(bet1).isNotEqualTo(bet2);
        bet1.setId(null);
        assertThat(bet1).isNotEqualTo(bet2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BetDTO.class);
        BetDTO betDTO1 = new BetDTO();
        betDTO1.setId(1L);
        BetDTO betDTO2 = new BetDTO();
        assertThat(betDTO1).isNotEqualTo(betDTO2);
        betDTO2.setId(betDTO1.getId());
        assertThat(betDTO1).isEqualTo(betDTO2);
        betDTO2.setId(2L);
        assertThat(betDTO1).isNotEqualTo(betDTO2);
        betDTO1.setId(null);
        assertThat(betDTO1).isNotEqualTo(betDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(betMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(betMapper.fromId(null)).isNull();
    }
}
