/*
 * MineQuest - Hey0 Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2010  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class mysql_interface {
	private Statement stmt;
	String url;
	java.sql.Connection con;
    private Logger log;
    String user, pass;
    boolean silent;
	
	
	public void setup(String location, String port, String db, String user, String pass, int silent) {
		url = "jdbc:mysql://" + location + ":" + port + "/" + db;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("You appear to be missing MySQL JDBC");
			e.printStackTrace();
			return;
		}
		this.user = user;
		this.pass = pass;
		reconnect();
		if (silent > 0) {
			this.silent = true;
			return;
		}
		this.silent = false;
        log = Logger.getLogger("Minecraft");
	}
	
	public void reconnect() {
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
	
	public ResultSet query(String the_query) {
		if (!silent) {
			log.info("[MineQuest] (MySQL) " + the_query);
		}
		try {
			return stmt.executeQuery(the_query);
		} catch (SQLException e) {
			log.info("[MineQuest] (MySQL) Failed to query database");
			reconnect();
			try {
				return stmt.executeQuery(the_query);
			} catch (SQLException e1) {
				return null;
			}
		}
	}
	
	public int update(String sql) {
		if (!silent) {
			log.info("[MineQuest] (MySQL) " + sql);
		}
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			log.info("[MineQuest] (MySQL) Failed to update database");
			reconnect();
			try {
				return stmt.executeUpdate(sql);
			} catch (SQLException e1) {
				return -1;
			}
		}
	}
}