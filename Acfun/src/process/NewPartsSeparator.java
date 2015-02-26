package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import dao.EmoticonPartDAO;
//import dao.NewEmoticonPartDAO;
import database.ISHCDataOperator;

public class NewPartsSeparator {
	public static String dictroot = null;
	
	public static Map<String, Emoticon> emoticon = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> emoticonRaw = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> triplet = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> unigram = new HashMap<String, Emoticon> ();
	
	public static Map<String, Emoticon> elm = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> mer = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> eler = new HashMap<String, Emoticon> ();
	
	public static Map<String, Emoticon> el = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> m = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> er = new HashMap<String, Emoticon> ();
	
	public static Map<String, Emoticon> s1 = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> s2 = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> s3 = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> s4 = new HashMap<String, Emoticon> ();
	
	public static List<String> emoticonList = new ArrayList<String> ();
	public static List<String> tripletList = new ArrayList<String> ();
	
	public static List<String> elmList = new ArrayList<String> ();
	public static List<String> merList = new ArrayList<String> ();
	public static List<String> elerList = new ArrayList<String> ();
	
	public static List<String> elList = new ArrayList<String> ();
	public static List<String> mList = new ArrayList<String> ();
	public static List<String> erList = new ArrayList<String> ();
	
	public static List<String> s1List = new ArrayList<String> ();
	public static List<String> s2List = new ArrayList<String> ();
	public static List<String> s3List = new ArrayList<String> ();
	public static List<String> s4List = new ArrayList<String> ();
	
	public static Map<String, Emoticon> notFound = new HashMap<String, Emoticon> ();
	
	NewPartsSeparator(){
		
	}
	
	NewPartsSeparator(String filename) {
		IO(filename);
	}
	
	public static String choke(String emoticon) {
		/**
		 * 去除首尾的连续空格，包括全角及半角空格
		 */
		int i = 0;
		for (i = 0; i < emoticon.length(); i++) {
			if (emoticon.charAt(i) != '　' && emoticon.charAt(i) != ' ') {
				break;
			}
		}
		
		String half = emoticon.substring(i);
		
		for (i = half.length() - 1; i >= 0; i--) {
			if (half.charAt(i) != '　' && half.charAt(i) != ' ') {
				break;
			}
		}

		return half.substring(0, i + 1);
	}

	
	/**
	 * 提取三元组中每个部分 
	 * @param instr 需要分析的三元组
	 */
	public static String[] extract(String instr) {	
		
		String El = null;
		String Er = null;
		String M = null;
				
		instr = choke(instr);
//		System.out.print(instr + " : ");
		
		//对称遍历
		char[] c = instr.toCharArray();
		int i = 0;
		int j = c.length - 1;
		
		for (i = 0, j = c.length - 1; i < j; i++, j--){
			if (c[i] != c[j] || c[i] == ' ' || c[j] == ' ' || c[i] == '　' || c[j] == '　') {
				break;
			}
		}
		
		if (i > j){
			i--;
			j++;
			if (i > 0){
				while (c[i] == c[i - 1]){
					i--;
				}
				i--;
				while (c[j] == c[j + 1]){
					j++;
				}
				j++;
			}
		} else if (i == j){
			while (c[i] == c[i - 1]){
				i--;
			}
			i--;
			while (c[j] == c[j + 1]){
				j++;
			}
			j++;
		} else {
			if (i > 0){
				i--;
				j++;
				while (c[i] == c[i + 1]){
					i++;
				}
				while (c[j] == c[j - 1]){
					j--;
				}
			}
		}
		
		El = instr.substring(0, i + 1);
		Er = instr.substring(j);
		M = instr.substring(i + 1, j);
		
		//顺序遍历
		for (i = c.length / 2; i > 0; i--) {
			String sub = instr.substring(0, i);
			int ind = 0;
			if ((ind = instr.lastIndexOf(sub)) != 0){
				if (sub.length() > El.length()) {
					El = sub;
					Er = sub;
					M = instr.substring(i, ind);
					break;
				}
			}
			
		}
		
		String[] res = {choke(El), choke(M), choke(Er)};
		
		return res;
	}
	
