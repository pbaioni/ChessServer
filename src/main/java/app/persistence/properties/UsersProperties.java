package app.persistence.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:users.properties")
@ConfigurationProperties(prefix="user")
public class UsersProperties {

	private String property1;
	
	private String property2;
	
	public UsersProperties() {
		
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	@Override
	public String toString() {
		return "UsersProperties [property1=" + property1 + ", property2=" + property2 + "]";
	}

}
