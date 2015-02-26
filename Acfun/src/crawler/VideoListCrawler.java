package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLIFrameElement;

import dao.VideoDAO;
import database.ISHCDataOperator;

public class VideoListCrawler extends ContentAnalyzer {

	@Override
	public void getContent() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		
		String base = basePath + "sports.acfun.tv/";
		File root = new File(base);
		File output = new File(base + "89.txt");
		PrintWriter pw = null;
		
		HtmlDivision div = null;
		HtmlHeading1 h1 = null;
		HtmlHeading2 h2 = null;
		HtmlAnchor ha = null;
		HtmlItalic hi = null;
		HtmlUnorderedList ul = null;
		String text = null;
		DomNodeList<DomNode> list = null;
		Pattern p = null;
		Matcher m = null;
		HtmlParagraph hp = null;
		HtmlInlineFrame hif = null;
		BufferedWriter br = null;
		
		try {
			if (!root.exists()) {
				root.mkdirs();
			}
			
//			page = wc.getPage("http://www.acfun.tv/v/list93/index.htm");
//			HtmlSpan hs = (HtmlSpan) page.getByXPath("//*[@id=\"block-content-channel\"]/div[3]/div[21]/span[10]").get(0);
//			String temp = hs.asText();
//			temp = temp.substring(temp.indexOf("/") + 1,temp.indexOf("页"));
//			int number = Integer.parseInt(temp);
//			System.out.println(number);
			int number = 144;
			
			br = new BufferedWriter(new FileWriter(output));
			
			for (int pgn = 1;pgn <= number; pgn++) {
				page = wc.getPage("http://www.acfun.tv/dynamic/channel/" + pgn + ".aspx?channelId=89&orderBy=0");
				System.out.print("Page  " + pgn + "    ");
				int count = 0;
				HtmlBody body = (HtmlBody) page.getByXPath("/html/body").get(0);
				list = body.getChildNodes();
				System.out.print(body.getChildElementCount() + "    ");
				for (DomNode d: list) {
					if (d.getNodeName().equals("div")){
						if (((HtmlDivision) d).getAttribute("class").equals("item unit")) {
							count++;
							br.write(((HtmlDivision) d).getAttribute("data-aid"));
							br.newLine();
							br.flush();
						}						
					}
				}
				System.out.println(count);
				Thread.sleep((int) (2000 * Math.random()) + 3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
//		HtmlImage hi = (HtmlImage) page.getByXPath("/html/body/div[5]/div[8]/div[1]/div[2]/img").get(0);
//		page = (HtmlPage) hi.click();
//		System.out.println(page.asText());
	}
	
	public static void main(String[] args) {
		VideoListCrawler c = new VideoListCrawler();
		c.setting();
		try {
			c.getContent();
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
