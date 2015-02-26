package processing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.ISHCDataOperator;

public class TrigramTest {
	ISHCDataOperator op = null;
	Map<String, Trigram> ta = null;
	ArrayList<Emoticon> ea = null;
	ArrayList<Emoticon> tea = null;
	
	TrigramTest() {
		op = new ISHCDataOperator();
		ea = new ArrayList<Emoticon>();
		getEmo();
//		System.out.println(ba.size());		
	}
	
	public Map<String, Trigram> getTrigram() {
		return ta;
	}
	
	public void internal_test() {
		for (int i = 0;i < 10;i++){
			ArrayList<Emoticon> partea = (ArrayList<Emoticon>) ea.clone();
			tea = new ArrayList<Emoticon>();
			for (int j = 0;j < 1056; j++){
				tea.add(ea.get(i*1056+j));
			}
			partea.subList(i*1056, i*1056+1055).clear();
			generateTrigram(partea);
			for (Map.Entry<String, Trigram> entry: ta.entrySet()){
				Trigram temp = entry.getValue();
				temp.calc();
//				System.out.println(temp);
				ta.put(entry.getKey(), temp);
			}
//			getEmo(i);
			calc_test();
		}
	}
	
	private void calc_test() {
		int total_count = 0;
		int hit_count = 0;
		
		for (Emoticon e: tea){
			double[] log_sum = {0, 0, 0, 0, 0, 0, 0};
			
			total_count += 1;
			
			for (int i = 0;i < e.content.length() - 2;i++){
				String temp = e.content.substring(i, i + 3);
				if (ta.containsKey(temp)){
					Trigram t = ta.get(temp);
					for (int j = 0;j < log_sum.length;j++){
						log_sum[j] += t.freq[j];
					}
				}
			}
			
			double max = Integer.MIN_VALUE;
			for (int i = 0;i < log_sum.length;i++){
				if (log_sum[i] < 0 && log_sum[i] > max){
					max = log_sum[i];
				}
			}

			check:
			for (int i = 0;i < log_sum.length;i++){
				if (log_sum[i] == max){
					if (e.belongs[i]){
						hit_count += 1;
						break check;
					}
				}
			}
//			String str = e.content;
//			String str2 = e.content;
//			for (int i = 0; i < 7;i++) {
//				str = str + ", " + log_sum[i];
//			}
//			for (int i = 0; i < 7;i++) {
//				str2 = str2 + ", " + e.belongs[i];
//			}
//			System.out.println(str);
//			System.out.println(str2);
		}
		
		System.out.println(total_count);
		System.out.println(hit_count);
		System.out.println((double)hit_count/(double)total_count);		
	}
	
	private void generateTrigram() {
		ta = new HashMap<String, Trigram>();
		for (Emoticon e: ea){
			String c = e.content;
			int[] cat = e.category; 
			for (int i = 0; i < c.length() - 2;i++){
				Trigram tri = new Trigram(c.substring(i,i+3), cat);
//				System.out.println(bi);
				if (!ta.containsKey(tri.trigram)){
					ta.put(tri.trigram, tri);
				} else {
					tri.add(ta.get(tri.trigram));
					ta.put(tri.trigram, tri);
				}
			}
		}
	}
	
	private void generateTrigram(ArrayList<Emoticon> partea) {
		ta = new HashMap<String, Trigram>();
		for (Emoticon e: partea){
			String c = e.content;
			int[] cat = e.category; 
			for (int i = 0; i < c.length() - 2;i++){
				Trigram tri = new Trigram(c.substring(i,i+3), cat);
//				System.out.println(bi);
				if (!ta.containsKey(tri.trigram)){
					ta.put(tri.trigram, tri);
				} else {
					tri.add(ta.get(tri.trigram));
					ta.put(tri.trigram, tri);
				}
			}
		}
	}
	
	private void getEmo(int i) {
		ResultSet rs = op.query("SELECT * FROM `new_emoticon` LIMIT " + i * 1056 + ", 1056");
		try {
			while (rs.next()){
				tea.add(new Emoticon(rs.getString("content"), 
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
	
	private void getEmo() {
		ResultSet rs = op.query("SELECT * FROM `new_emoticon` LIMIT 0, 20000");
		try {
			while (rs.next()){
				ea.add(new Emoticon(rs.getString("content"), 
						new int[] {rs.getInt("happiness"), rs.getInt("sadness"), 
					rs.getInt("fear"), rs.getInt("disgust"),
					rs.getInt("anger"), rs.getInt("surprise"),
					rs.getInt("love")}));
//				System.out.println(ea.get(ea.size()-1));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TrigramTest tt = new TrigramTest();
		tt.internal_test();
	}
}
