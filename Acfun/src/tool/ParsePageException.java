package tool;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

public class ParsePageException extends Exception{
	public String url;
	public String type;
	static HashSet<String> list;
	static {
		list = new HashSet<String>();
	}
	
	public ParsePageException(String _url, String _type) {
		super(_type+" ["+_url+"]");
		url = _url;
		type = _type;
		list.add(url+" ["+type+"]");
	}
	
	public static void save(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			Iterator<String> i = list.iterator();
			while(i.hasNext()){
				bw.write(i.next());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
