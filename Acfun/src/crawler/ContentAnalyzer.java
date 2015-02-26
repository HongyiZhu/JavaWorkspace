package crawler;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

abstract public class ContentAnalyzer {
	protected HtmlPage page = null;
	protected final WebClient wc = new WebClient();
	protected String basePath = "E:/emoticon_research/";
	public void setting() {
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
		wc.setProxyConfig(new ProxyConfig("127.0.0.1",3128));
		wc.setCssEnabled(false);
		wc.setJavaScriptEnabled(true);
		wc.setTimeout(35000);
		wc.setPrintContentOnFailingStatusCode(false);
		wc.setThrowExceptionOnScriptError(false);
		wc.setThrowExceptionOnFailingStatusCode(false);
	}

	String restoreUrl(String root, File path) {
		return "http://" + path.getAbsolutePath().substring(root.length() + 1).replaceAll("\\\\", "/");
	}
	
	abstract public void getContent() throws FailingHttpStatusCodeException, MalformedURLException, IOException;
	
}
