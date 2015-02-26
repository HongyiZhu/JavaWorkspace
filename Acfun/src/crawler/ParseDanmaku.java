package crawler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.*;
import database.*;

import org.json.*;
import org.json.zip.*;

public class ParseDanmaku {
	String root1 = "E:/emoticon_research/www.acfun.tv/";
	String root2 = "E:/emoticon_research/backup/www.acfun.tv/";
//	Set<String> utf = new HashSet<String>(); 
	ISHCDataOperator op = null;
	
	public ParseDanmaku(){
		op = new ISHCDataOperator();
		DanmakuDAO.configure("http://www.acfun.tv/", "002", "0");
		
//		if (!utf.isEmpty()) {
//			utf.clear();
//		}
		IO();
//		print();
	}
	
//	private void print(){
//		for (String s : utf) {
//			System.out.println(s);
//		}
//	}
//	
	private void ins(String vid, String jsonStr) {
		
		JSONArray ja = new JSONArray(jsonStr);
		System.out.println(vid + "\t" + ja.length());
		for (int i = 0; i < ja.length(); i++) {
			DanmakuDAO dmk = new DanmakuDAO();
			JSONObject jo = ja.getJSONObject(i);
			String attr = (String) jo.get("c");
			String[] att = attr.split(",");
			
			try {
				dmk.second = Float.parseFloat(att[0]);
			} catch (NumberFormatException e) {
				if (att[0].split("：").length == 2) {
					dmk.second = Integer.parseInt(att[0].split("：")[0]) * 60 + Integer.parseInt(att[0].split("：")[1]);
				} else if (att[0].split(":").length == 2) {
					dmk.second = Integer.parseInt(att[0].split(":")[0]) * 60 + Integer.parseInt(att[0].split(":")[1]);
				}
			}
			dmk.color = Integer.parseInt(att[1]);
			dmk.danmaku_file_id = Integer.parseInt(vid);
			dmk.content = (String) jo.get("m");
			dmk.type = Integer.parseInt(att[2]);
			dmk.fontsize = Integer.parseInt(att[3]);
			dmk.author_identifier = att[4];
			dmk.ID = (new BigInteger("100000").multiply(new BigInteger(vid))).add(new BigInteger("20400000000000000")).add(new BigInteger(String.valueOf(i)));
			
			try {
				op.insert(dmk);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		op.emptyCache();
	}
	
	private String parse(String vid, String jsonStr) {
		Pattern p = Pattern.compile("\\\\u([0-9a-f]{4})");
		Matcher m = p.matcher(jsonStr);
		while (m.find()) {
			String s = m.group(1);
			int i = Integer.parseInt(s, 16);
			char c = (char) i;
			jsonStr = jsonStr.substring(0, m.start()) + c + jsonStr.substring(m.end());
			m = p.matcher(jsonStr);
		}
		ins(vid, jsonStr);
		return jsonStr;
	}
	
	private void IO(){
		try {
			File folder = new File(root1);
			File dir = new File(root2);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			File[] files = folder.listFiles();
			
			for (File f : files) {
				BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
				String jsonString = null;
				if ((jsonString = br.readLine()) != null) {
					String vid = f.getName().split("_")[0];
					jsonString = parse(vid, jsonString);
//					System.out.println("Parse complete");
					br.close();
					
					//backup
					File copy = new File(root2 + f.getName());
					BufferedWriter bw = new BufferedWriter(new FileWriter(copy));
					bw.write(jsonString);
					bw.close();
					
					//remove f
					br.close();
					f.delete();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		ParseDanmaku pd = new ParseDanmaku();
	}
}
