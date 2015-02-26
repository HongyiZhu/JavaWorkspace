package tbcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

import dao.ShopinfoDAO;
import database.ISHCDataOperator;

public class ShopCrawler extends Crawler{
	
	ShopCrawler() {
		super();
	}
	
	public void getShopList() {		
		for (int i = 0; i < 100; i++){
			try {
				HtmlPage hp = (HtmlPage) wc.getPage("http://s.taobao.com/search?app=shopsearch&q=%E5%8A%A8%E6%BC%AB&s=" + i * 20);
				HtmlUnorderedList ul = (HtmlUnorderedList) hp.getElementById("list-container");
				for (DomElement de : ul.getChildElements()){
					ShopinfoDAO dao = new ShopinfoDAO();
					DomElement tempDE = null;
					if (de.getChildElementCount() == 2){
						tempDE = de.getFirstElementChild().getFirstElementChild().getNextElementSibling().getFirstElementChild().getFirstElementChild();
						HtmlAnchor ha = (HtmlAnchor) tempDE;
						dao.url = ha.getHrefAttribute();
						dao.userID = ha.getAttribute("data-uid");
						if (tempDE.getNextElementSibling().hasAttribute("title") && tempDE.getNextElementSibling().getAttribute("title").equals("天猫")){
							dao.isTmall = 1;
						}
						System.out.println(ha.getAttribute("data-uid") + "\t" + ha.getHrefAttribute());
					} else {
						tempDE = de.getFirstElementChild();
						HtmlTextArea hta = (HtmlTextArea) tempDE;
						String str = hta.asText();
						
						String regex = "a trace=\\\"shop\\\" data-uid=\\\"(.*)\\\" href=\\\"(.+)\\\" title=.*";
						Pattern p = Pattern.compile(regex);
						Matcher m = p.matcher(str);
						if (m.find()){
							dao.url = m.group(2);
							dao.userID = m.group(1);
							System.out.println(m.group(1) + "\t" + m.group(2));
						}
						
						String regex2 = "a trace=\\\"shop\\\" data-uid=\\\"(.*)\\\" title=\\\"天猫\\\".*";
						Pattern p2 = Pattern.compile(regex2);
						Matcher m2 = p2.matcher(str);
						if (m2.find()){
							dao.isTmall = 1;
						}
					}
					
					op.insert(dao);
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
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			newClient();
			System.out.println("++++++ Page " + i + " +++++++");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		ShopCrawler sc = new ShopCrawler();
		sc.getShopList();
	}
}
