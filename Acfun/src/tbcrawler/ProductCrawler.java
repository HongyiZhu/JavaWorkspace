package tbcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlDefinitionList;


public class ProductCrawler extends Crawler{
	Set<String> products = null;
	List<String[]> shopList = null;
	
	ProductCrawler() {
		super();
		products = new HashSet<String>();
		shopList = new ArrayList<String[]>();
	}
	
	private void getShopList() {
		ResultSet rs = op.query("SELECT `shopURL`,`userID` FROM `shopinfo` WHERE `hasList` = 0  LIMIT 0, 1");
		try {
			while (rs.next()){
				String[] temp = {rs.getString("shopURL"), rs.getString("userID")};
				shopList.add(temp);
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public void execute() {
		getShopList();
		for (String[] strList : shopList){
			getProduct(strList[0], strList[1]);
		}
		newClient();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public void getProduct(String shopURL, String userID) {
		products.clear();
		HtmlPage hp;
		System.out.println(shopURL + "\t" + userID);
		String href = shopURL + "/search.htm?search=y&pageNo=1";
		while(true){
			try {
				boolean pagination = false;
				hp = wc.getPage(href);
				if (hp.getUrl().getHost().split("\\.")[0].equals("login")){
					HtmlInput hiUser = (HtmlInput) hp.getElementById("TPL_username_1");
					hiUser.setAttribute("value", "ailabtucson");
					HtmlInput hiSafe = (HtmlInput) hp.getElementById("J_SafeLoginCheck");
					hiSafe.removeAttribute("value");
					HtmlInput hiPass = (HtmlInput) hp.getElementById("TPL_password_1");
					hiPass.setAttribute("value", "tucson2014");
					HtmlButton hbSubmit = (HtmlButton) hp.getElementById("J_SubmitStatic");
					Thread.sleep(300);
					hp = hbSubmit.click();
				}
//				HtmlDivision bd = (HtmlDivision) hp.getElementById("bd");
				List<DomElement> divList = hp.getElementsByTagName("div");
				for (DomElement de: divList){
					if (((HtmlDivision)de).hasAttribute("class") && ((HtmlDivision)de).getAttribute("class").equals("pagination")){
						List<HtmlElement> aList = de.getElementsByTagName("a");
						for (HtmlElement he : aList){
							if (((HtmlAnchor)he).hasAttribute("class") && ((HtmlAnchor)he).getAttribute("class").equals("J_SearchAsync next")){
								pagination = true;
								href = ((HtmlAnchor)he).getHrefAttribute();
								System.out.println(href);
							}
						}
						break;
					}
				}
				List<DomElement> dlList = hp.getElementsByTagName("dl"); 
				if (pagination){
					for (DomElement de : dlList){
						products.add(((HtmlDefinitionList)de).getAttribute("data-id"));
						System.out.println(((HtmlDefinitionList)de).getAttribute("data-id"));
					}
				} else {
					System.out.println(hp.asXml());
					System.out.println(hp.getUrl());
					System.out.println("Last page! " + href);
					break;
				}
				Thread.sleep(30);
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
		if (products.size() != 0){
			BufferedWriter bw = null;
			try {
				File output = new File("E:/taobao/product/" + userID + ".txt");
				bw = new BufferedWriter(new FileWriter(output));
				for (String s : products){
					bw.write(s);
					bw.newLine();
				}
				bw.close();
				op.update("UPDATE `shopinfo` SET `hasList`= 1 WHERE `shopURL`='" + shopURL + "'");
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ProductCrawler pc = new ProductCrawler();
//		pc.execute();
		pc.getProduct("http://shop35127038.taobao.com", "66280588");
	}
}
