package crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.*;
import org.json.zip.*;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class Test {
	public static void main(String[] args) {
		String a = String.valueOf(1234);
		System.out.println(a.split("\\.")[0]);
	}
}