	private void IO(String filename) {
		File dict = new File(dictroot + filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(dict));
			
			String instr = null;
			while ((instr = br.readLine()) != null) {
				if (instr.contains("*****")) {
					System.out.println(instr);
					instr = br.readLine();
					System.out.println(instr);
					continue;
				}
				String[] result = extract(instr.trim());
				for (String str: result){
					System.out.print("{" + str + "}\t");
				}
				System.out.println("");
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		
	}

	public static void extractParts() {
		for (String s : triplet.keySet()) {
//			System.out.println(s);
			String[] ss = extract(s);
			String leftEye = ss[0];
			String mouth = ss[1];
			String rightEye = ss[2];
			String leftEyeMouth = leftEye + mouth;
			String mouthRightEye = mouth + rightEye;
			String leftRightEye = leftEye + rightEye;
			
			Emoticon tri = triplet.get(s);
			
			// insert into map el
			Emoticon leftEyeIcon = new Emoticon(leftEye);
			leftEyeIcon.combine(tri);
			if (el.containsKey(leftEye)) {
				el.put(leftEye, el.get(leftEye).combine(leftEyeIcon));
			} else {
				el.put(leftEye, leftEyeIcon);
			}
			
			// insert into map m
			Emoticon mouthIcon = new Emoticon(mouth);
			mouthIcon.combine(tri);
			if (m.containsKey(mouth)) {
				m.put(mouth, m.get(mouth).combine(mouthIcon));
			} else {
				m.put(mouth, mouthIcon);
			}
			
			// insert into map er
			Emoticon rightEyeIcon = new Emoticon(rightEye);
			rightEyeIcon.combine(tri);
			if (er.containsKey(rightEye)) {
				er.put(rightEye, er.get(rightEye).combine(rightEyeIcon));
			} else {
				er.put(rightEye, rightEyeIcon);
			}
			
			// insert into map elm
			if (leftEyeMouth.length() > 1) {
				Emoticon leftEyeMouthIcon = new Emoticon(leftEyeMouth);
				leftEyeMouthIcon.combine(tri);
				if (elm.containsKey(leftEyeMouth)) {
					elm.put(leftEyeMouth, elm.get(leftEyeMouth).combine(leftEyeMouthIcon));
				} else {
					elm.put(leftEyeMouth, leftEyeMouthIcon);
				}
			}
			
			// insert into map mer
			if (mouthRightEye.length() > 1) {
				Emoticon mouthRightEyeIcon = new Emoticon(mouthRightEye);
				mouthRightEyeIcon.combine(tri);
				if (mer.containsKey(mouthRightEye)) {
					mer.put(mouthRightEye, mer.get(mouthRightEye).combine(mouthRightEyeIcon));
				} else {
					mer.put(mouthRightEye, mouthRightEyeIcon);
				}
			}
			
			// insert into map eler
			if (leftRightEye.length() > 1) {
				Emoticon leftRightEyeIcon = new Emoticon(leftRightEye);
				leftRightEyeIcon.combine(tri);
				if (eler.containsKey(leftRightEye)) {
					eler.put(leftRightEye, eler.get(leftRightEye).combine(leftRightEyeIcon));
				} else {
					eler.put(leftRightEye, leftRightEyeIcon);
				}
			}
		}
		
		for (String s : el.keySet()) {
			elList.add(s);
		}
		elList = sortList(elList);
		
		for (String s : m.keySet()) {
			mList.add(s);
		}
		mList = sortList(mList);
		
		for (String s : er.keySet()) {
			erList.add(s);
		}
		erList = sortList(erList);
		
		for (String s : elm.keySet()) {
			elmList.add(s);
		}
		elmList = sortList(elmList);
		
		for (String s : mer.keySet()) {
			merList.add(s);
		}
		merList = sortList(merList);
		
		for (String s : eler.keySet()) {
			elerList.add(s);
		}
		elerList = sortList(elerList);
	}
	
//	public static void insertIntoDatabase() {
//		ISHCDataOperator op = new ISHCDataOperator();
//		NewEmoticonPartDAO.configure(null, "000", "0");
//		// map el
//		for (String s : el.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = el.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 1;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// map m
//		for (String s : m.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = m.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 2;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// map er
//		for (String s : er.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = er.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 3;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// map s1
//		for (String s : s1.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = s1.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 4;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// map s2
//		for (String s : s2.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = s2.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 5;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// map s3
//		for (String s : s3.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = s3.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 6;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// map s4
//		for (String s : s4.keySet()) {
//			NewEmoticonPartDAO e = new NewEmoticonPartDAO();
//			Emoticon icon = s4.get(s);
//			e.getID();
//			e.content = s;
//			e.position = 7;
//			e.happiness = icon.happiness;
//			e.sadness = icon.sadness;
//			e.fear = icon.fear;
//			e.disgust = icon.disgust;
//			e.anger = icon.anger;
//			e.surprise = icon.surprise;
//			e.love = icon.love;
//			e.occurrence = icon.happiness + icon.sadness + icon.fear
//					+ icon.disgust + icon.anger + icon.surprise + icon.love;
//			try {
//				op.insert(e);
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		
//		op.emptyCache();
//		op.disconnect();
//	}
//	
	public static boolean isPar(char c) {
		if (c == '(' || c == ')' || c == '（' || c == '）'
				|| c == '{' || c == '}' || c == '[' || c == ']') {
			return true;
		} else {
			return false;
		}
	}
	
	public static int hasPar(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (isPar(s.charAt(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public static void initUnigram() {
		ISHCDataOperator op = new ISHCDataOperator();
		ResultSet rs = op.query("SELECT * FROM new_emoticon_unigram LIMIT 0, 20000");
		
		try {
			while (rs.next()) {
				Emoticon e = new Emoticon();
				e.content = rs.getString(2);
				e.happiness = rs.getInt(4);
				e.sadness = rs.getInt(5);
				e.fear = rs.getInt(6);
				e.disgust = rs.getInt(7);
				e.anger = rs.getInt(8);
				e.surprise = rs.getInt(9);
				e.love = rs.getInt(10);
				e.occurrence = rs.getInt(11);
				unigram.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void initEmoticon() {
		ISHCDataOperator op = new ISHCDataOperator();
		ResultSet rs = op.query("SELECT * FROM new_emoticon LIMIT 0, 20000");
		
		try {
			while (rs.next()) {
				Emoticon e = new Emoticon();
				e.content = rs.getString(2);
				e.happiness = rs.getInt(4);
				e.sadness = rs.getInt(5);
				e.fear = rs.getInt(6);
				e.disgust = rs.getInt(7);
				e.anger = rs.getInt(8);
				e.surprise = rs.getInt(9);
				e.love = rs.getInt(10);
				e.occurrence = rs.getInt(11);
				emoticon.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String s : emoticon.keySet()) {
			emoticonList.add(s);
		}
		emoticonList = sortList(emoticonList);
	}

	public static void initEmoticonRaw() {
		ISHCDataOperator op = new ISHCDataOperator();
		ResultSet rs = op.query("SELECT * FROM new_emoticon_raw LIMIT 0, 20000");
		
		try {
			while (rs.next()) {
				Emoticon e = new Emoticon();
				e.content = rs.getString(2);
				e.happiness = rs.getInt(4);
				e.sadness = rs.getInt(5);
				e.fear = rs.getInt(6);
				e.disgust = rs.getInt(7);
				e.anger = rs.getInt(8);
				e.surprise = rs.getInt(9);
				e.love = rs.getInt(10);
				e.occurrence = rs.getInt(11);
				emoticonRaw.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void initTriplet() {
		ISHCDataOperator op = new ISHCDataOperator();
		ResultSet rs = op.query("SELECT * FROM new_emoticon_triplet LIMIT 0, 20000");
		
		try {
			while (rs.next()) {
				Emoticon e = new Emoticon();
				e.content = rs.getString(2);
				e.happiness = rs.getInt(4);
				e.sadness = rs.getInt(5);
				e.fear = rs.getInt(6);
				e.disgust = rs.getInt(7);
				e.anger = rs.getInt(8);
				e.surprise = rs.getInt(9);
				e.love = rs.getInt(10);
				e.occurrence = rs.getInt(11);
				triplet.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String s : triplet.keySet()) {
			tripletList.add(s);
		}
		tripletList = sortList(tripletList);
	}

	public static void initSakura() {
		ISHCDataOperator op = new ISHCDataOperator();
		ResultSet rs1 = op.query("SELECT * FROM new_emoticon_part WHERE position = 4 LIMIT 0, 20000");
		
		try {
			while (rs1.next()) {
				Emoticon e = new Emoticon();
				e.content = rs1.getString(2);
				e.happiness = rs1.getInt(4);
				e.sadness = rs1.getInt(5);
				e.fear = rs1.getInt(6);
				e.disgust = rs1.getInt(7);
				e.anger = rs1.getInt(8);
				e.surprise = rs1.getInt(9);
				e.love = rs1.getInt(10);
				e.occurrence = rs1.getInt(11);
				s1.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String s : s1.keySet()) {
			s1List.add(s);
		}
		s1List = sortList(s1List);
		
		ResultSet rs2 = op.query("SELECT * FROM new_emoticon_part WHERE position = 5 LIMIT 0, 20000");
		try {
			while (rs2.next()) {
				Emoticon e = new Emoticon();
				e.content = rs2.getString(2);
				e.happiness = rs2.getInt(4);
				e.sadness = rs2.getInt(5);
				e.fear = rs2.getInt(6);
				e.disgust = rs2.getInt(7);
				e.anger = rs2.getInt(8);
				e.surprise = rs2.getInt(9);
				e.love = rs2.getInt(10);
				e.occurrence = rs2.getInt(11);
				s2.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String s : s2.keySet()) {
			s2List.add(s);
		}
		s2List = sortList(s2List);
		
		ResultSet rs3 = op.query("SELECT * FROM new_emoticon_part WHERE position = 6 LIMIT 0, 20000");
		try {
			while (rs3.next()) {
				Emoticon e = new Emoticon();
				e.content = rs3.getString(2);
				e.happiness = rs3.getInt(4);
				e.sadness = rs3.getInt(5);
				e.fear = rs3.getInt(6);
				e.disgust = rs3.getInt(7);
				e.anger = rs3.getInt(8);
				e.surprise = rs3.getInt(9);
				e.love = rs3.getInt(10);
				e.occurrence = rs3.getInt(11);
				s3.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String s : s3.keySet()) {
			s3List.add(s);
		}
		s3List = sortList(s3List);

		ResultSet rs4 = op.query("SELECT * FROM new_emoticon_part WHERE position = 7 LIMIT 0, 20000");
		try {
			while (rs4.next()) {
				Emoticon e = new Emoticon();
				e.content = rs4.getString(2);
				e.happiness = rs4.getInt(4);
				e.sadness = rs4.getInt(5);
				e.fear = rs4.getInt(6);
				e.disgust = rs4.getInt(7);
				e.anger = rs4.getInt(8);
				e.surprise = rs4.getInt(9);
				e.love = rs4.getInt(10);
				e.occurrence = rs4.getInt(11);
				s4.put(e.content, e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String s : s4.keySet()) {
			s4List.add(s);
		}
		s4List = sortList(s4List);
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
	
	public static String regex(String s) {
		
		return s.replace("\\", "\\\\\\\\")
				.replace("$", "\\\\$")
				.replaceAll("\\(", "\\\\(")
				.replaceAll("\\)", "\\\\)")
				.replace("[", "\\\\[")
				.replace("]", "\\\\]")
				.replace("{", "\\\\{")
				.replace("}", "\\\\}")
				.replace(".", "\\\\.")
				.replace("*", "\\\\*")
				.replace("?", "\\\\?")
				.replace("+", "\\\\+")
				.replaceAll("\\^", "\\\\^")
				.replaceAll("\\|", "\\\\|");
	}
	
	public static void depart(String str) {
		String eme = null;
		Emoticon origin = emoticon.get(str);
		String leftover = str;
		do {
			int type = 0;
			Pattern p1 = Pattern.compile(".*?[(（]([^)）]*)[)）]");
			Matcher n1 = p1.matcher(leftover);
			Pattern p2 = Pattern.compile(".*?[(（](.*)");
			Matcher n2 = p2.matcher(leftover);
			Pattern p3 = Pattern.compile("(.*?)[)）]");
			Matcher n3 = p3.matcher(leftover);
			String temp = null;
			boolean flag = true;
OUTER:
			while (flag) {
				flag = false;
				if (n1.find()) {
					temp = n1.group(1);
					type = 1;
					flag = true;
				} else if (n2.find()) {
					temp = n2.group(1);
					type = 2;
					flag = true;
				} else if (n3.find()) {
					temp = n3.group(1);
					type = 3;
					flag = true;
				} else {
					temp = leftover;
					type = 4;
				}
				for (String s : tripletList) {
					if (temp.contains(s)) {
						eme = s;
						System.out.println("Found triplet {" + eme + "}");
						break OUTER;
					}
				}

				if (eme == null) {
					for (String s : elerList) {
						if (temp.contains(s)) {
							eme = s;
							System.out.println("Found eler {" + eme + "}");
							break OUTER;
						}
					}
				}
				if (eme == null) {
					for (String s : elmList) {
						if (temp.contains(s)) {
							eme = s;
							System.out.println("Found elm {" + eme + "}");
							break OUTER;
						}
					}
				}
				if (eme == null) {
					for (String s : merList) {
						if (temp.contains(s)) {
							eme = s;
							System.out.println("Found mer {" + eme + "}");
							break OUTER;
						}
					}
				}
				
			}

			if (eme == null) {
				System.out.println("Triplet in {" + leftover + "} not found");
				notFound.put(leftover, origin);
				return;
			}
			
			System.out.println("Leftover: " + leftover);
			if (type == 1) {
				Pattern p = Pattern.compile("(.*?)[(（](.*?)" + regex(eme) + "(.*?)[)）]([^(（]*)(.*)");
				Matcher m = p.matcher(leftover);
			
				if (m.matches()) {
					String str1 = choke(m.group(1));
					String str2 = choke(m.group(2));
					String str3 = choke(m.group(3));
					String str4 = choke(m.group(4));
					System.out.println("{" + str1 	// s1
							+ "}\t{" + str2 	// s2
							+ "}\t{" + str3 	// s3
							+ "}\t{" + str4	// s4
							+ "}\t{" + m.group(5)
							+ "}");
					leftover = m.group(5);
					
					if (str1.length() > 0) {
						if (!s1.containsKey(str1)) {
							Emoticon icon1 = new Emoticon(str1);
							s1.put(str1, icon1.combine(origin));
						} else {
							s1.put(str1, s1.get(str1).combine(origin));
						}
					}
					if (str2.length() > 0) {
						if (!s2.containsKey(str2)) {
							Emoticon icon2 = new Emoticon(str2);
							s2.put(str2, icon2.combine(origin));
						} else {
							s2.put(str2, s2.get(str2).combine(origin));
						}
					}
					if (str3.length() > 0) {
						if (!s3.containsKey(str3)) {
							Emoticon icon3 = new Emoticon(str3);
							s3.put(str3, icon3.combine(origin));
						} else {
							s3.put(str3, s3.get(str3).combine(origin));
						}
					}
					if (str4.length() > 0) {
						if (!s4.containsKey(str4)) {
							Emoticon icon4 = new Emoticon(str4);
							s4.put(str4, icon4.combine(origin));
						} else {
							s4.put(str4, s4.get(str4).combine(origin));
						}
					}
				} else {
					System.out.println("No match 1");
					leftover = "";
				}
				m = p.matcher(leftover);
				
			} else if (type == 2) {
				Pattern p = Pattern.compile("(.*?)[(（](.*?)" + regex(eme) + "([^(（]*)(.*)");
				Matcher m = p.matcher(leftover);
				
				if (m.matches()) {
					String str1 = choke(m.group(1));
					String str2 = choke(m.group(2));
					String str3 = choke(m.group(3));
					System.out.println("{" + str1 	// s1
							+ "}\t{" + str2 	// s2
							+ "}\t{" + str3 	// s3
							+ "}\t{" + m.group(4)
							+ "}");
					leftover = m.group(4);
					
					if (str1.length() > 0) {
						if (!s1.containsKey(str1)) {
							Emoticon icon1 = new Emoticon(str1);
							s1.put(str1, icon1.combine(origin));
						} else {
							s1.put(str1, s1.get(str1).combine(origin));
						}
					}
					if (str2.length() > 0) {
						if (!s2.containsKey(str2)) {
							Emoticon icon2 = new Emoticon(str2);
							s2.put(str2, icon2.combine(origin));
						} else {
							s2.put(str2, s2.get(str2).combine(origin));
						}
					}
					if (str3.length() > 0) {
						if (!s3.containsKey(str3)) {
							Emoticon icon3 = new Emoticon(str3);
							s3.put(str3, icon3.combine(origin));
						} else {
							s3.put(str3, s3.get(str3).combine(origin));
						}
					}
				} else {
					System.out.println("No match 2");
					leftover = "";
				}
				m = p.matcher(leftover);
			} else if (type == 3) {
				Pattern p = Pattern.compile("(.*?)" + regex(eme) + "(.*?)[)）]([^(（]*)(.*)");
				Matcher m = p.matcher(leftover);
				
				if (m.matches()) {
					String str2 = choke(m.group(1));
					String str3 = choke(m.group(2));
					String str4 = choke(m.group(3));
					System.out.println("{" + str2	// s2
							+ "}\t{" + str3 	// s3
							+ "}\t{" + str4 	// s4
							+ "}\t{" + m.group(4)
							+ "}");
					leftover = m.group(4);
					
					if (str2.length() > 0) {
						if (!s2.containsKey(str2)) {
							Emoticon icon2 = new Emoticon(str2);
							s2.put(str2, icon2.combine(origin));
						} else {
							s2.put(str2, s2.get(str2).combine(origin));
						}
					}
					if (str3.length() > 0) {
						if (!s3.containsKey(str3)) {
							Emoticon icon3 = new Emoticon(str3);
							s3.put(str3, icon3.combine(origin));
						} else {
							s3.put(str3, s3.get(str3).combine(origin));
						}
					}
					if (str4.length() > 0) {
						if (!s4.containsKey(str4)) {
							Emoticon icon4 = new Emoticon(str4);
							s4.put(str4, icon4.combine(origin));
						} else {
							s4.put(str4, s4.get(str4).combine(origin));
						}
					}
				} else {
					System.out.println("No match 3");
					leftover = "";
				}
				m = p.matcher(leftover);
			} else {
				Pattern p = Pattern.compile("(.*?)" + regex(eme) + "(.*)");
				Matcher m = p.matcher(leftover);
				if (m.matches()) {
					String str2 = choke(m.group(1));
					String str3 = choke(m.group(2));
					System.out.println("{" + str2 	// s2
							+ "}\t{" + str3	// s3
							+ "}");
					leftover = "";
					
					if (str2.length() > 0) {
						if (!s2.containsKey(str2)) {
							Emoticon icon2 = new Emoticon(str2);
							s2.put(str2, icon2.combine(origin));
						} else {
							s2.put(str2, s2.get(str2).combine(origin));
						}
					}
					if (str3.length() > 0) {
						if (!s3.containsKey(str3)) {
							Emoticon icon3 = new Emoticon(str3);
							s3.put(str3, icon3.combine(origin));
						} else {
							s3.put(str3, s3.get(str3).combine(origin));
						}
					}
				} else {
					System.out.println("No match 4");
					leftover = "";
				}
				m = p.matcher(leftover);
			}
		} while (leftover.length() > 0);
	}
	
	public static void process() {
		for (String s : emoticon.keySet()) {
			depart(s);
		}
	}
	
	public static void main(String[] args) {
//		PartsSeparator ps = new PartsSeparator("test.txt");
//		ps.dictroot = "C:/";
		
//		PartsSeparator ps2 = new PartsSeparator();
//		for (String str :ps2.extract("=_=")){
//			System.out.print("{" + str + "}\t");
//		}
		
//		extractParts();
		initEmoticonRaw();
		initEmoticon();
		initTriplet();
		extractParts();
		process();
		// ｡(*^▽^*)ゞ
//		for (String s : notFound.keySet()) {
//			Emoticon e = notFound.get(s); 
//			System.out.println(e.content + "\t" + e.happiness + " "
//					+ e.sadness + " " + e.fear + " " + e.anger + " "
//					+ e.disgust + " " + e.surprise + " " + e.love + ", " + e.occurrence);
//		}
//		insertIntoDatabase();
//		depart("(〃＾)(*￣▽￣*)v");
	}
}
