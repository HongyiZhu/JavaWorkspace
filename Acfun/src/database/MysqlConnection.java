
package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlConnection {
	String url;
	String username;
	String passwd;
	
	private Connection conn = null;
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("data base add succeed");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Statement createStatement() throws SQLException
	{
		return conn.createStatement();
	}
	public MysqlConnection()
	{
		url = new String( "jdbc:mysql://localhost:3306/library" );
		username = new String( "root" );
		passwd = new String( "xinsheng2012" );
	}
	
	public MysqlConnection(String _url, String _username, String _passwd)
	{
		url = _url;
		username = _username;
		passwd = _passwd;
	}
	
	public PreparedStatement prepareStatement(String arg) throws SQLException
	{
		return conn.prepareStatement(arg);
	}
	
	// 连接数据库
	public Connection connect() {
		java.util.Properties prop = new java.util.Properties();
		prop.put("charSet", "utf8");
		prop.put("user", username);
		prop.put("password", passwd);
		try {
			conn = DriverManager.getConnection(url, prop);
			if (conn != null) {
				//System.out.println("data link succeed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void setAutoCommit(boolean b)
	{
		try{
			conn.setAutoCommit(b);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void excuteBatch(Statement st)
	{
		try{
			setAutoCommit(false);
			st.executeBatch();
			conn.commit();
			setAutoCommit(true);
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	// 插入一个记录
	public int update(String sqlinsert) {
		try {
			Statement st = conn.createStatement();
			int count = st.executeUpdate(sqlinsert);
			return count;
		} catch (SQLException e) {
			//System.out.println(sqlinsert);
			e.printStackTrace();
		}
		return -1;
	}
	
	public ResultSet query(String sqlquery){
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlquery);
			return rs;
		} catch (SQLException e) {
			//System.out.println(sqlquery);
			e.printStackTrace();
		}
		return null;
	}
}
