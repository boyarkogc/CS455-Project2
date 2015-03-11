package cs455.harvester;

import net.htmlparser.jericho.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
/**
* This class demonstrate
s how to use the Jericho HTML parser
* to extract URLs of a given web pages.
*/
public class URLExtractor {
	public static void main(String[] args) {
		// disable verbose log statements
		Config.LoggerProvider = LoggerProvider.DISABLED;
		try {
			// web page that needs to be parsed
			final String pageUrl = args[2];
			Source source = new Source(new URL(pageUrl));
			// get all 'a' tags
			List<Element> aTags = source.getAllElements(HTMLElementName.A);
			// get the URL ("href" attribute) in each 'a' tag
			for (Element aTag : aTags) {
				// print the url
				System.out.println(aTag.getAttributeValue("href"));
			}
		} catch (IOException e) { // in case of malformed url
			System.err.println(e.getMessage());
		}
	}
}