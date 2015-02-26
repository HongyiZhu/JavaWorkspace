package dao;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;

import org.json.JSONObject;

import database.Define;
import tool.database.ValueTransfer;

public class EmoticonDAO implements DAO {
	
	public String getTableName() { return "emoticon"; }
	public String getTypeNamespace() { return type_namespace.divide(Define.type_namespace_gain).toString(); }
	
	public static BigInteger website_namespace = null;
	public static BigInteger type_namespace = new BigInteger("100000000000000");
	
	public static BigInteger ID = null;
	public static String root = null;
	public String content;
	public String type;
	public int occurrence;
	
	public EmoticonDAO() {
		content = null;
		type = null;
		occurrence = 0;
	}
	
	public BigInteger getID()
	{
		ID = ID.add(new BigInteger("1"));
		return ID;
	}
	/*
	public void setPosition(String _url, String _path){
		url = _url;
		path = _path;
	}
	
	public void setStatistic(int _like_count, int _hate_count, int _click_count, int _share_count, int _favor_count, int _reply_count){
		like_count = _like_count;
		hate_count = _hate_count;
		click_count = _click_count;
		share_count = _share_count;
		favor_count = _favor_count;
		reply_count = _reply_count;
	}
	
	public void setMetadata(BigInteger _author, String _date, String _time, String _channel, String _tag, String _source, String _title){
		if(_date != null){
			String[] date_temp = _date.split("-");
			String formated_date_string = String.format("%d-%02d-%02d", Integer.parseInt(date_temp[0]), Integer.parseInt(date_temp[1]), Integer.parseInt(date_temp[2]));
			date = Date.valueOf(formated_date_string);
		}
		if(_time != null){
			String[] time_temp = _time.split(":");
			String formated_time_string = String.format("%02d:%02d:%02d", Integer.parseInt(time_temp[0]), Integer.parseInt(time_temp[1]), Integer.parseInt(time_temp[2]));
			time = Time.valueOf(formated_time_string);
		}
		author = _author;
		channel = _channel;
		tag = _tag;
		source = _source;
		title = _title;
	}
	
	public void setAddContent(String _item, String _content){
		if ( content == null )
			content = new JSONObject();
		content.put(_item, _content);
	}
	*/
	public static void configure( String _root, String _namespace, String _start_ID )
	{
		initRootWebsite(_root);
		initWebsiteNamespace(_namespace);
		initStartID(_start_ID);
	}
	
	private static void initStartID(String _start_ID) {
		ID = website_namespace.add(type_namespace).add( new BigInteger(_start_ID) ).add(new BigInteger("-1"));
	}

	private static void initRootWebsite(String _root) {
		root = _root;
	}
	
	private static void initWebsiteNamespace(String namespace){
		website_namespace = new BigInteger(namespace).multiply(new BigInteger("10000000000000000"));
	}

	@Override
	public String[] insert() {
		String[] command = new String[1];
		command[0] = "insert into `emoticon` " +
				"(`id`,`root`,`content`,`type`)" +
				" values " +
				"( "+ ValueTransfer.SqlValueFor(ID) +", "+ ValueTransfer.SqlValueFor(root) +", "+ ValueTransfer.SqlValueFor(content) +", "+ ValueTransfer.SqlValueFor(type) 
				+")";
		return command;
	}

	@Override
	public String[] delete(String website_namespace, String start_ID,
			String end_ID) {
		BigInteger namespace = new BigInteger(website_namespace).multiply(new BigInteger("10000000000000000")).add(VideoDAO.type_namespace);
		BigInteger lower, higher;
		if(start_ID == null){
			lower = namespace;
		}else{
			lower = namespace.add(new BigInteger(start_ID));
		}
		if(end_ID == null){
			higher = namespace.add(new BigInteger("99999999999999"));
		}else{
			higher = namespace.add(new BigInteger(end_ID));
		}
		String[] command = new String[1];
		command[0] = new String("delete from `emoticon` where `ID`>="+lower.toString()+" and `ID`<="+higher.toString());
		return command;
	}
	
}
