package dao;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import org.json.JSONObject;

import database.Define;

import tool.database.ValueTransfer;

public class VideoDAO implements DAO {
	
	public String getTableName() { return "video"; }
	public String getTypeNamespace() { return type_namespace.divide(Define.type_namespace_gain).toString(); }
	
	public static BigInteger website_namespace = null;
	public static BigInteger type_namespace = new BigInteger("200000000000000");
	
	public static BigInteger ID = null;   //唯一标识，参见2
	public static String root = null;   //例如糖尿病网 www.tnbz.com
	public String url;   //当前页面的url
	public String path;    //一般位于页面上方，格式xx>xxx>xxx
	public BigInteger author;    //创建人，可能是昵称，真实姓名等等
	public Date date;    //文章创建的日期（也可能是被转载的时间）
	public Time time;    //文章创建的时间（也可能是被转载的时间）
	public int no;
	public String tag;   //网站自己加入的标签，多标签以两个半角分号间隔。
	public String title;   //视频题目
	public String intro;   //视频简介
	
	public int view;   //喜欢，顶，大拇指，支持……
	public int favor;   //不喜欢，踩，倒的大拇指，反对……
	public int coin;    //阅读数，浏览数，点击数……
	public int point;    //分享数，转载数……
	public int danmaku;    //被收藏的次数
	public int reply;    //下方回复/评论的个数，有些网站会标明评论的个数
	
	public int danmaku_file_id;
	public String danmaku_file_name;
	
	public VideoDAO(){
		url = null;
		path = null;
		author = null;
		date = null;
		time = null;
		no = 0;
		tag = null;
		title = null;
		intro = null;
		view = -1;
		favor = -1;
		coin = -1;
		point = -1;
		danmaku = -1;
		reply = -1;
		danmaku_file_id = -1;
		danmaku_file_name = null;
	}
	
	public BigInteger getID()
	{
		ID = ID.add(new BigInteger("1"));
		return ID;
	}
	
	public void setPosition(String _url, String _path){
		url = _url;
		path = _path;
	}
	/*
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
		command[0] = "insert into `video` " +
				"(`id`,`root`,`url`,`path`,`author`," +
				"`date`,`time`,`tag`,`title`," +
				"`intro`,`view`,`favor`,`coin`," +
				"`point`,`danmaku`,`reply`,`danmaku_file_id`,`danmaku_file_name`)" +
				" values " +
				"( "+ ValueTransfer.SqlValueFor(ID) +", "+ ValueTransfer.SqlValueFor(root) +", "+ ValueTransfer.SqlValueFor(url) +", "+ ValueTransfer.SqlValueFor(path) +", "+ ValueTransfer.SqlValueFor(author) +
				", "+ ValueTransfer.SqlValueFor(date) +", "+ ValueTransfer.SqlValueFor(time) +", "+ ValueTransfer.SqlValueFor(tag) +", "+ ValueTransfer.SqlValueFor(title) +
				", "+ ValueTransfer.SqlValueFor(intro) +", "+ ValueTransfer.SqlValueFor(view) +", "+ ValueTransfer.SqlValueFor(favor) +", "+ ValueTransfer.SqlValueFor(coin) +
				", "+ ValueTransfer.SqlValueFor(point) +", "+ ValueTransfer.SqlValueFor(danmaku) +", "+ ValueTransfer.SqlValueFor(reply) +", " + ValueTransfer.SqlValueFor(danmaku_file_id) +", " + ValueTransfer.SqlValueFor(danmaku_file_name) + ")";
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
		command[0] = new String("delete from `video` where `ID`>="+lower.toString()+" and `ID`<="+higher.toString());
		return command;
	}
	
}
