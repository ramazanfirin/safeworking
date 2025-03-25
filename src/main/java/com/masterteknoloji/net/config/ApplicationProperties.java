package com.masterteknoloji.net.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Safeworking.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
	
	private String saveDirectoryPath;

	public String getSaveDirectoryPath() {
		return saveDirectoryPath;
	}

	public void setSaveDirectoryPath(String saveDirectoryPath) {
		this.saveDirectoryPath = saveDirectoryPath;
	}
	
	

}
