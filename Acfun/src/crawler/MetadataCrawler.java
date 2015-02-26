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

public class MetadataCrawler extends ContentAnalyzer {
	
	@Override
	public void getContent() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		// TODO Auto-generated method stub
		getContent(7, 20);
	}
	
	public void getContent(int start, int end) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		
		String base = basePath + "www.acfun.tv/";
		File root = new File(base + "www.acfun.tv");
		File output = new File(base + "log.txt");
		PrintWriter pw = null;
		
		ISHCDataOperator op = new ISHCDataOperator();
		VideoDAO.configure("http://www.acfun.tv/", "002", "0");
		
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
			for (int ac = start; ac < end; ac++) {
				int cid = -1;
				try {
					page = wc.getPage("http://www.acfun.tv/v/ac" + ac + "/");
					div = (HtmlDivision) page.getByXPath("//*[@id=\"bofqi\"]").get(0);
					text = div.asXml();
					System.out.println(text);
					p = Pattern.compile(".*cid=(\\d+).*", Pattern.DOTALL);
					m = p.matcher(text);
					
					if (m.matches()) {
						System.out.println("!! cid matches");
						cid = Integer.parseInt(m.group(1));
						BigInteger id = new BigInteger("0010200000000000000").add(new BigInteger("" + ac));
						op.update("UPDATE `video` SET `danmaku_file_id`='" + cid + "' WHERE `id`='" + id + "'");
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("This video does not exist");
					e.printStackTrace();
					continue;
				}
				Thread.sleep(100);
			}
//			op.emptyCache();
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
		MetadataCrawler c = new MetadataCrawler();
		c.setting();
		int start = 9, end = 1000;
		try {
			c.getContent(start, end);
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
