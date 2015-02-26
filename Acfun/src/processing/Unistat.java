package processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import database.*;

public class Unistat {
	static String root = "C:/large_emoticon_4.txt";
//	static int cate_ind = 1;
	static int EmoticonLength = 3;
	static int FetchSize = 50000;
	ISHCDataOperator op = null;
	ArrayList<String> unigram = null;
	ArrayList<Danmaku> content = null;
	ArrayList<Danmaku> include = null;
	
	Unistat(){
		op = new ISHCDataOperator();
		unigram = new ArrayList<String>();
		include = new ArrayList<Danmaku>();
		getUni();
		execute();
//		getContent(0);
//		judge("ni bei chao le");
	}
	
	private ArrayList<Danmaku> getContent(int i){
		ResultSet rs = op.query("SELECT `id`, `category`, `content` FROM `danmaku_health` WHERE NOT `type` = 8 LIMIT " + i * FetchSize + ", " + FetchSize);
		ArrayList<Danmaku> temp = new ArrayList<Danmaku>();
		
		try {
			while (rs.next()){
				temp.add(new Danmaku(rs.getString("id"), rs.getInt("category"), rs.getString("content")));
//				System.out.println(rs.getString("content"));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return temp;
	}
	
	private void judge(String str) {
		int totalCount = 0;
		
		Set<Integer> set = new HashSet<Integer>();
		int maxcount = 0;
		
		for (int j = 0; j < str.length(); j++){
			String t = str.substring(j, j + 1);
//			System.out.println(t);
			if (unigram.contains(t)){
				set.add(unigram.indexOf(t));
			} else {
				if (set.size() > maxcount) {
					maxcount = set.size();
				}
				set.clear();
			}
		}
		if (set.size() > maxcount) {
			maxcount = set.size();
		}
		
		if (maxcount >= EmoticonLength){
			System.out.println(str);
			totalCount++;
		}
		
		System.out.println(totalCount);
	}
	
	private void execute(){
		BufferedWriter bw = null;
		
		try {			
			ResultSet rs = op.query("SELECT count(`id`) AS `count` FROM `danmaku_health` WHERE NOT `type` = 8");
			rs.next();
			int total = rs.getInt("count");
			int totalCount = 0;
			
			for (int i = 0; i <= total / FetchSize; i++){
				System.out.println(i);
				content = getContent(i);
				for (Danmaku d: content){
					Set<Integer> set = new HashSet<Integer>();
					int maxcount = 0;
					if (d.content.length() > 85){
						continue;
					}
					for (int j = 0; j < d.content.length(); j++){
						if (unigram.contains(d.content.substring(j, j + 1))){
							set.add(unigram.indexOf(d.content.substring(j, j + 1)));
						} else {
							if (set.size() > maxcount) {
								maxcount = set.size();
							}
							set.clear();
						}
					}
					if (set.size() > maxcount) {
						maxcount = set.size();
					}
					if (maxcount >= EmoticonLength){
						include.add(d);
//						bw.write(d.id.toString());
//						bw.write("\t");
//						bw.write(String.valueOf(d.category));
//						bw.write("\t");
//						bw.write(d.content);
//						bw.newLine();
//						bw.flush();
//						System.out.println(str);
						totalCount++;
					}
				}
			}
			File output = new File(root);
			bw = new BufferedWriter(new FileWriter(output));
//			for (int i = totalCount - 1; i >= 250; i--){
//				include.remove(Random.class.newInstance().nextInt(i));
//			}
			for (Danmaku d: include){
				bw.write(d.id.toString());
				bw.write("\t");
				bw.write(String.valueOf(d.category));
				bw.write("\t");
				bw.write(d.content);
				bw.newLine();
				bw.flush();
			}
			
			System.out.println(totalCount);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	
	private void getUni() {
		ResultSet rs = op.query("SELECT `content` FROM `new_emoticon_unigram` ORDER BY `occurrence` DESC LIMIT 0, 200");
		try {
			while (rs.next()) {
				unigram.add(rs.getString("content"));
			}
			unigram.remove(unigram.indexOf("！"));
			unigram.remove(unigram.indexOf("!"));
			unigram.remove(unigram.indexOf("c"));
			unigram.remove(unigram.indexOf("U"));
			unigram.remove(unigram.indexOf("?"));
//			unigram.remove(unigram.indexOf("？"));
			unigram.remove(unigram.indexOf("s"));
			unigram.remove(unigram.indexOf("a"));
			unigram.remove(unigram.indexOf("b"));
			unigram.remove(unigram.indexOf("L"));
			unigram.remove(unigram.indexOf("E"));
			unigram.remove(unigram.indexOf("h"));
			unigram.remove(unigram.indexOf("p"));
			unigram.remove(unigram.indexOf("i"));
			unigram.remove(unigram.indexOf("l"));
			unigram.remove(unigram.indexOf("○"));
			unigram.remove(unigram.indexOf("q"));
			unigram.remove(unigram.indexOf("m"));
			unigram.remove(unigram.indexOf("w"));
			unigram.remove(unigram.indexOf("u"));
			unigram.remove(unigram.indexOf("d"));
			unigram.remove(unigram.indexOf("Y"));
			unigram.remove(unigram.indexOf("゛"));
			unigram.remove(unigram.indexOf("0"));
			unigram.remove(unigram.indexOf(" "));
			unigram.remove(unigram.indexOf("｡"));
			unigram.remove(unigram.indexOf("\""));
			unigram.remove(unigram.indexOf("，"));
			unigram.remove(unigram.indexOf("A"));
			unigram.remove(unigram.indexOf("笑"));
			
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
//		for (String s : unigram){
//			System.out.println(s);
//		}
//		System.exit(0);
	}
	
	public static void main(String[] args){
		Unistat us = new Unistat();
	}
}

class Danmaku {
	BigInteger id = null;
	int category = -1;
	String content = null; 
	
	Danmaku(String bi, int cate, String c) {
		id = new BigInteger(bi);
		category = cate;
		content = c;
	}
}
