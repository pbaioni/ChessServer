package app.commands.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:command.properties")
@ConfigurationProperties(prefix="command")
public class CommandProperties {
	
	private boolean start;
	
	public CommandProperties() {
		
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	@Override
	public String toString() {
		return "CommandProperties [start=" + start + "]";
	}

}
