package cs455.harvester;

import java.net.MalformedURLException;
import java.net.URL;

public class urlParser {

	public static String convertToAbsolute(String rel, String abs) throws MalformedURLException {
		URL url = new URL(abs);
		return new URL(url, rel).toString();
    }
	
	public static void main(String[] args) throws MalformedURLException {
		System.out.println(convertToAbsolute("./index.php", "http://www.cs.colostate.edu/cstop/"));
	}

}
