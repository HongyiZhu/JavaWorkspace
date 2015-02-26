package tbcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Test {
	
	public static void main(String[] args) {
		WebClient wc = new WebClient();
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
		wc.setProxyConfig(new ProxyConfig("127.0.0.1",3128));
		wc.setCssEnabled(false);
		wc.setJavaScriptEnabled(true);
		wc.setTimeout(35000);
		wc.setPrintContentOnFailingStatusCode(false);
		wc.setThrowExceptionOnScriptError(false);
		wc.setThrowExceptionOnFailingStatusCode(false);
		
		try {
			HtmlPage hp = wc.getPage("http://shop63786325.taobao.com");
			System.out.println(hp.getUrl().getHost().split("\\.")[1]);
		} catch (FailingHttpStatusCodeException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
