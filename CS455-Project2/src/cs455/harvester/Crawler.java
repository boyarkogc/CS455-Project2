package cs455.harvester;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.htmlparser.jericho.*;
import java.io.*;
import java.net.*;

public class Crawler {
	public static final int maxDepth = 5;
	
	public int port;
	public String rootURL;
	public int numThreads;
	
	public List<Thread> threads;
	public LinkedList<Task> tasks;
	public ArrayList<Node> graph;
	public ArrayList<CrawlerAddress> crawlers;
	
	//Constructor used to create the local Crawler
	public Crawler(int port, int threadPoolSize, String rootURL, String configFile) throws NumberFormatException, IOException, InterruptedException {
		this.port = port;
		this.rootURL = "";
		String[] base = rootURL.split("/");
		for (int i = 0; i < base.length - 1; i++) {
			this.rootURL += base[i] + "/";
		}
		this.rootURL = this.rootURL.substring(0, this.rootURL.length() - 1);
		this.numThreads = threadPoolSize;
		
		threads = new ArrayList<Thread>();
		tasks = new LinkedList<Task>();
		graph = new ArrayList<Node>();
		crawlers = new ArrayList<CrawlerAddress>();
		
		//Read config file and put other machines' info into crawlers arrayList
		FileReader input = new FileReader(configFile);
		BufferedReader bufRead = new BufferedReader(input);
		String line = null;
		while ((line = bufRead.readLine()) != null) {    
			String[] array1 = line.split(":");
			String[] array2 = array1[1].split(",");
			CrawlerAddress crawler = new CrawlerAddress(array1[0], Integer.parseInt(array2[0]), array2[1] + ":" + array1[2]);
			crawlers.add(crawler);
			System.out.println(crawler.getHost() + " " + crawler.getPort() + " " + crawler.getRootURL());
		}
		
		graph.add(new Node(rootURL));
		tasks.add(new Task(rootURL, 1));
		initialize(threadPoolSize);
		manageThreads();
	}
	
	public void initialize(int numThreads) {
		for (int i = 0; i < numThreads; i++) {
		      Runnable task = new CrawlerThread();
		      Thread worker = new Thread(task);
		      // We can set the name of the thread
		      worker.setName(String.valueOf(i));
		      // Start the thread, never call method run() direct
		      worker.start();
		      // Remember the thread for later usage
		      threads.add(worker);
		}
	}
	
	public void manageThreads() throws InterruptedException {
		int running = 0;
		do {
			Thread.sleep(1000);
			running = 0;
			for (Thread thread : threads) {
		    	if (thread.isAlive()) {
		    		running++;
		    	}
		    }
			synchronized(tasks) {
				if (!tasks.isEmpty()) {
					tasks.notify();
				}
			}
		}while (running > 0);
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		new Crawler(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3]);
		ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]));
		socket.accept();
	}
	
	
	
	private class CrawlerThread implements Runnable {
		
		public void run() {
			Task task;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {}
			
			while (true) {
				synchronized(tasks) {
					try {
						tasks.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task = tasks.poll();
				}
				int index = graph.indexOf(new Node(task.getUrl()));
				ArrayList<String> links = parseWebpage(task.getUrl());
				synchronized (tasks) {
					for (int i = 0; i < links.size(); i++) {
						Node tempNode = new Node(links.get(i));
						synchronized (graph) {
							if (!graph.contains(tempNode)) {
								graph.add(tempNode);
								graph.get(index).addEdge(tempNode.getURL());
								Task temp = new Task(links.get(i), task.getDepth() + 1);
								if (task.getDepth() < maxDepth) {
									tasks.add(temp);
								}
								//System.out.println(temp.getUrl());
							}
						}
					}
				}
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {}
			}
		}
		//returns arrayList of links from given url
		//also calls handOff on any links that lead to domains that other crawlers are crawling
		public ArrayList<String> parseWebpage(String url) {
			ArrayList<String> links = new ArrayList<String>();
			// disable verbose log statements
			Config.LoggerProvider = LoggerProvider.DISABLED;
			
			try {
				// web page that needs to be parsed
				final String pageUrl = resolveRedirects(url);
				Source source = new Source(new URL(pageUrl));
				// get all 'a' tags
				List<Element> aTags = source.getAllElements(HTMLElementName.A);
				// get the URL ("href" attribute) in each 'a' tag
				for (Element aTag : aTags) {
					if (aTag.getAttributeValue("href") != null) {
						String ele = aTag.getAttributeValue("href").toString();
						if (ele.contains("http")) {
							if (checkDomain(ele, rootURL)) {
								links.add(ele);
							}else {
								for (int i = 0; i < crawlers.size(); i++) {
									if (checkDomain(ele, crawlers.get(i).getRootURL())) {
										handOff(ele, crawlers.get(i).getHost());
										break;
									}
								}
							}
						}else if (!ele.contains("mailto:")){
							links.add(convertToAbsolute(ele, pageUrl));
						}
					}
				}
			} catch (IOException e) { // in case of malformed url
				//System.err.println(e.getMessage());
			}
			return links;
		}
		//returns url of possible redirect
	    public String resolveRedirects(String url) throws IOException {
	        HttpURLConnection con = (HttpURLConnection)(new URL(url).openConnection());
	        con.setInstanceFollowRedirects(false);
	        con.connect();
	        int responseCode = con.getResponseCode();
	        if(responseCode == 301){
	            return con.getHeaderField( "Location" );
	        } else {
	            return url;
	        }
	    }
	    //checks to see if page is within the given domain; we don't want to crawl outside of our scope
	    public boolean checkDomain(String pageUrl, String rootUrl) throws MalformedURLException {
	    	if (rootUrl.contains("colostate.edu/Depts/Psychology/")) {
	    		return pageUrl.contains("colostate.edu/Depts/Psychology/");
	    	}
	        return new URL(pageUrl).getHost().equals(new URL(rootUrl).getHost());
	    }
	    
	    public void handOff(String url, String host) {
	    	
	    }
	    
	    public String convertToAbsolute(String rel, String abs) throws MalformedURLException {
			URL url = new URL(abs);
			return new URL(url, rel).toString();
	    }
	}
}