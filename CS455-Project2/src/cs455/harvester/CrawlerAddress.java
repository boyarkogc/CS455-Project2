package cs455.harvester;

public class CrawlerAddress {
	public String host;
	public int port;
	public String rootURL;
	
	public CrawlerAddress(String hostname, int port, String rootURL) {
		this.host = hostname;
		this.port = port;
		this.rootURL = rootURL;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getRootURL() {
		return rootURL;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}

}
