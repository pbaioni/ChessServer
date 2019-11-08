package app.main.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="application")
public class ApplicationProperties {
	
	private String appProperty;
	

	
	public ApplicationProperties() {
		
	}

	public String getAppProperty() {
		return appProperty;
	}



	public void setAppProperty(String appProperty) {
		this.appProperty = appProperty;
	}

}
