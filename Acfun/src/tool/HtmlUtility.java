package tool;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUtility {
	
	public static void setProxy(String host, String port, String to_open){
		// Modify system properties
		Properties sysProperties = System.getProperties();

		// Specify proxy settings
		sysProperties.setProperty("http.proxySet", to_open);
		sysProperties.setProperty("http.proxyHost", host);
		sysProperties.setProperty("http.proxyPort", port);
	}
	
	public static void writeHtmlPage( HtmlPage page, String filepath ) throws IOException{
		String path = filepath.substring(0,filepath.lastIndexOf("\\"));
		File folder = new File(path);
		if(!folder.exists())
			folder.mkdirs();
		String body = page.getWebResponse().getContentAsString();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filepath)));
		bw.write(body);
		bw.close();
	}
	
	public static String getFullUrl(String base_url, String relative_url){
		String final_url = null;
		try{
			URL baseurl = new URL(base_url);
			final_url =  new URL(baseurl, relative_url).toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return final_url;
	}
	/*
	 * 解析 n秒前，n分钟前，n小时前，n天前，n月前，n年前。
	 */
	public static String[] parseTime(String how_long_ago){
		String[] time_split=null;
		String date=null;
		String time=null;
		Calendar c = Calendar.getInstance();
		int temp=0;
		if(how_long_ago.contains("秒"))
		{
			temp = Integer.parseInt( how_long_ago.substring(0, how_long_ago.indexOf("秒")) );
			c.set(Calendar.SECOND, c.get(Calendar.SECOND)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(" ");
			date = time_split[0];
			time = time_split[1];
		}else if( how_long_ago.contains("分钟") ){
			temp = Integer.parseInt( how_long_ago.substring(0, how_long_ago.indexOf("分钟")) );
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(":");
			String n = time_split[0]+":"+time_split[1]+":00";
			time_split = n.split(" ");
			date = time_split[0];
			time = time_split[1];
		}else if( how_long_ago.contains("小时") ){
			temp = Integer.parseInt( how_long_ago.substring(0, how_long_ago.indexOf("小时")) );
			c.set(Calendar.HOUR, c.get(Calendar.HOUR)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(":");
			String n = time_split[0]+":00:00";
			time_split = n.split(" ");
			date = time_split[0];
			time = time_split[1];
		}else if( how_long_ago.contains("天") ){
			temp = Integer.parseInt( how_long_ago.substring(0, how_long_ago.indexOf("天")) );
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(" ");
			date = time_split[0];
			time = null;
		}else if( how_long_ago.contains("月") ){
			temp = Integer.parseInt( how_long_ago.substring(0, how_long_ago.indexOf("个月")) );
			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split("-");
			date = time_split[0]+"-"+time_split[1]+"-01";
			time = null;
		}else if( how_long_ago.contains("年") ){
			temp = Integer.parseInt( how_long_ago.substring(0, how_long_ago.indexOf("年")) );
			c.set(Calendar.YEAR, c.get(Calendar.YEAR)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split("-");
			date = time_split[0]+"-01-01";
			time = null;
		}
		String[] date_time = new String[2];
		date_time[0] = date;
		date_time[1] = time;
		return date_time;
	}
	
	public static HtmlElement getElementByAttribute(HtmlElement element, String tag, String attribute, String value) throws GetElementException{
		List<HtmlElement> l = element.getElementsByAttribute(tag, attribute, value);
		if(l.isEmpty())
			throw new GetElementException(null, "cannot get element <"+tag+" "+attribute+"="+value+">");
		return l.get(0);
	}
	
	public static String urlToFilePath( String url, String base_path ){
		String path = base_path+"/"+url.replace("http://", "").replaceAll("[^a-zA-Z0-9_]", "/").replaceAll("[/]+$", "")+".html";
		return path.replaceAll("[\\\\/]+", "/");
	}
	
	public static String urlToFileUrl(String url, String base_path){
		return "file:///"+urlToFilePath(url, base_path);
	}
	
	public static void savePage(HtmlPage page, String url, String base_path) {
		String filename = urlToFilePath(url, base_path);
		try{
			page.save(new File(filename));
			BufferedReader br = new BufferedReader(new FileReader(filename));
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename + "_1"));
			bw.write("<!--");
			bw.newLine();
			bw.write(url);
			bw.newLine();
			bw.write("-->");
			bw.newLine();
			String line = br.readLine();
			while (line != null) {
				bw.write(line.replaceAll("encoding=\"[a-zA-Z\\-0-9]*\"",
						"encoding=\"utf-8\"").replaceAll("charset=[a-zA-Z\\-0-9]+",
						"charset=utf-8"));
				bw.newLine();
				line = br.readLine();
			}
			br.close();
			bw.close();
			File f = new File(filename);
			f.delete();
			File f2 = new File(filename+"_1");
			f2.renameTo(f);
		}catch(IOException e){
			System.out.println("Failed to saving ["+url+"] to file ["+filename+"]");
			e.printStackTrace();
		}
	}
	
	public static void downloadHtml(String url, String base_path) {
		String filename = urlToFilePath(url, base_path);
		try {
			WebClient web_client;
			web_client = new WebClient(BrowserVersion.FIREFOX_17);
			web_client
					.setAjaxController(new NicelyResynchronizingAjaxController());
			web_client.setCssEnabled(false);
			web_client.setJavaScriptEnabled(true);
			web_client.setTimeout(35000);
			web_client.setThrowExceptionOnScriptError(false);
			HtmlPage page = web_client.getPage(url);

			// String a = "D:\\39data\\q_" + System.currentTimeMillis();
			Calendar c = Calendar.getInstance();
			System.out.println(filename);
			page.save(new File(filename));
			BufferedReader br = new BufferedReader(new FileReader(filename));
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename
					+ "_1"));
			bw.write("<!--");
			bw.newLine();
			bw.write(url);
			bw.newLine();
			bw.write("-->");
			bw.newLine();
			String line = br.readLine();
			while (line != null) {
				bw.write(line.replaceAll("encoding=\"[a-zA-Z\\-0-9]*\"",
						"encoding=\"utf-8\"").replaceAll(
						"charset=[a-zA-Z\\-0-9]+", "charset=utf-8"));
				bw.newLine();
				line = br.readLine();
			}
			br.close();
			bw.close();
			File f = new File(filename);
			f.delete();
			File f2 = new File(filename + "_1");
			f2.renameTo(f);
		}
		catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(IOException e){
			System.out.println("Failed to saving ["+url+"] to file ["+filename+"]");
			e.printStackTrace();
		}
		
		
	}
	
	public static String cleanString(String original_text){
		original_text = original_text.replaceAll("([\r\n]+[ ]*)+", "\r\n");
		original_text = original_text.replaceAll("[ ]+", " ").replaceAll("[\r\n ]+$", "").replaceAll("^[\r\n ]+", "");
		return original_text;
	}
	
	public static long sleep(long max_seconds){
		double a = Math.random();
		long t = (long)(a*(double)max_seconds*1000);
		System.out.println("sleep for "+(t/1000.0) + "s");
		try {
			Thread.sleep( t );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static void test(){
		WebClient web_client;
		web_client = new WebClient(BrowserVersion.FIREFOX_17);
		web_client.setAjaxController(new NicelyResynchronizingAjaxController());
		web_client.setCssEnabled(false);
		web_client.setJavaScriptEnabled(true);
		web_client.setTimeout(35000);
		web_client.setThrowExceptionOnScriptError(false);
		for(int i=0; i<1000; i++){
			try {
				web_client.getPage("http://ask.39.net/question/27489315.html");
			}catch (java.net.SocketTimeoutException e) {
				e.printStackTrace();
				break;
			} catch (FailingHttpStatusCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} 
		}
	}
	
	public static void main(String[] args) {
		System.out.println( sleep(100) );
		
	}
}
