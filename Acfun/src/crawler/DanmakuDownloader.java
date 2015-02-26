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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLIFrameElement;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

import dao.VideoDAO;
import database.ISHCDataOperator;

public class DanmakuDownloader extends ContentAnalyzer {
	
	@Override
	public void getContent() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		// TODO Auto-generated method stub
		getContent(7, 20);
	}
	
	public void getContent(int start, int end) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		
		String base = basePath + "www.acfun.tv/";
		File root = new File(base);
		PrintWriter pw = null;
		
		ISHCDataOperator op = new ISHCDataOperator();
		VideoDAO.configure("http://www.acfun.tv/", "002", "2");
		wc.setJavaScriptEnabled(false);
		
		HtmlDivision div = null;
		HtmlHeading2 h2 = null;
		HtmlAnchor ha = null;
		HtmlItalic hi = null;
		HtmlUnorderedList ul = null;
		String text = null;
		DomNodeList<DomNode> list = null;
		Pattern p = null;
		Matcher m = null;
		UnexpectedPage up = null;
		
		try {
			if (!root.exists()) {
				root.mkdirs();
			}
			for (int a = start; a <= end - 1000; a += 1000) {
				ResultSet rs = op.query("SELECT `id`,`danmaku_file_id`, `danmaku_file_name` FROM `video` WHERE `id` > 20200000000000000 AND `id` < 30000000000000000 LIMIT " + a + "," + (a + 999));
				
				XmlPage xml = null;
				File output = null;
				BufferedWriter fw = null;
				while (rs.next()) {
					try {
						BigInteger al = new BigInteger(rs.getString("id")).subtract(new BigInteger("0020200000000000000"));
						String a2 = rs.getString("danmaku_file_name");
						System.out.print(a2);
						output = new File(base + al + "_" + a2 + ".json");
						if (output.exists() && output.getTotalSpace() != 0){
							System.out.println(" Exist");
							continue;
						}
						
						up = wc.getPage("http://comment.acfun.tv/" + a2 + ".json");
						String str = up.getWebResponse().getContentAsString();
						byte[] temp = str.getBytes("ISO-8859-1");//这里写原编码方式
			            String newStr = new String(temp,"utf-8");
						
//						System.out.println(start);
//						System.out.println(al);
//						start++;
						
						fw = new BufferedWriter(new FileWriter(output));
						fw.write(newStr);
						fw.flush();
						System.out.println(" Done");
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassCastException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (fw != null)
					fw.close();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		DanmakuDownloader c = new DanmakuDownloader();
		c.setting();
		int start = 1, end = 170000;
		try {
			c.getContent(start, end);
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
