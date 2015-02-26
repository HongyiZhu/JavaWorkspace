package tbcrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;

import database.ISHCDataOperator;

public class Crawler {
	WebClient wc = null;
	ISHCDataOperator op = null;
	
	Crawler() {
		newClient();
		op = new ISHCDataOperator();
	}
	
	void newClient(){
		wc = new WebClient(BrowserVersion.CHROME);
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
//		wc.setProxyConfig(new ProxyConfig("127.0.0.1",3128));
		wc.setCssEnabled(false);
		wc.setJavaScriptEnabled(true);
		wc.setTimeout(35000);
		wc.setPrintContentOnFailingStatusCode(false);
		wc.setThrowExceptionOnScriptError(false);
		wc.setThrowExceptionOnFailingStatusCode(false);
	}
}
