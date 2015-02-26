package database;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import tool.DatabaseException;
import tool.database.MysqlConnection;
import tool.database.ValueTransfer;
import dao.*;

public class ISHCDataOperator {
	static int current_batch_size;
	MysqlConnection con;
	Statement global_st;
	
	public ISHCDataOperator() {
		con = new MysqlConnection(
				Define.database_url, Define.username,
				Define.password);
		con.connect();
		global_st = con.createStatement();
	}
	
	public ResultSet query(String query_command){
		return con.query(query_command);
	}
	
	public int update(String update_command){
		return con.update(update_command);
	}
	
//	public BigInteger insert(DAO object) throws SQLException{
//		String[] commands = object.insert();
//		for(String command : commands){
//			cache(command);
//		}
//		/*
//		 * return the ID of the record to be inserted
//		 */
//		String temp = commands[0].split("values")[1].split(",")[0].replace(" ", "");
//		return new BigInteger(temp.substring(1, temp.length()));
//	}
	
	public void insert(DAO object) throws SQLException{
		String[] commands = object.insert();
		for(String command : commands){
			cache(command);
		}
	}
	
	public void delete(DAO object, String website_namespace, String start_ID,
			String end_ID) throws SQLException {
		String[] commands = object.delete(website_namespace, start_ID, end_ID);
		for(String command : commands){
			cache(command);
		}
	}
	
	public BigInteger queryID( String url, DAO dao) throws DatabaseException{
		if(dao.getTableName().matches("reply_.+")){
			throw new DatabaseException("queryID() on "+dao.getClass(), "Failed to queryID (not supported)");
		}
		ResultSet rs = null;
		if(dao.getTableName().matches("user.*")){
			rs = con.query("select `ID` from `user` where `url`=\""+EscapeProcessor.escape(url)+"\"");
		}else{
			rs = con.query("select `ID` from `"+dao.getTableName()+"` where `url`=\""+EscapeProcessor.escape(url)+"\"");
		}
		BigInteger ID = null;
		try {
			if(rs.next())
				ID = rs.getBigDecimal("ID").toBigInteger();
		} catch (SQLException e) {
			ID = null;
		}
		return ID;
	}
	
	public BigInteger queryID_Reply( String content, BigInteger reply_ID, DAO dao ) throws DatabaseException{
		if(!dao.getTableName().matches("reply_.+")){
			throw new DatabaseException("queryID_Reply() on "+dao.getClass(), "Failed to queryID_reply (not supported)");
		}
		ResultSet rs = null;
		rs = con.query("select `ID` from `"+dao.getTableName()+"` where `content`=\""+EscapeProcessor.escape(content)+"\" and `reply_ID`="+reply_ID.toString());
		BigInteger ID = null;
		try {
			if(rs.next())
				ID = rs.getBigDecimal("ID").toBigInteger();
		} catch (SQLException e) {
			ID = null;
		}
		return ID;
	}
	
	public BigInteger queryMaxID(DAO dao, String website_namespace) throws DatabaseException {
		if(dao.getTableName().matches("user_.+")){
			throw new DatabaseException("queryMaxID() on "+dao.getClass(), "Failed to queryMaxID (not supported)");
		}
		FullID min_id = new FullID(website_namespace, dao.getTypeNamespace(), "0");
		FullID max_id = new FullID(website_namespace, dao.getTypeNamespace(), "99999999999999");
		System.out.println("select max(`ID`) from `"+dao.getTableName()+"` where `ID` <= "+max_id.getAbsoluteID()+" and `ID` >= "+min_id.getAbsoluteID());
		ResultSet rs = con.query("select max(`ID`) from `"+dao.getTableName()+"` where `ID` <= "+max_id.getAbsoluteID()+" and `ID` >= "+min_id.getAbsoluteID());
		BigInteger max_ID = null;
		try {
			if( rs.next() )
				max_ID = rs.getBigDecimal(1).toBigInteger();
		} catch (SQLException e) {
			max_ID = null;
		}
		return max_ID;
	}
	
	private void cache(String command) throws SQLException
	{
		System.out.println(command);
		global_st.addBatch(command);
		current_batch_size = current_batch_size + 1;
		if (current_batch_size == Define.sql_cache_size) {
			System.out.println("excuting batch, and reset size.");
			con.excuteBatch(global_st);
			current_batch_size = 0;
		}
	}
	
	public void emptyCache() {
		con.excuteBatch(global_st);
		current_batch_size = 0;
	}
	
	public static void printTerminatePoint()
	{
		System.out.println("Terminated Point: " +
				"\n\tarticle ID: "+ValueTransfer.SqlValueFor(ArticleDAO.ID)+
				"\n\tpost ID: "+ValueTransfer.SqlValueFor(PostDAO.ID)+
				"\n\treply_article ID: "+ValueTransfer.SqlValueFor(ReplyArticleDAO.ID)+
				"\n\treply_post ID: "+ValueTransfer.SqlValueFor(ReplyPostDAO.ID)+
				"\n\treply_video ID: "+ValueTransfer.SqlValueFor(ReplyVideoDAO.ID)+
				"\n\tuser ID: "+ValueTransfer.SqlValueFor(UserDAO.ID)+
				"\n\tvideo ID: "+ValueTransfer.SqlValueFor(VideoDAO.ID));
	}
	
	public void disconnect()
	{
		con.disconnect();
	} 
	
	public static void main(String[] args) {
		try{
			ISHCDataOperator db = new ISHCDataOperator();
			BigInteger b = db.queryID_Reply("{\"全文\":\"因为着凉吗？多喝热水这样会快点好，平常多穿衣服。\"}", new BigInteger("00040100000000000001"), new VideoDAO());
			System.out.println(b);
			db.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			printTerminatePoint();
		}
	}
}

