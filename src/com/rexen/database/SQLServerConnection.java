package com.rexen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


import com.rexen.configs.Property;

/**
 * @author Sam Rexen Technology
 *         http://www.microsoft.com/en-us/download/details.aspx?id=11774
 */

public class SQLServerConnection {
	public static void main(String[] args) {
		Connection conn = null;
		new SQLServerConnection().connect(conn);
	}
	public Connection connect(Connection conn) {
		String url = "jdbc:sqlserver://" + Property.db_mysql_host + ":"
				+ Property.db_mysql_port + ";databaseName="
				+ Property.db_mysql_schema + ";user="
				+ Property.db_mysql_username + ";password="
				+ Property.db_mysql_password;// sa身份连接
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		System.out.println("url:"+url);
		try {
			// Establish the connection.
			System.out.println("begin.");
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url);
			System.out.println("end.");

			// Create and execute an SQL statement that returns some data.
			String SQL = "SELECT TOP 10 * FROM aud_t_basis";
			stmt = con.createStatement();
			rs = stmt.executeQuery(SQL);

			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				System.out.println(rs.getString(4) + " " + rs.getString(6));
			}
		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}

		}
		return conn;
	}
}
