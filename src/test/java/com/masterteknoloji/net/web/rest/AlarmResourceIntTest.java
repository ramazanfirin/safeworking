package com.masterteknoloji.net.web.rest;

import com.masterteknoloji.net.SafeworkingApp;

import com.masterteknoloji.net.domain.Alarm;
import com.masterteknoloji.net.repository.AlarmRepository;
import com.masterteknoloji.net.web.rest.errors.ExceptionTranslator;

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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.masterteknoloji.net.web.rest.TestUtil.sameInstant;
import static com.masterteknoloji.net.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.masterteknoloji.net.domain.enumeration.AlarmType;
/**
 * Test class for the AlarmResource REST controller.
 *
 * @see AlarmResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SafeworkingApp.class)
public class AlarmResourceIntTest {

    private static final ZonedDateTime DEFAULT_INSERT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_INSERT_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_BACK_GROUND_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BACK_GROUND_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_BACK_GROUND_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BACK_GROUND_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_IMAGE_FILE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_FILE = "BBBBBBBBBB";

    private static final String DEFAULT_ALARM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ALARM_TYPE = "BBBBBBBBBB";

    private static final AlarmType DEFAULT_ALARM_TYPE_VALUE = AlarmType.CALL;
    private static final AlarmType UPDATED_ALARM_TYPE_VALUE = AlarmType.WATCH_PHONE;

    private static final Boolean DEFAULT_FALSE_ALARM = false;
    private static final Boolean UPDATED_FALSE_ALARM = true;

    private static final Boolean DEFAULT_PROCESSED = false;
    private static final Boolean UPDATED_PROCESSED = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAlarmMockMvc;

    private Alarm alarm;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AlarmResource alarmResource = new AlarmResource(alarmRepository);
        this.restAlarmMockMvc = MockMvcBuilders.standaloneSetup(alarmResource)
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
    public static Alarm createEntity(EntityManager em) {
        Alarm alarm = new Alarm()
            .insertDate(DEFAULT_INSERT_DATE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .backGroundImage(DEFAULT_BACK_GROUND_IMAGE)
            .backGroundImageContentType(DEFAULT_BACK_GROUND_IMAGE_CONTENT_TYPE)
            .imageFile(DEFAULT_IMAGE_FILE)
            .alarmType(DEFAULT_ALARM_TYPE)
            .alarmTypeValue(DEFAULT_ALARM_TYPE_VALUE)
            .falseAlarm(DEFAULT_FALSE_ALARM)
            .processed(DEFAULT_PROCESSED)
            .note(DEFAULT_NOTE);
        return alarm;
    }

    @Before
    public void initTest() {
        alarm = createEntity(em);
    }

    @Test
    @Transactional
    public void createAlarm() throws Exception {
        int databaseSizeBeforeCreate = alarmRepository.findAll().size();

        // Create the Alarm
        restAlarmMockMvc.perform(post("/api/alarms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(alarm)))
            .andExpect(status().isCreated());

        // Validate the Alarm in the database
        List<Alarm> alarmList = alarmRepository.findAll();
        assertThat(alarmList).hasSize(databaseSizeBeforeCreate + 1);
        Alarm testAlarm = alarmList.get(alarmList.size() - 1);
        assertThat(testAlarm.getInsertDate()).isEqualTo(DEFAULT_INSERT_DATE);
        assertThat(testAlarm.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testAlarm.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testAlarm.getBackGroundImage()).isEqualTo(DEFAULT_BACK_GROUND_IMAGE);
        assertThat(testAlarm.getBackGroundImageContentType()).isEqualTo(DEFAULT_BACK_GROUND_IMAGE_CONTENT_TYPE);
        assertThat(testAlarm.getImageFile()).isEqualTo(DEFAULT_IMAGE_FILE);
        assertThat(testAlarm.getAlarmType()).isEqualTo(DEFAULT_ALARM_TYPE);
        assertThat(testAlarm.getAlarmTypeValue()).isEqualTo(DEFAULT_ALARM_TYPE_VALUE);
        assertThat(testAlarm.isFalseAlarm()).isEqualTo(DEFAULT_FALSE_ALARM);
        assertThat(testAlarm.isProcessed()).isEqualTo(DEFAULT_PROCESSED);
        assertThat(testAlarm.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    @Transactional
    public void createAlarmWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = alarmRepository.findAll().size();

        // Create the Alarm with an existing ID
        alarm.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlarmMockMvc.perform(post("/api/alarms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(alarm)))
            .andExpect(status().isBadRequest());

        // Validate the Alarm in the database
        List<Alarm> alarmList = alarmRepository.findAll();
        assertThat(alarmList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAlarms() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        // Get all the alarmList
        restAlarmMockMvc.perform(get("/api/alarms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alarm.getId().intValue())))
            .andExpect(jsonPath("$.[*].insertDate").value(hasItem(sameInstant(DEFAULT_INSERT_DATE))))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].backGroundImageContentType").value(hasItem(DEFAULT_BACK_GROUND_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].backGroundImage").value(hasItem(Base64Utils.encodeToString(DEFAULT_BACK_GROUND_IMAGE))))
            .andExpect(jsonPath("$.[*].imageFile").value(hasItem(DEFAULT_IMAGE_FILE.toString())))
            .andExpect(jsonPath("$.[*].alarmType").value(hasItem(DEFAULT_ALARM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].alarmTypeValue").value(hasItem(DEFAULT_ALARM_TYPE_VALUE.toString())))
            .andExpect(jsonPath("$.[*].falseAlarm").value(hasItem(DEFAULT_FALSE_ALARM.booleanValue())))
            .andExpect(jsonPath("$.[*].processed").value(hasItem(DEFAULT_PROCESSED.booleanValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())));
    }

    @Test
    @Transactional
    public void getAlarm() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        // Get the alarm
        restAlarmMockMvc.perform(get("/api/alarms/{id}", alarm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(alarm.getId().intValue()))
            .andExpect(jsonPath("$.insertDate").value(sameInstant(DEFAULT_INSERT_DATE)))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.backGroundImageContentType").value(DEFAULT_BACK_GROUND_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.backGroundImage").value(Base64Utils.encodeToString(DEFAULT_BACK_GROUND_IMAGE)))
            .andExpect(jsonPath("$.imageFile").value(DEFAULT_IMAGE_FILE.toString()))
            .andExpect(jsonPath("$.alarmType").value(DEFAULT_ALARM_TYPE.toString()))
            .andExpect(jsonPath("$.alarmTypeValue").value(DEFAULT_ALARM_TYPE_VALUE.toString()))
            .andExpect(jsonPath("$.falseAlarm").value(DEFAULT_FALSE_ALARM.booleanValue()))
            .andExpect(jsonPath("$.processed").value(DEFAULT_PROCESSED.booleanValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAlarm() throws Exception {
        // Get the alarm
        restAlarmMockMvc.perform(get("/api/alarms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlarm() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);
        int databaseSizeBeforeUpdate = alarmRepository.findAll().size();

        // Update the alarm
        Alarm updatedAlarm = alarmRepository.findOne(alarm.getId());
        // Disconnect from session so that the updates on updatedAlarm are not directly saved in db
        em.detach(updatedAlarm);
        updatedAlarm
            .insertDate(UPDATED_INSERT_DATE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .backGroundImage(UPDATED_BACK_GROUND_IMAGE)
            .backGroundImageContentType(UPDATED_BACK_GROUND_IMAGE_CONTENT_TYPE)
            .imageFile(UPDATED_IMAGE_FILE)
            .alarmType(UPDATED_ALARM_TYPE)
            .alarmTypeValue(UPDATED_ALARM_TYPE_VALUE)
            .falseAlarm(UPDATED_FALSE_ALARM)
            .processed(UPDATED_PROCESSED)
            .note(UPDATED_NOTE);

        restAlarmMockMvc.perform(put("/api/alarms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAlarm)))
            .andExpect(status().isOk());

        // Validate the Alarm in the database
        List<Alarm> alarmList = alarmRepository.findAll();
        assertThat(alarmList).hasSize(databaseSizeBeforeUpdate);
        Alarm testAlarm = alarmList.get(alarmList.size() - 1);
        assertThat(testAlarm.getInsertDate()).isEqualTo(UPDATED_INSERT_DATE);
        assertThat(testAlarm.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testAlarm.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testAlarm.getBackGroundImage()).isEqualTo(UPDATED_BACK_GROUND_IMAGE);
        assertThat(testAlarm.getBackGroundImageContentType()).isEqualTo(UPDATED_BACK_GROUND_IMAGE_CONTENT_TYPE);
        assertThat(testAlarm.getImageFile()).isEqualTo(UPDATED_IMAGE_FILE);
        assertThat(testAlarm.getAlarmType()).isEqualTo(UPDATED_ALARM_TYPE);
        assertThat(testAlarm.getAlarmTypeValue()).isEqualTo(UPDATED_ALARM_TYPE_VALUE);
        assertThat(testAlarm.isFalseAlarm()).isEqualTo(UPDATED_FALSE_ALARM);
        assertThat(testAlarm.isProcessed()).isEqualTo(UPDATED_PROCESSED);
        assertThat(testAlarm.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    @Transactional
    public void updateNonExistingAlarm() throws Exception {
        int databaseSizeBeforeUpdate = alarmRepository.findAll().size();

        // Create the Alarm

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAlarmMockMvc.perform(put("/api/alarms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(alarm)))
            .andExpect(status().isCreated());

        // Validate the Alarm in the database
        List<Alarm> alarmList = alarmRepository.findAll();
        assertThat(alarmList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAlarm() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);
        int databaseSizeBeforeDelete = alarmRepository.findAll().size();

        // Get the alarm
        restAlarmMockMvc.perform(delete("/api/alarms/{id}", alarm.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Alarm> alarmList = alarmRepository.findAll();
        assertThat(alarmList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Alarm.class);
        Alarm alarm1 = new Alarm();
        alarm1.setId(1L);
        Alarm alarm2 = new Alarm();
        alarm2.setId(alarm1.getId());
        assertThat(alarm1).isEqualTo(alarm2);
        alarm2.setId(2L);
        assertThat(alarm1).isNotEqualTo(alarm2);
        alarm1.setId(null);
        assertThat(alarm1).isNotEqualTo(alarm2);
    }
}
