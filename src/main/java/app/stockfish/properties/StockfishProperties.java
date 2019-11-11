package app.stockfish.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:stockfish.properties")
@ConfigurationProperties(prefix="stockfish")
public class StockfishProperties {

	private int instances;
	
	private int threads;
	
	private int depth;
	
	private int skills;
	
	private String enginePath;
	
	private int hash;
	
	private int multipv;
	
	public StockfishProperties() {
		
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getSkills() {
		return skills;
	}

	public void setSkills(int skills) {
		this.skills = skills;
	}

	public String getEnginePath() {
		return enginePath;
	}

	public void setEnginePath(String enginePath) {
		this.enginePath = enginePath;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getMultipv() {
		return multipv;
	}

	public void setMultipv(int multipv) {
		this.multipv = multipv;
	}

	@Override
	public String toString() {
		return "StockfishProperties [instances=" + instances + ", threads=" + threads + ", depth=" + depth + ", skills="
				+ skills + ", enginePath=" + enginePath + ", hash=" + hash + ", multipv=" + multipv + "]";
	}

}
