package cs455.harvester;

public class Task {
	String url;
	int depth;
	
	public Task(String url, int depth) {
		this.url = url;
		this.depth = depth;
	}

	public String getUrl() {
		return url;
	}

	public int getDepth() {
		return depth;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}
