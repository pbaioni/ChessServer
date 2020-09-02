package app.stockfish.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:stockfish.properties")
@ConfigurationProperties(prefix="stockfish")
public class StockfishProperties {

	private String instances;
	
	private String threads;
	
	private String depth;
	
	private String skills;
	
	private String enginePath;
	
	private String hash;
	
	private String multipv;
	
	private String ownbook;
	
	public StockfishProperties() {
		
	}

	public String getInstances() {
		return instances;
	}

	public void setInstances(String instances) {
		this.instances = instances;
	}

	public String getThreads() {
		return threads;
	}

	public void setThreads(String threads) {
		this.threads = threads;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getEnginePath() {
		return enginePath;
	}

	public void setEnginePath(String enginePath) {
		this.enginePath = enginePath;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getMultipv() {
		return multipv;
	}

	public void setMultipv(String multipv) {
		this.multipv = multipv;
	}

	public String getOwnbook() {
		return ownbook;
	}

	public void setOwnbook(String ownbook) {
		this.ownbook = ownbook;
	}

	@Override
	public String toString() {
		return "StockfishProperties [instances=" + instances + ", threads=" + threads + ", depth=" + depth + ", skills="
				+ skills + ", enginePath=" + enginePath + ", hash=" + hash + ", multipv=" + multipv + ", ownbook="
				+ ownbook + "]";
	}

}
