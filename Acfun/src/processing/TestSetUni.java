package processing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import database.ISHCDataOperator;

public class TestSetUni {
	ISHCDataOperator op = null;
	Map<String, Unigram> mu = null;
	Map<String, Danmaku2> md = null;
	List<String> ld = null;
	List<String> lt = null;
	List<String> le = null;

	
	TestSetUni() {
		op = new ISHCDataOperator();
		mu = new HashMap<String, Unigram>();
		lt = new ArrayList<String>();
		le = new ArrayList<String>();
		getUni();
		getTriplet();
		getEmoticon();
		for (int i = 0; i < 10; i++){
			md = new HashMap<String, Danmaku2>();
			ld = new ArrayList<String>();
			getTestSet(i);
			execute();
			System.out.println();
		}
//		md = new HashMap<String, Danmaku2>();
//		ld = new ArrayList<String>();
//		getTestSet();
//		execute();
	}
	
	private void execute() {
		int totalcount = 0;
		int hitcount = 0;
		
		for (String id: ld){
			md.get(id).calClass(mu, lt, le);
			if (md.get(id).found){
				totalcount += 1;
			}
			if (md.get(id).hit){
				hitcount += 1;
			}
		}
		
		System.out.println(totalcount);
		System.out.println(hitcount);
		System.out.println((double)hitcount / (double)totalcount);
	}
	
	private void getTriplet() {
		ResultSet rs = op.query("SELECT `content` FROM `new_emoticon_triplet` LIMIT 0, 20000");
		try {
			while (rs.next()){
				lt.add(rs.getString("content"));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		lt = sortList(lt);
	}
	
	private void getEmoticon() {
		ResultSet rs = op.query("SELECT `content` FROM `new_emoticon` LIMIT 0, 20000");
		try {
			while (rs.next()){
				le.add(rs.getString("content"));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		le = sortList(le);
	}
	
	private void getTestSet() {
//		ResultSet rs = op.query("SELECT * FROM `taobao_test_set` LIMIT 0, 1000");
//		try {
//			while (rs.next()){
//				ld.add(rs.getString("id"));
//				md.put(rs.getString("id"), new Danmaku2(rs.getString("id"), rs.getString("content"), 
//						rs.getInt("type"), new int[] {rs.getInt("happiness"), rs.getInt("sadness"), 
//							rs.getInt("fear"), rs.getInt("disgust"),
//							rs.getInt("anger"), rs.getInt("surprise"),
//							rs.getInt("love")}, rs.getInt("occurrence")));
//			}
//		} catch (SQLException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
		ld.add("1");
		md.put("1", new Danmaku2("1", "o(｀Д´*)o", 1, new int[] {0, 0, 0, 0, 1, 0, 0}, 1));
	}
	
	private void getTestSet(int i) {
		ResultSet rs = op.query("SELECT * FROM `taobao_test_set` LIMIT " + i * 60 + ", 60");
		try {
			while (rs.next()){
				ld.add(rs.getString("id"));
				md.put(rs.getString("id"), new Danmaku2(rs.getString("id"), rs.getString("content"), 
						rs.getInt("type"), new int[] {rs.getInt("happiness"), rs.getInt("sadness"), 
							rs.getInt("fear"), rs.getInt("disgust"),
							rs.getInt("anger"), rs.getInt("surprise"),
							rs.getInt("love")}, rs.getInt("occurrence")));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	private void getUni() {
		ResultSet rs = op.query("SELECT * FROM `new_emoticon_unigram` LIMIT 0, 20000");
		try {
			while (rs.next()){
				mu.put(rs.getString("content"), new Unigram(rs.getString("content"), rs.getInt("occurrence"), 
						new int[] {rs.getInt("happiness"), rs.getInt("sadness"), 
							rs.getInt("fear"), rs.getInt("disgust"),
							rs.getInt("anger"), rs.getInt("surprise"),
							rs.getInt("love")}));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> sortList(List<String> list) {  
	    /* 返回的list */  
	    ArrayList<String> retList = new ArrayList<String>();  
	    /* 当前list的元素个数 */  
	    int size = list.size();  
	    /* list的最大长度(while循环使用) */  
	    int listMaxSize = size;  
	    /* 当返回的list的 长度和参数list的长度不一致的时候，循环 */  
	    while (retList.size() < listMaxSize) {  
	        /* 长度最大的字符串的长度 */  
	        int maxLen = 0;  
	        /* 长度最大的index */  
	        int maxIndex = 0;  
	        /* 循环找出长度最大的字符串 */  
	        for (int i = 0; i < size; i++) {  
	            /* 当前字符串 */  
	            String str = list.get(i);  
	            /* 当前字符串的长度（null的时候是0） */  
	            int len = 0;  
	            if (str != null) {  
	                len = str.length();  
	            }  
	            /* 如果当前字符串的长度比设定的maxLen大，则把当前字符串设定为最大*/  
	            if (len > maxLen) {  
	                maxLen = len;  
	                maxIndex = i;  
	            }  
	        }  
	        /* 结束内层循环，把最大的字符串追加到retList中 */  
	        retList.add(list.get(maxIndex));  
	        /* 把list中最大的那个元素remove掉 */  
	        list.remove(maxIndex);  
	        /* 元素-1 */  
	        size--;  
	    }  
	    return retList;  
	}
		
	public static void main(String[] args) {
		TestSetUni tsu = new TestSetUni();
	}
}
