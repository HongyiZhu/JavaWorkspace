package processing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.*;


public class UnigramTest_Orig {
	ISHCDataOperator op = null;
	Map<String, Unigram> ua = null;
	ArrayList<Emoticon> ea = null;
	
	UnigramTest_Orig() {
		op = new ISHCDataOperator();
		ua = new HashMap<String, Unigram>();
		
		getUni();
		for (int i = 0;i < 10;i++){
			ea = new ArrayList<Emoticon>();
			getEmo(i);
			calc_test();
		}
	}
	
	private void calc_test() {
		int total_count = 0;
		int hit_count = 0;
		
		for (Emoticon e: ea){
			double[] log_sum = {0, 0, 0, 0, 0, 0, 0};
			
			total_count += 1;
			
			for (int i = 0;i < e.content.length();i++){
				String temp = e.content.substring(i, i + 1);
				if (ua.containsKey(temp)){
					Unigram u = ua.get(temp);
					for (int j = 0;j < log_sum.length;j++){
						log_sum[j] += u.category[j];
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
		}
		
		System.out.println(total_count);
		System.out.println(hit_count);
		System.out.println((double)hit_count/(double)total_count);
	}
	
	private void getUni() {
		ResultSet rs = op.query("SELECT * FROM `new_emoticon_unigram` LIMIT 0, 20000");
		try {
			while (rs.next()){
				ua.put(rs.getString("content"), new Unigram(rs.getString("content"), rs.getInt("occurrence"), 
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
	
	private void getEmo(int i) {
		ResultSet rs = op.query("SELECT * FROM `new_emoticon` LIMIT " + i * 1056 + ", 1056");
		try {
			while (rs.next()){
				ea.add(new Emoticon(rs.getString("content"), 
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
	
	public static void main(String[] args) {
		UnigramTest_Orig ut = new UnigramTest_Orig();
	}

}

class Emoticon {
	public String content = null;
	int[] category = null;
	public boolean[] belongs = {false, false, false, false, false, false, false};
	
	Emoticon(String c, int[] ca) {
		content = c;
		category = ca;
		caltype(category);
	}
	
	private void caltype(int[] c) {
		int max = -1;
		
		for (int i = 0;i < c.length;i++){
			if (c[i] > max){
				max = c[i];				
			}
		}
		for (int i = 0;i < c.length;i++){
			if (c[i] == max){
				belongs[i] = true;
			}
		}
	}
	
	public String toString() {
		String str = this.content;
		for (int i = 0;i < 7;i++){
			str = str + ", " + this.category[i]; 
		}
		return str;
	}
}
