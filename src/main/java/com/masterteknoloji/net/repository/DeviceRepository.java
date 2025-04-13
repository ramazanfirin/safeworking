package com.masterteknoloji.net.repository;

import com.masterteknoloji.net.domain.Device;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Device entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
	
	@Query("SELECT d FROM Device d WHERE d.serialNumber = :serialNumber")
	Optional<Device> findDeviceSerialNumber(@Param("serialNumber") String serialNumber);

}
