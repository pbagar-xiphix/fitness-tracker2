package com.pluralsight.web.rest;

import com.pluralsight.FitnessTracker2App;

import com.pluralsight.domain.Goal;
import com.pluralsight.repository.GoalRepository;
import com.pluralsight.service.GoalService;
import com.pluralsight.service.dto.GoalDTO;
import com.pluralsight.service.mapper.GoalMapper;
import com.pluralsight.web.rest.errors.ExceptionTranslator;

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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.pluralsight.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the GoalResource REST controller.
 *
 * @see GoalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FitnessTracker2App.class)
public class GoalResourceIntTest {

    private static final Integer DEFAULT_MINUTES = 1;
    private static final Integer UPDATED_MINUTES = 2;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalMapper goalMapper;

    @Autowired
    private GoalService goalService;

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

    private MockMvc restGoalMockMvc;

    private Goal goal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GoalResource goalResource = new GoalResource(goalService);
        this.restGoalMockMvc = MockMvcBuilders.standaloneSetup(goalResource)
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
    public static Goal createEntity(EntityManager em) {
        Goal goal = new Goal()
            .minutes(DEFAULT_MINUTES);
        return goal;
    }

    @Before
    public void initTest() {
        goal = createEntity(em);
    }

    @Test
    @Transactional
    public void createGoal() throws Exception {
        int databaseSizeBeforeCreate = goalRepository.findAll().size();

        // Create the Goal
        GoalDTO goalDTO = goalMapper.toDto(goal);
        restGoalMockMvc.perform(post("/api/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isCreated());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeCreate + 1);
        Goal testGoal = goalList.get(goalList.size() - 1);
        assertThat(testGoal.getMinutes()).isEqualTo(DEFAULT_MINUTES);
    }

    @Test
    @Transactional
    public void createGoalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = goalRepository.findAll().size();

        // Create the Goal with an existing ID
        goal.setId(1L);
        GoalDTO goalDTO = goalMapper.toDto(goal);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGoalMockMvc.perform(post("/api/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGoals() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        // Get all the goalList
        restGoalMockMvc.perform(get("/api/goals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(goal.getId().intValue())))
            .andExpect(jsonPath("$.[*].minutes").value(hasItem(DEFAULT_MINUTES)));
    }
    
    @Test
    @Transactional
    public void getGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        // Get the goal
        restGoalMockMvc.perform(get("/api/goals/{id}", goal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(goal.getId().intValue()))
            .andExpect(jsonPath("$.minutes").value(DEFAULT_MINUTES));
    }

    @Test
    @Transactional
    public void getNonExistingGoal() throws Exception {
        // Get the goal
        restGoalMockMvc.perform(get("/api/goals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        int databaseSizeBeforeUpdate = goalRepository.findAll().size();

        // Update the goal
        Goal updatedGoal = goalRepository.findById(goal.getId()).get();
        // Disconnect from session so that the updates on updatedGoal are not directly saved in db
        em.detach(updatedGoal);
        updatedGoal
            .minutes(UPDATED_MINUTES);
        GoalDTO goalDTO = goalMapper.toDto(updatedGoal);

        restGoalMockMvc.perform(put("/api/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isOk());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeUpdate);
        Goal testGoal = goalList.get(goalList.size() - 1);
        assertThat(testGoal.getMinutes()).isEqualTo(UPDATED_MINUTES);
    }

    @Test
    @Transactional
    public void updateNonExistingGoal() throws Exception {
        int databaseSizeBeforeUpdate = goalRepository.findAll().size();

        // Create the Goal
        GoalDTO goalDTO = goalMapper.toDto(goal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoalMockMvc.perform(put("/api/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        int databaseSizeBeforeDelete = goalRepository.findAll().size();

        // Get the goal
        restGoalMockMvc.perform(delete("/api/goals/{id}", goal.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Goal.class);
        Goal goal1 = new Goal();
        goal1.setId(1L);
        Goal goal2 = new Goal();
        goal2.setId(goal1.getId());
        assertThat(goal1).isEqualTo(goal2);
        goal2.setId(2L);
        assertThat(goal1).isNotEqualTo(goal2);
        goal1.setId(null);
        assertThat(goal1).isNotEqualTo(goal2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoalDTO.class);
        GoalDTO goalDTO1 = new GoalDTO();
        goalDTO1.setId(1L);
        GoalDTO goalDTO2 = new GoalDTO();
        assertThat(goalDTO1).isNotEqualTo(goalDTO2);
        goalDTO2.setId(goalDTO1.getId());
        assertThat(goalDTO1).isEqualTo(goalDTO2);
        goalDTO2.setId(2L);
        assertThat(goalDTO1).isNotEqualTo(goalDTO2);
        goalDTO1.setId(null);
        assertThat(goalDTO1).isNotEqualTo(goalDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(goalMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(goalMapper.fromId(null)).isNull();
    }
}
