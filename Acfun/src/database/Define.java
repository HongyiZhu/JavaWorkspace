package database;

import java.math.BigInteger;
import java.util.HashMap;

public class Define {
	/*
	 * database parameter
	 */
	
	//Platina's database
//	public static final String database_url = "jdbc:mysql://192.168.1.100:3306/emoticon";
	public static final String database_url = "jdbc:mysql://AI-ZZ30.ailab.eller.arizona.edu:3306/emoticon";
	public static final String username = "zhuhy10";
	public static final String password = "123";
	
	//localhost's database
//	public static final String database_url = "jdbc:mysql://127.0.0.1:3306/taobao";
//	public static final String username = "zhuhy";
//	public static final String password = "123";
	
	//West_ishc's database
//	public static final String database_url = "jdbc:mysql://166.111.134.20:3307/ishc_data";
//	public static final String username = "ishc";
//	public static final String password = "West_ishc2013";
	
	/*
	 * global parameter
	 */
	public static final int sql_cache_size = 1;
	
	
	/*
	 * Rule Definition Of ID
	 */
	public static final BigInteger website_namespace_gain = new BigInteger("10000000000000000");
	public static final BigInteger type_namespace_gain = new BigInteger("100000000000000");
	public static HashMap<String, String> TYPE_NS;
	public static HashMap<String, String> WEBSITE_NS;
	
	static {
		TYPE_NS = new HashMap<String, String>();
		TYPE_NS.put("emoticon", "01");
		TYPE_NS.put("video", "02");
		TYPE_NS.put("user", "03");
		TYPE_NS.put("danmaku", "04");
		TYPE_NS.put("reply", "05");
		
		WEBSITE_NS = new HashMap<String, String>();
		WEBSITE_NS.put("www.bilibili.tv", "001");
		WEBSITE_NS.put("www.acfun.tv", "002");
		WEBSITE_NS.put("sports.acufn.tv", "003");
	}
}
