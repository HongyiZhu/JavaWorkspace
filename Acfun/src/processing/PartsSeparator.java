package processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PartsSeparator {
	public static String dictroot = null;
	
	PartsSeparator(){
		
	}
	
	PartsSeparator(String filename) {
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
	public String[] extract(String instr) {	
		
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

	public static void main(String[] args) {
//		PartsSeparator ps = new PartsSeparator("test.txt");
//		ps.dictroot = "C:/";
		
		PartsSeparator ps2 = new PartsSeparator();
		for (String str :ps2.extract("=_=")){
			System.out.print("{" + str + "}\t");
		}
	}
}
