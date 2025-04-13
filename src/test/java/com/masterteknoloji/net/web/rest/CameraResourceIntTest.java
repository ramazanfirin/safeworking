package com.masterteknoloji.net.web.rest;

import com.masterteknoloji.net.SafeworkingApp;

import com.masterteknoloji.net.domain.Camera;
import com.masterteknoloji.net.repository.CameraRepository;
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

import javax.persistence.EntityManager;
import java.util.List;

import static com.masterteknoloji.net.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CameraResource REST controller.
 *
 * @see CameraResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SafeworkingApp.class)
public class CameraResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_RTSP_URL = "AAAAAAAAAA";
    private static final String UPDATED_RTSP_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_CHANNEL = 1L;
    private static final Long UPDATED_CHANNEL = 2L;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCameraMockMvc;

    private Camera camera;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CameraResource cameraResource = new CameraResource(cameraRepository);
        this.restCameraMockMvc = MockMvcBuilders.standaloneSetup(cameraResource)
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
    public static Camera createEntity(EntityManager em) {
        Camera camera = new Camera()
            .name(DEFAULT_NAME)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .rtspUrl(DEFAULT_RTSP_URL)
            .channel(DEFAULT_CHANNEL)
            .description(DEFAULT_DESCRIPTION);
        return camera;
    }

    @Before
    public void initTest() {
        camera = createEntity(em);
    }

    @Test
    @Transactional
    public void createCamera() throws Exception {
        int databaseSizeBeforeCreate = cameraRepository.findAll().size();

        // Create the Camera
        restCameraMockMvc.perform(post("/api/cameras")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(camera)))
            .andExpect(status().isCreated());

        // Validate the Camera in the database
        List<Camera> cameraList = cameraRepository.findAll();
        assertThat(cameraList).hasSize(databaseSizeBeforeCreate + 1);
        Camera testCamera = cameraList.get(cameraList.size() - 1);
        assertThat(testCamera.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCamera.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testCamera.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testCamera.getRtspUrl()).isEqualTo(DEFAULT_RTSP_URL);
        assertThat(testCamera.getChannel()).isEqualTo(DEFAULT_CHANNEL);
        assertThat(testCamera.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createCameraWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cameraRepository.findAll().size();

        // Create the Camera with an existing ID
        camera.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCameraMockMvc.perform(post("/api/cameras")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(camera)))
            .andExpect(status().isBadRequest());

        // Validate the Camera in the database
        List<Camera> cameraList = cameraRepository.findAll();
        assertThat(cameraList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCameras() throws Exception {
        // Initialize the database
        cameraRepository.saveAndFlush(camera);

        // Get all the cameraList
        restCameraMockMvc.perform(get("/api/cameras?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(camera.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
            .andExpect(jsonPath("$.[*].rtspUrl").value(hasItem(DEFAULT_RTSP_URL.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getCamera() throws Exception {
        // Initialize the database
        cameraRepository.saveAndFlush(camera);

        // Get the camera
        restCameraMockMvc.perform(get("/api/cameras/{id}", camera.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(camera.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
            .andExpect(jsonPath("$.rtspUrl").value(DEFAULT_RTSP_URL.toString()))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCamera() throws Exception {
        // Get the camera
        restCameraMockMvc.perform(get("/api/cameras/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCamera() throws Exception {
        // Initialize the database
        cameraRepository.saveAndFlush(camera);
        int databaseSizeBeforeUpdate = cameraRepository.findAll().size();

        // Update the camera
        Camera updatedCamera = cameraRepository.findOne(camera.getId());
        // Disconnect from session so that the updates on updatedCamera are not directly saved in db
        em.detach(updatedCamera);
        updatedCamera
            .name(UPDATED_NAME)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .rtspUrl(UPDATED_RTSP_URL)
            .channel(UPDATED_CHANNEL)
            .description(UPDATED_DESCRIPTION);

        restCameraMockMvc.perform(put("/api/cameras")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCamera)))
            .andExpect(status().isOk());

        // Validate the Camera in the database
        List<Camera> cameraList = cameraRepository.findAll();
        assertThat(cameraList).hasSize(databaseSizeBeforeUpdate);
        Camera testCamera = cameraList.get(cameraList.size() - 1);
        assertThat(testCamera.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCamera.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testCamera.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testCamera.getRtspUrl()).isEqualTo(UPDATED_RTSP_URL);
        assertThat(testCamera.getChannel()).isEqualTo(UPDATED_CHANNEL);
        assertThat(testCamera.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingCamera() throws Exception {
        int databaseSizeBeforeUpdate = cameraRepository.findAll().size();

        // Create the Camera

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCameraMockMvc.perform(put("/api/cameras")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(camera)))
            .andExpect(status().isCreated());

        // Validate the Camera in the database
        List<Camera> cameraList = cameraRepository.findAll();
        assertThat(cameraList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCamera() throws Exception {
        // Initialize the database
        cameraRepository.saveAndFlush(camera);
        int databaseSizeBeforeDelete = cameraRepository.findAll().size();

        // Get the camera
        restCameraMockMvc.perform(delete("/api/cameras/{id}", camera.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Camera> cameraList = cameraRepository.findAll();
        assertThat(cameraList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Camera.class);
        Camera camera1 = new Camera();
        camera1.setId(1L);
        Camera camera2 = new Camera();
        camera2.setId(camera1.getId());
        assertThat(camera1).isEqualTo(camera2);
        camera2.setId(2L);
        assertThat(camera1).isNotEqualTo(camera2);
        camera1.setId(null);
        assertThat(camera1).isNotEqualTo(camera2);
    }
}
