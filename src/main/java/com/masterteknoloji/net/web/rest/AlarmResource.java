package com.masterteknoloji.net.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterteknoloji.net.domain.Alarm;
import com.masterteknoloji.net.domain.Camera;
import com.masterteknoloji.net.domain.Device;
import com.masterteknoloji.net.domain.enumeration.AlarmType;
import com.masterteknoloji.net.repository.AlarmRepository;
import com.masterteknoloji.net.repository.CameraRepository;
import com.masterteknoloji.net.repository.DeviceRepository;
import com.masterteknoloji.net.web.rest.errors.BadRequestAlertException;
import com.masterteknoloji.net.web.rest.util.HeaderUtil;
import com.masterteknoloji.net.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import liquibase.pro.packaged.ca;

/**
 * REST controller for managing Alarm.
 */
@RestController
@RequestMapping("/api")
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    private static final String ENTITY_NAME = "alarm";

    private final AlarmRepository alarmRepository;
    
    private final DeviceRepository deviceRepository;
    
    private final CameraRepository cameraRepository;

    public AlarmResource(AlarmRepository alarmRepository,DeviceRepository deviceRepository,CameraRepository cameraRepository) {
        this.alarmRepository = alarmRepository;
        this.deviceRepository = deviceRepository;
        this.cameraRepository = cameraRepository;
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
    		 @RequestParam Map<String, MultipartFile> files,
            @RequestPart(value = "picture", required = false) List<MultipartFile> multipartfiles,  // Dosya
            
            @RequestParam Map<String, String> allParams,HttpServletRequest request
            ) throws IOException {
    	
    	
    	 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    	 
    	 
    	  Alarm alarm = new Alarm();
          alarm.setInsertDate(ZonedDateTime.now());
          //alarm.setImageFile(targetFile.getAbsolutePath());
          //alarm.setImage(file.getBytes());
          //alarm.setAlarmType(getAlarmType(uploadDirectory))
         
          if(allParams.get("alarm_info") != null) {
        	  setParameters(alarm,allParams.get("alarm_info"));
          }
    	 
    	System.out.println("geldi");
    	
    	if(multipartfiles.size()>0) {
    		MultipartFile file = multipartfiles.get(0);
    		alarm.setImage(file.getBytes());
    		alarm.setImageContentType(file.getContentType());
    	}
    	
    	if(multipartfiles.size()>1) {
    		MultipartFile file = multipartfiles.get(1);
    		alarm.setBackGroundImage(file.getBytes());
    		alarm.setBackGroundImageContentType(file.getContentType());
    	}    	
    	 alarmRepository.save(alarm); 
    	
    	
    	return;
    }
    
    public void setParameters(Alarm alarm,String value) {
    	ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode alarmInfo = objectMapper.readTree(value);
			JsonNode jsonObject = alarmInfo.get("additional");
			if(jsonObject == null)
				return;
			
			
			String alarmType = getJsonValue(jsonObject, "alarm_minor");
			alarm.setAlarmType(alarmType);
			AlarmType type = AlarmType.valueOf(alarmType);
			alarm.setAlarmTypeValue(type);
			
			String deviceName = getJsonValue(jsonObject, "device_name");
			alarm.setCamera(getCamera(deviceName));
			
			JsonNode globalInfo = alarmInfo.get("global_info");
			if(globalInfo != null) {
				String deviceId = getJsonValue(globalInfo, "device_id");
				alarm.setDevice(getDevice(deviceId));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public String getJsonValue(JsonNode jsonObject,String parameter) {
    	JsonNode node = jsonObject.get(parameter);
    	if(node != null) {
    		return node.asText();
    	}
    	return null;
    }
    
    public Device getDevice(String deviceId) {
    	Optional<Device> device = deviceRepository.findDeviceSerialNumber(deviceId);
  		if(device.isPresent())
  			return device.get();
    	
    	return null;
    }
    
	public Camera getCamera(String cameraName) {

		Optional<Camera> device = cameraRepository.findByCameraName(cameraName);
		if (device.isPresent())
			return device.get();

		return null;
	}
    
        
    //alarm_info={"additional":{"alarm_major":"alert_alarm","alarm_minor":"CALL","stream_id":1,"channel_type":3,"channel_id":0,"device_id":1,"monitor_id":102,"device_name":"testCamera","alarm_time":"1742901125592"},"global_info":{"message_type":"MSDP","version":"V2.3.3.B19_426","time_ms":"1742901125592","device_id":"M030201202210000148","data_uuid":"6504431:1742901125592"},"full_images":[{"original_resolution":{"width_pixels":1920,"height_pixels":1080},"image_data":{"image_data_format":1,"value":"1"}}],"warehouseV20Events":{"alarmEvents":[{"areaIds":[1],"eventType":"CALL","labels":{},"level":"ALARM_LEVEL","ruleId":1,"subType":"","targets":[{"areaId":1,"points":[{"x":0.44017016887664795,"y":0.071169525384902954},{"x":0.546575129032135,"y":0.55017679929733276}],"targetId":"PERSON:429:0","targetScore":0.97759515047073364,"targetType":"PERSON","trackId":"429"}],"areaTypes":["POLYGON"],"msdpCustom":{"imageData":{"format":"INDEX_FORMAT","value":"0"},"areaTypes":["POLYGON"],"trackId":"6504431"}}],"algoCabinName":"alert_alarm","version":"V2.0.0","uri":"2416825.jpg","frameId":2416825,"pts":1742901125592,"recvTs":1742901125592}}

    @GetMapping("/alarms/search")
    @Timed
    public ResponseEntity<List<Alarm>> searchAlarms(
            @RequestParam(required = false) Long personId,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) Boolean falseAlarm,
            @RequestParam(required = false) Boolean processed,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        log.debug("REST request to search Alarms with filters: personId={}, alarmType={}, falseAlarm={}, processed={}, startDate={}, endDate={}",
                personId, alarmType, falseAlarm, processed, startDate, endDate);

        AlarmType enumAlarmType = null;
        if (alarmType != null) {
            try {
                enumAlarmType = AlarmType.valueOf(alarmType);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid alarm type: {}", alarmType);
            }
        }

        ZonedDateTime startDateTime = null;
        ZonedDateTime endDateTime = null;
        if (startDate != null) {
            startDateTime = ZonedDateTime.parse(startDate);
        }
        if (endDate != null) {
            endDateTime = ZonedDateTime.parse(endDate);
        }

        Page<Alarm> page = alarmRepository.searchAlarms(
                personId,
                enumAlarmType,
                falseAlarm,
                processed,
                startDateTime,
                endDateTime,
                pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/alarms/search");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
