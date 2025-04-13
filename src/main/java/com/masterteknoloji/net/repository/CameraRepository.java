package com.masterteknoloji.net.repository;

import com.masterteknoloji.net.domain.Camera;
import com.masterteknoloji.net.domain.Device;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Camera entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
	
	@Query("SELECT d FROM Camera d WHERE d.name = :name")
	Optional<Camera> findByCameraName(@Param("name") String vame);
}
