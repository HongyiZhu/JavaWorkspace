package tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class UrlBucket {
	
	HashSet<String> bucket = new HashSet<String>();
	
	public void add(String url){
		bucket.add(url.split("39.net/")[1]);
	}
	
	public boolean has(String url){
		return bucket.contains(url.split("39.net/")[1]);
	}

	public void remove(String url){
		bucket.remove(url);
	}
	
	public void save(String filename){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			Iterator<String> i = bucket.iterator();
			while(i.hasNext()){
				bw.write(i.next());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(String filename){
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(filename));
			bucket.clear();
			String line = br.readLine();
			while(line!=null){
				bucket.add(line);
				line = br.readLine();
			}
			br.close();
		}catch(IOException e){e.printStackTrace();}
	}
}
