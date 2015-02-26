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
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLIFrameElement;

import dao.VideoDAO;
import database.ISHCDataOperator;

public class VideoCrawler extends ContentAnalyzer {

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
		
		String base = basePath + "www.acfun.tv/";
		File root = new File(base + "www.acfun.tv");
		File output = new File(base + "log.txt");
		PrintWriter pw = null;
		
		ISHCDataOperator op = new ISHCDataOperator();
		VideoDAO.configure("http://www.acfun.tv/", "002", "0");
		
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
		int ccCount = 1;
		
		try {
			for (int ac = start; ac < end; ac++) {
				VideoDAO video = new VideoDAO();
				try {
					page = wc.getPage("http://www.acfun.tv/v/ac" + ac + "/");
					
					hif = (HtmlInlineFrame) page.getElementById("ACFlashPlayer-re");
					int vid;
					String ifadd = (String) hif.getAttribute("src");
					if (ifadd.isEmpty())
						throw new NullPointerException();
					p = Pattern.compile(".*vid=(\\d+).*");
					m = p.matcher(ifadd);
					if (m.matches())
						System.out.println("!!");
					vid = new Integer(m.group(1));
					
					p = null;
					m = null;
					
					h1 = (HtmlHeading1) page.getElementById("title-article");
					video.title = h1.asText();
					System.out.println(video.title);

					video.url = page.getUrl().toString();
					text = video.url;
					System.out.println(text);
					p = Pattern.compile(".*ac(\\d+).*");
					m = p.matcher(video.url);
					if (m.matches())
						System.out.println("!!");
					video.ID = new BigInteger("0020200000000000000").add(new BigInteger(m.group(1)));
					
					p = null;
					m = null;
					
					hp = (HtmlParagraph) page.getElementById("subtitle-article");
					text = hp.asText();
					text = text.replace(" ", "");
					video.path = text.substring(0, text.indexOf('/'));
					p = Pattern.compile(".*发布于(\\d*)年(\\d*)月(\\d*)日\\D*(\\d*)时(\\d*)分.*");
					m = p.matcher(text);
					if (m.matches())
						System.out.println("!!!");
					if (!m.group(1).isEmpty() && !m.group(2).isEmpty() && !m.group(3).isEmpty())
						video.date = new Date(Integer.parseInt(m.group(1)) - 1900, Integer.parseInt(m.group(2)) - 1, Integer.parseInt(m.group(3)));
					if (!m.group(4).isEmpty() && !m.group(5).isEmpty())
						video.time = new Time(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), 0);
					
					UnexpectedPage pgCount = wc.getPage("http://www.acfun.tv/content_view.aspx?contentId=" + ac);
					String sub = pgCount.getWebResponse().getContentAsString();
					p = Pattern.compile("\\[(\\d*),(\\d*),\\d*,\\d*,\\d*,(\\d*)\\]");
					m = p.matcher(sub);
					if (m.matches())
						System.out.println("!!!!");
					video.view = Integer.parseInt(m.group(1));
					video.reply = Integer.parseInt(m.group(2));
					video.favor = Integer.parseInt(m.group(3));
					
					p = null;
					m = null;
					
					UnexpectedPage pgVideo = wc.getPage("http://www.acfun.tv/video/getVideo.aspx?id=" + vid);
					String info = pgVideo.getWebResponse().getContentAsString();
					p = Pattern.compile(".*danmakuId\":\"([^\"]*)\".*");
					m = p.matcher(info);
					if (m.matches()) {
						System.out.println("!!!!!");
						String dmn = m.group(1);
						video.danmaku_file_name = dmn;
					}
					System.out.println(m.group(1));
					p = null;
					m = null;				
					
//					p = Pattern.compile("(\\d*)\\[(\\d*)\\]");
//					m = p.matcher(list.get(9).asText());
//					if (m.matches()) {
//						video.coin = tenThousand(m.group(1));
//						video.point = tenThousand(m.group(2));
//					}
					
					div = (HtmlDivision) page.getByXPath("//*[@id=\"area-tag-view\"]/div[1]/div").get(0);
					list = div.getChildNodes();
					text = "";
					for (DomNode dn : list) {
						if (dn.asText().length() != 0)
							text += dn.asText() + ";;";
					}
					if (!text.isEmpty()) {
						text = text.substring(0, text.length() - 2);
					}
					video.tag = text;
					
					HtmlHeading3 hd3 = (HtmlHeading3) page.getElementById("desc-article");
					video.intro = hd3.asText().trim();
					
					ha = (HtmlAnchor) page.getElementById("name-author");
					p = Pattern.compile("/member/user.aspx\\?uid=(\\d+)");
					System.out.println(ha.getHrefAttribute());
					m = p.matcher(ha.getHrefAttribute());
					if (m.matches())
						System.out.println("!! matches");
					if (m.matches())
						video.author = (new BigInteger(m.group(1))).add((new BigInteger("0020300000000000000")));

					video.danmaku_file_id = vid;
					
					op.insert(video);
					ccCount = 1;
					
				} catch (ClassCastException e) {
					Thread.sleep(1000 * ccCount);
					ac -= 1;
					ccCount += 1;
					if (ccCount <= 10) {
						continue;
					} else {
						System.out.println("This video does not exist//" + ac);
						e.printStackTrace();
						ccCount = 1;
						continue;
					}
				} catch (IndexOutOfBoundsException | NullPointerException | IllegalStateException e) {
					System.out.println("This video does not exist//" + ac);
					e.printStackTrace();
					ccCount = 1;
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
		int start = 439515, end = 442000;
		try {
			c.getContent(start, end);
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
