package processing;

import java.util.List;
import java.util.Map;


class Danmaku2 {
	String id = null;
	String content = null; 
	int type = 0;
	int[] temp = null;
	public int[] category = {0, 0, 0, 0, 0, 0, 0};
	public int occurrence = 0;
	Map<String, Unigram> mu;
	Map<String, Bigram> mb;
	Map<String, Trigram> mt;
	String maptype;
	
	public boolean found = false;
	public boolean hit = false;
	String extracted = null;
	public int caltype = 0;	
	
	Danmaku2(String bi, String c, int t, int[] cate, int occur) {
		id = bi;
		content = c;
		temp = cate;
		type = t;
		occurrence = occur;
		category = cate;
	}

	
	private void calc() {
		if (maptype.equals("bi")){
			double[] log_sum = {0, 0, 0, 0, 0, 0, 0};
			
	//		System.out.println(extracted.length());
			for (int i = 0;i < extracted.length() - 1;i++){
				String ch = extracted.substring(i, i + 2);
	//			System.out.println(i + ch);
				if (mb.containsKey(ch)){
					Bigram b = mb.get(ch);
					for (int j = 0;j < log_sum.length;j++){
						log_sum[j] += b.freq[j];
					}
				}
			}
			
			double max = Integer.MIN_VALUE;
			for (int i = 0;i < log_sum.length;i++){
	//			System.out.println(log_sum[i]);
				if (log_sum[i] > max){
					max = log_sum[i];
				}
			}
			
			for (int i = 0;i < log_sum.length;i++){
				if (log_sum[i] == max){
					if (category[i] == 1){
						hit = true;
					}
					break;
				}
			}
		} else if (maptype.equals("tri")){
			double[] log_sum = {0, 0, 0, 0, 0, 0, 0};
			
	//		System.out.println(extracted.length());
			for (int i = 0;i < extracted.length() - 2;i++){
				String ch = extracted.substring(i, i + 3);
	//			System.out.println(i + ch);
				if (mt.containsKey(ch)){
					Trigram t = mt.get(ch);
					for (int j = 0;j < log_sum.length;j++){
						log_sum[j] += t.freq[j];
					}
				}
			}
			
			double max = Integer.MIN_VALUE;
			for (int i = 0;i < log_sum.length;i++){
	//			System.out.println(log_sum[i]);
				if (log_sum[i] > max){
					max = log_sum[i];
				}
			}
			
			for (int i = 0;i < log_sum.length;i++){
				if (log_sum[i] == max){
					if (category[i] == 1){
						hit = true;
					}
					break;
				}
			}
		} else {
			double[] log_sum = {0, 0, 0, 0, 0, 0, 0};
			
	//		System.out.println(extracted.length());
			for (int i = 0;i < extracted.length();i++){
				String ch = extracted.substring(i, i + 1);
	//			System.out.println(i + ch);
				if (mu.containsKey(ch)){
					Unigram u = mu.get(ch);
					for (int j = 0;j < log_sum.length;j++){
						log_sum[j] += u.category[j];
					}
				}
			}
			
			double max = Integer.MIN_VALUE;
			for (int i = 0;i < log_sum.length;i++){
	//			System.out.println(log_sum[i]);
				if (log_sum[i] > max){
					max = log_sum[i];
				}
			}
			
			for (int i = 0;i < log_sum.length;i++){
				if (log_sum[i] == max){
					if (category[i] == 1){
						hit = true;
					}
					break;
				}
			}		
		}
	}
	
	public void setMap(Map<String, ?> map, String type) {
		if (type.equals("bi")) {
			mb = (Map<String, Bigram>) map;
		} else {
			mt = (Map<String, Trigram>) map;
		}
		maptype = type;
	}
	
	public void calClass(Map<String, Unigram> m, List<String> lt, List<String> le) {
		mu = m;
		for (String ee: le){
			if (this.content.contains(ee)){
				extracted = ee;
				found = true;
				break;
			}
		}
		if (!found){
			for (String tt: lt){
				if (this.content.contains(tt)){
					extracted = tt;
					found = true;
					break;
				}
			}
		}
		if (found){
			calc();
		}
	}
	
	public void calClass(List<String> lt, List<String> le) {
		for (String ee: le){
			if (this.content.contains(ee)){
				extracted = ee;
				found = true;
				break;
			}
		}
		if (!found){
			for (String tt: lt){
				if (this.content.contains(tt)){
					extracted = tt;
					found = true;
					break;
				}
			}
		}
		if (found){
			calc();
		}
	}
}