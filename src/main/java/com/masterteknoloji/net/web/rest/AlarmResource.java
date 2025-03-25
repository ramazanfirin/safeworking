package com.masterteknoloji.net.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.masterteknoloji.net.config.ApplicationProperties;
import com.masterteknoloji.net.domain.Alarm;

import com.masterteknoloji.net.repository.AlarmRepository;
import com.masterteknoloji.net.web.rest.errors.BadRequestAlertException;
import com.masterteknoloji.net.web.rest.util.HeaderUtil;
import com.masterteknoloji.net.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.undertow.server.handlers.form.FormData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing Alarm.
 */
@RestController
@RequestMapping("/api")
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    private static final String ENTITY_NAME = "alarm";

    private final AlarmRepository alarmRepository;
    
    private final ApplicationProperties applicationProperties;

    public AlarmResource(AlarmRepository alarmRepository,ApplicationProperties applicationProperties) {
        this.alarmRepository = alarmRepository;
        this.applicationProperties = applicationProperties;
    }

    /**
     * POST  /alarms : Create a new alarm.
     *
     * @param alarm the alarm to create
     * @return the ResponseEntity with status 201 (Created) and with body the new alarm, or with status 400 (Bad Request) if the alarm has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/alarms")
    @Timed
    public ResponseEntity<Alarm> createAlarm(@RequestBody Alarm alarm) throws URISyntaxException {
        log.debug("REST request to save Alarm : {}", alarm);
        if (alarm.getId() != null) {
            throw new BadRequestAlertException("A new alarm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Alarm result = alarmRepository.save(alarm);
        return ResponseEntity.created(new URI("/api/alarms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /alarms : Updates an existing alarm.
     *
     * @param alarm the alarm to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated alarm,
     * or with status 400 (Bad Request) if the alarm is not valid,
     * or with status 500 (Internal Server Error) if the alarm couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/alarms")
    @Timed
    public ResponseEntity<Alarm> updateAlarm(@RequestBody Alarm alarm) throws URISyntaxException {
        log.debug("REST request to update Alarm : {}", alarm);
        if (alarm.getId() == null) {
            return createAlarm(alarm);
        }
        Alarm result = alarmRepository.save(alarm);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, alarm.getId().toString()))
            .body(result);
    }

    /**
     * GET  /alarms : get all the alarms.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of alarms in body
     */
    @GetMapping("/alarms")
    @Timed
    public ResponseEntity<List<Alarm>> getAllAlarms(Pageable pageable) {
        log.debug("REST request to get a page of Alarms");
        Page<Alarm> page = alarmRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/alarms");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /alarms/:id : get the "id" alarm.
     *
     * @param id the id of the alarm to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the alarm, or with status 404 (Not Found)
     */
    @GetMapping("/alarms/{id}")
    @Timed
    public ResponseEntity<Alarm> getAlarm(@PathVariable Long id) {
        log.debug("REST request to get Alarm : {}", id);
        Alarm alarm = alarmRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(alarm));
    }

    /**
     * DELETE  /alarms/:id : delete the "id" alarm.
     *
     * @param id the id of the alarm to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/alarms/{id}")
    @Timed
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long id) {
        log.debug("REST request to delete Alarm : {}", id);
        alarmRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    
    @PostMapping("/alarms/createAlarm")
    public void handlePostRequest(
            @RequestParam(value = "picture", required = false) MultipartFile file,
            @RequestParam Map<String, String> allParams) {
    	
    	System.out.println("geldi");
    	
    	if (file != null && !file.isEmpty()) {
            String uploadDirectory = applicationProperties.getSaveDirectoryPath();
            File targetFile = new File(uploadDirectory + file.getOriginalFilename());
            
            try {
                file.transferTo(targetFile);
                log.info("dosya kaydedildi");
                
                Alarm alarm = new Alarm();
                alarm.setInsertDate(ZonedDateTime.now());
                
                alarmRepository.save(alarm);
                
            } catch (IOException e) {
                e.printStackTrace();
                log.info("dosya kaydedilirken hata oluştu");
               
            }
        } else {
        	log.info("dosya gelmedi");
        }
    	
    	return;
    }
    
    //alarm_info={"additional":{"alarm_major":"alert_alarm","alarm_minor":"CALL","stream_id":1,"channel_type":3,"channel_id":0,"device_id":1,"monitor_id":102,"device_name":"testCamera","alarm_time":"1742901125592"},"global_info":{"message_type":"MSDP","version":"V2.3.3.B19_426","time_ms":"1742901125592","device_id":"M030201202210000148","data_uuid":"6504431:1742901125592"},"full_images":[{"original_resolution":{"width_pixels":1920,"height_pixels":1080},"image_data":{"image_data_format":1,"value":"1"}}],"warehouseV20Events":{"alarmEvents":[{"areaIds":[1],"eventType":"CALL","labels":{},"level":"ALARM_LEVEL","ruleId":1,"subType":"","targets":[{"areaId":1,"points":[{"x":0.44017016887664795,"y":0.071169525384902954},{"x":0.546575129032135,"y":0.55017679929733276}],"targetId":"PERSON:429:0","targetScore":0.97759515047073364,"targetType":"PERSON","trackId":"429"}],"areaTypes":["POLYGON"],"msdpCustom":{"imageData":{"format":"INDEX_FORMAT","value":"0"},"areaTypes":["POLYGON"],"trackId":"6504431"}}],"algoCabinName":"alert_alarm","version":"V2.0.0","uri":"2416825.jpg","frameId":2416825,"pts":1742901125592,"recvTs":1742901125592}}
}
