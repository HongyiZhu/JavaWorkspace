package tbcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CommentCrawler extends Crawler{
	CommentCrawler() {
		super();
	}
	
	public void getComment(String userID, String productID) {
		try {
			HtmlPage hp = wc.getPage("http://rate.taobao.com/detail_rate.htm?userNumId=" + userID 
					+ "&auctionNumId=" + productID + "&showContent=1&currentPage=0");
			String str = hp.asText();
//			System.out.println(str);
			String regex = ".*\\\"lastPage\\\":(\\d+),.*";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(str);
			int lastPage = -1;
			if (m.find()){
				lastPage = Integer.parseInt(m.group(1));
			}
			for (int i = 1;i <= lastPage;i++){
				newClient();
				hp = wc.getPage("http://rate.taobao.com/detail_rate.htm?userNumId=" + userID 
						+ "&auctionNumId=" + productID + "&showContent=1&currentPage=" + i);
				str = hp.asText();
				//TODO output
				Thread.sleep(500);
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		CommentCrawler cc = new CommentCrawler();
//		cc.getComment(userID, productID);
	}
}
