/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
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
package org.monk.MineQuest;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MysqlInterface is a class that wraps the JDBC
 * 
 * @author jmonk
 */
public class MysqlInterface {
	private Statement stmt;
	String url;
	java.sql.Connection con;
    String user, pass;
    boolean silent;
	
	/**
	 * Creates a Wrapper to query a MySQL DB.
	 * 
	 * @param location URL of DB
	 * @param port Port Number of DB
	 * @param db Database to use
	 * @param user Username to connect
	 * @param pass Password to connect
	 * @param silent Whether or not to log queries
	 */
	public MysqlInterface(String location, String port, String db, String user, String pass, int silent) {
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
	}
	
	/**
	 * Reconnect to the database with same parameters as before.
	 */
	public void reconnect() {
		try {
			con = (Connection) DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			MineQuest.log("[ERROR] Unable to Connect to MySQL Databse");
			e.printStackTrace();
			return;
		}
		
		 try {
			stmt = (Statement) con.createStatement();
		} catch (SQLException e) {
			MineQuest.log("[ERROR] Failed to setup MySQL Statement");
			e.printStackTrace();
		}
	}
	
	/**
	 * Queries the database with the_query. Upon failure it will
	 * reconnect to the database and try again.
	 * 
	 * @param the_query Query to Database.
	 * @return ResultSet from query.
	 */
	public ResultSet query(String the_query) {
		if (!silent) {
			MineQuest.log("[MineQuest] (MySQL) " + the_query);
		}
		try {
			return stmt.executeQuery(the_query);
		} catch (SQLException e) {
			MineQuest.log("[ERROR] Failed to query database");
			e.printStackTrace();
			reconnect();
			try {
				return stmt.executeQuery(the_query);
			} catch (SQLException e1) {
				return null;
			}
		}
	}
	
	/**
	 * Updates the database based on sql update string. Upon failure
	 * it will reconnect and try to update again.
	 * 
	 * @param sql SQL Update String
	 * @return Non-zero upon failure
	 */
	public int update(String sql) {
		if (!silent) {
			MineQuest.log("(MySQL) " + sql);
		}
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			MineQuest.log("[ERROR] Failed to update database");
			e.printStackTrace();
			reconnect();
			try {
				return stmt.executeUpdate(sql);
			} catch (SQLException e1) {
				return 1;
			}
		}
	}
}