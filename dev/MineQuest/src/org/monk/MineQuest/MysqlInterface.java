/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
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

import org.monk.MineQuest.Ability.AbilityBinder;

/**
 * MysqlInterface is a class that wraps the JDBC
 * 
 * @author jmonk
 */
public class MysqlInterface {
	private Statement stmt;
	private String url;
	private java.sql.Connection con;
	private String user, pass;
    private boolean silent;
    private ResultSet last;
    private boolean real;
	
	/**
	 * Creates a Wrapper to query a MySQL DB.
	 * 
	 * @param location URL of DB
	 * @param port Port Number of DB
	 * @param db Database to use
	 * @param user Username to connect
	 * @param pass Password to connect
	 * @param silent Whether or not to log queries
	 * @throws Exception 
	 */
	public MysqlInterface(String location, String port, String db, String user, String pass, int silent, boolean real_sql) throws Exception {
		if (real_sql) {
			url = "jdbc:mysql://" + location + ":" + port + "/" + db + "?autoReconnect=true";
			try {
				Class.forName("com.mysql.jdbc.Driver", true, (new AbilityBinder()).getClass().getClassLoader());
			} catch (ClassNotFoundException e) {
				MineQuest.log("You appear to be missing MySQL JDBC");
				con = null;
				stmt = null;
				throw new Exception();
			}
		} else {
			url = "jdbc:sqlite:" + db + ".sql";
			try {
				Class.forName("org.sqlite.JDBC", true, (new AbilityBinder()).getClass().getClassLoader());
			} catch (ClassNotFoundException e) {
				MineQuest.log("You appear to be missing SQLite JDBC");
				con = null;
				stmt = null;
				throw new Exception();
			}
		}
		this.real = real_sql;
		this.user = user;
		this.pass = pass;
		reconnect();
		if (silent > 0) {
			this.silent = true;
			return;
		}
		last = null;
		this.silent = false;
	}
	
	/**
	 * Reconnect to the database with same parameters as before.
	 */
	public void reconnect() {
		try {
			if (real) {
				con = (Connection) DriverManager.getConnection(url, user, pass);
			} else {
				con = (Connection) DriverManager.getConnection(url);
			}
		} catch (SQLException e) {
			MineQuest.log("[ERROR] Unable to Connect to MySQL Database");
			return;
		}
		
		 try {
			stmt = (Statement) con.createStatement();
		} catch (SQLException e) {
			MineQuest.log("[ERROR] Failed to setup MySQL Statement");
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
		if (stmt == null) {
			MineQuest.log("You are not connected to a database (try configuring minequest.properties)");
		}
		if (!silent) {
			MineQuest.log("(MySQL) " + the_query);
		}
		try {
			if (last != null) {
				last.close();
				last = null;
			}
			last = stmt.executeQuery(the_query);
			return last;
		} catch (SQLException e) {
			MineQuest.log("(MySQL) " + the_query);
			MineQuest.log("[ERROR] Failed to query database");
			reconnect();
			try {
				last = stmt.executeQuery(the_query);
				return last;
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
		int ret;
		if (stmt == null) {
			MineQuest.log("You are not connected to a database (try configuring minequest.properties)");
		}
		if (!silent) {
			MineQuest.log("(MySQL) " + sql);
		}
		try {
			if (last != null) {
				last.close();
				last = null;
			}
			ret = stmt.executeUpdate(sql);
			return ret;
		} catch (SQLException e) {
			MineQuest.log("(MySQL) " + sql);
			MineQuest.log("[ERROR] Failed to update database");
			e.printStackTrace();
			reconnect();
			try {
				ret = stmt.executeUpdate(sql);
				return ret;
			} catch (SQLException e1) {
				MineQuest.log("Retry Failed!!");
				return 1;
			}
		}
	}

	public int update(String sql, boolean extra_silent) {
		int ret;
		if (stmt == null) {
			MineQuest.log("You are not connected to a database (try configuring minequest.properties)");
		}
		if (!silent) {
			MineQuest.log("(MySQL) " + sql);
		}
		try {
			if (last != null) {
				last.close();
				last = null;
			}
			ret = stmt.executeUpdate(sql);
			return ret;
		} catch (SQLException e) {
			if (!extra_silent) {
				MineQuest.log("(MySQL) " + sql);
				MineQuest.log("[ERROR] Failed to update database");
				e.printStackTrace();
			}
			reconnect();
			try {
				ret = stmt.executeUpdate(sql);
				return ret;
			} catch (SQLException e1) {
				MineQuest.log("Retry Failed!!");
				return 1;
			}
		}
	}
}