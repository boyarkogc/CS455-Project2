package cs455.harvester;

import java.util.ArrayList;

public class Node {
	String url;
	ArrayList<String> edges;
	
	public Node(String url) {
		this.url = url;
		edges = new ArrayList<String>();
	}
	
	public void addEdge(String edge) {
		edges.add(edge);
	}
	
	public String getURL() {
		return url;
	}
	
	public ArrayList<String> getEdges() {
		return edges;
	}
	
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Node)) {
            return false;
       }
       Node node = (Node) obj;
       if (node.getURL().equals(url)) {
    	   return true;
       }
       return false;
    }
}
