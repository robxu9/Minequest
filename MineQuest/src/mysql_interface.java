import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class mysql_interface {
	private Statement stmt;
	String url, user, pass;
	java.sql.Connection con;
    private Logger log;
	
	
	public void setup(String location, String port, String db, String user, String pass) {
		url = "jdbc:mysql://" + location + ":" + port + "/" + db;
		this.user = user;
		this.pass = pass;
		reconnect();
        log = Logger.getLogger("Minecraft");
	}
	
	public void reconnect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("You appear to be missing MySQL JDBC");
			e.printStackTrace();
			return;
		}
		try {
			con = (Connection) DriverManager.getConnection(url, user, pass);// + "?autoReconnect=true&user=" + user + "&password=" + pass);
		} catch (SQLException e) {
			System.out.println("Unable to Connect to MySQL Databse");
			e.printStackTrace();
			return;
		}
		
		 try {
			stmt = (Statement) con.createStatement();
		} catch (SQLException e) {
			System.out.println("Failed to setup MySQL Statement");
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String the_query) throws SQLException {
		log.info("[MineQuest] (MySQL) " + the_query);
		return stmt.executeQuery(the_query);
	}
	
	public int update(String sql) throws SQLException {
		log.info("[MineQuest] (MySQL) " + sql);
		return stmt.executeUpdate(sql);
	}
}