package dao;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import org.json.JSONObject;

import database.Define;

import tool.database.ValueTransfer;

public class ReplyDAO implements DAO {
	
	public String getTableName() { return "reply_video"; }
	public String getTypeNamespace() { return type_namespace.divide(Define.type_namespace_gain).toString(); }
	
	public static BigInteger website_namespace = null;
	public static BigInteger type_namespace = new BigInteger("500000000000000");
	public static BigInteger ID = null;
	
	public BigInteger reply_ID;    //标识其回复的video
	public BigInteger nested_reply_ID;   //回复的回复
	public BigInteger author;    //作者昵称或者姓名
	public Date date;    //文章创建的日期（也可能是被转载的时间）
	public Time time;    //文章创建的时间（也可能是被转载的时间）
	public int floor;
	public String content;
	public int like_count;   //喜欢，顶，大拇指，支持……
	
	public ReplyDAO() {
		reply_ID = null;
		nested_reply_ID = null;
		author = null;
		date = null;
		time = null;
		floor = -1;
		content = null;
		like_count = -1;
	}
	
	public BigInteger getID()
	{
		ID = ID.add(new BigInteger("1"));
		return ID;
	}
	
	public void setReplyObject(BigInteger _reply_ID, BigInteger _nested_reply_ID){
		reply_ID = _reply_ID;
		nested_reply_ID = _nested_reply_ID;
	}
	
	public void setStatistic(int _like_count, int _hate_count){
		like_count = _like_count;
	}
	
	public void setMetadata(BigInteger _author, String _date, String _time){
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
	}
	/*
	public void setAddContent(String _item, String _content){
		if ( content == null )
			content = new JSONObject();
		content.put(_item, _content);
	}*/
	
	public static void configure( String _website_namespace, String _start_ID )
	{
		initWebsiteNamespace(_website_namespace);
		initStartID(_start_ID);
	}
	
	private static void initStartID(String _start_ID) {
		ID = website_namespace.add(type_namespace).add( new BigInteger(_start_ID) ).add(new BigInteger("-1"));
	}
	
	private static void initWebsiteNamespace(String namespace){
		website_namespace = new BigInteger(namespace).multiply(new BigInteger("10000000000000000"));
	}

	@Override
	public String[] insert() {
		String[] command = new String[1];
		command[0] = "insert into `reply` "
				+ "(`ID`,`reply_ID`,`nested_reply_ID`,"
				+ "`author`,`date`,`time`,"
				+ "`content`,`floor`,`like_count`)" + " values " + "( "
				+ ValueTransfer.SqlValueFor(getID()) + ", " + ValueTransfer.SqlValueFor(reply_ID) + ", "
				+ ValueTransfer.SqlValueFor(nested_reply_ID) + ", " + ValueTransfer.SqlValueFor(author) + ", "
				+ ValueTransfer.SqlValueFor(date) + ", " + ValueTransfer.SqlValueFor(time) + ", "
				+ ValueTransfer.SqlValueFor(floor) + ", "
				+ ValueTransfer.SqlValueFor(content.toString()) + ", " + ValueTransfer.SqlValueFor(like_count) + ", "
				+ ")";
		return command;
	}

	@Override
	public String[] delete(String website_namespace, String start_ID,
			String end_ID) {
		BigInteger namespace = new BigInteger(website_namespace).multiply(
				new BigInteger("10000000000000000")).add(
				ReplyVideoDAO.type_namespace);
		BigInteger lower, higher;
		if (start_ID == null) {
			lower = namespace;
		} else {
			lower = namespace.add(new BigInteger(start_ID));
		}
		if (end_ID == null) {
			higher = namespace.add(new BigInteger("99999999999999"));
		} else {
			higher = namespace.add(new BigInteger(end_ID));
		}
		String[] command = new String[1];
		command[0] = new String("delete from `reply` where `ID`>="
				+ lower.toString() + " and `ID`<=" + higher.toString());
		return command;
	}
	
}
