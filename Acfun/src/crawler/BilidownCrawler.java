package crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.sql.BatchUpdateException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLIFrameElement;

import dao.VideoDAO;
import database.ISHCDataOperator;

public class BilidownCrawler extends ContentAnalyzer {

	@Override
	public void getContent() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		// TODO Auto-generated method stub
		getContent(7, 20);
	}
	
	public void getContent(int start) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		getContent(start, start + 1);
	}
	public void getContent(int start, int end) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		
		String base = basePath + "www.bilibili.tv/";
		File root = new File(base + "www.bilibili.tv");
		File output = new File(base + "log.txt");
		PrintWriter pw = null;
		
		ISHCDataOperator op = new ISHCDataOperator();
		VideoDAO.configure("http://www.bilibili.tv/", "001", "2");
		
		HtmlDivision div = null;
		HtmlHeading2 h2 = null;
		HtmlAnchor ha = null;
		HtmlItalic hi = null;
		HtmlUnorderedList ul = null;
		String text = null;
		DomNodeList<DomNode> list = null;
		Pattern p = null;
		Matcher m = null;
		
		try {
			for (int av = start; av < end; av++) {
				VideoDAO video = new VideoDAO();
				try {
					page = wc.getPage("http://bilibili.kankanews.com/video/av" + av + "/");
					h2 = (HtmlHeading2) page.getByXPath("/html/body/div[5]/div[2]/div[1]/h2").get(0);
					video.title = h2.asText();
					div = (HtmlDivision) page.getByXPath("/html/body/div[5]/div[2]/div[1]/div[2]").get(0);
	
					list = div.getChildNodes();
					
	//				for (DomNode dn : list) {
	//					System.out.println(dn.asText());
	//				}
					text = list.get(0).asText()
							+ ">" + list.get(2).asText()
							+ ">" + list.get(4).asText();
					video.path = text;
					
					video.url = page.getUrl().toString();
					text = video.url;
					System.out.println(text);
					p = Pattern.compile(".*av(\\d+).*");
					m = p.matcher(video.url);
					if (m.matches())
						System.out.println("!!");
					video.ID = new BigInteger("0010200000000000000").add(new BigInteger(m.group(1)));
					
					System.out.println(list.get(6).asText());
					//(\\d*)-(\\d*)-(\\d*)\\s*(\\d*):(\\d*)
					p = Pattern.compile("(\\d*)-(\\d*)-(\\d*)\\s*(\\d*):(\\d*).*");
					m = p.matcher(list.get(6).asText());
					if (m.matches())
						System.out.println("!! matches");
					if (!m.group(1).isEmpty() && !m.group(2).isEmpty() && !m.group(3).isEmpty())
						video.date = new Date(Integer.parseInt(m.group(1)) - 1900, Integer.parseInt(m.group(2)) - 1, Integer.parseInt(m.group(3)));
					if (!m.group(4).isEmpty() && !m.group(5).isEmpty())
						video.time = new Time(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), 0);
					
					video.view = tenThousand(list.get(7).asText());
					video.favor = tenThousand(list.get(8).asText());
					
					p = Pattern.compile("(\\d*)\\[(\\d*)\\]");
					m = p.matcher(list.get(9).asText());
					if (m.matches()) {
						video.coin = tenThousand(m.group(1));
						video.point = tenThousand(m.group(2));
					}
					
					ul = (HtmlUnorderedList) page.getByXPath("/html/body/div[5]/div[5]/div[1]/div[1]/ul").get(0);
					
					list = ul.getChildNodes();
					text = "";
					for (DomNode dn : list) {
						text += dn.asText() + ";;";
					}
					if (!text.isEmpty()) {
						text = text.substring(0, text.length() - 2);
					}
					video.tag = text;
					
					div = (HtmlDivision) page.getByXPath("/html/body/div[5]/div[5]/div[1]/div[2]").get(0);
					video.intro = div.asText().trim();
					
					ha = (HtmlAnchor) page.getByXPath("//*[@id=\"r-info-rank\"]/a").get(0);
					if (ha.getHrefAttribute().endsWith(".html"))
						ha = (HtmlAnchor) page.getByXPath("//*[@id=\"r-info-rank\"]/a").get(1);
					
					p = Pattern.compile("http://space.bilibili.tv/(\\d+)");
					System.out.println(ha.getHrefAttribute());
					m = p.matcher(ha.getHrefAttribute());
					if (m.matches())
						System.out.println("!! matches");
					if (m.matches())
						video.author = (new BigInteger(m.group(1))).add((new BigInteger("0010300000000000000")));
					
					div = (HtmlDivision) page.getByXPath("//*[@id=\"bofqi\"]").get(0);
					text = div.asXml();
//					System.out.println(text);
					p = Pattern.compile(".*cid=(\\d+).*", Pattern.DOTALL);
					m = p.matcher(text);
					if (m.matches())
						System.out.println("!! cid matches");
					if (m.matches())
						video.danmaku_file_id = Integer.parseInt(m.group(1));
					
					op.insert(video);
					
				} catch (IndexOutOfBoundsException e) {
					System.out.println("This video does not exist");
					e.printStackTrace();
					continue;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread.sleep(100);
			}
			op.emptyCache();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			op.printTerminatePoint();
			op.disconnect();
		}
//		HtmlImage hi = (HtmlImage) page.getByXPath("/html/body/div[5]/div[8]/div[1]/div[2]/img").get(0);
//		page = (HtmlPage) hi.click();
//		System.out.println(page.asText());
	}
	
	public int tenThousand(String text) {
		try {
			if (text.endsWith("万")) {
				double dbl = Double.parseDouble(text.substring(0, text.length() - 1));
				return (int) (dbl * 10000);
			} else {
				return Integer.parseInt(text);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static void main(String[] args) {
		VideoCrawler c = new VideoCrawler();
		c.setting();
		int start = 110589, end = start + 10000;
		try {
			c.getContent(start, end);
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
