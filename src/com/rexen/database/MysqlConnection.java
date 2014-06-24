package com.rexen.database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.rexen.configs.LogWrite;
import com.rexen.configs.Property;
import com.rexen.configs.csvTrans;

/***
 * 
 * @author Sam Rexen Technology
 * 
 */
public class MysqlConnection {// 连接Mysql的java类

	final private String Target = "_SalesforceToMysql";
	LogWrite lw = new LogWrite();

	public Connection connect(Connection conn) { // 建立连接
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://" + Property.db_mysql_host + ":"
				+ Property.db_mysql_port + "/" + Property.db_mysql_schema
				+ "?useUnicode=true&characterEncoding=utf8";
		String user = Property.db_mysql_username;
		String password = Property.db_mysql_password;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (CommunicationsException e) {
			System.out.println("Sorry,you mysql engineer is stop!");
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			// e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("连接失败！检查你的配置信息！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void closeAll(Statement stat, Connection conn) { // 关闭连接
		if (stat != null)
			try {
				stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public ArrayList<HashMap<Integer, String>> queryMysqlIngoreNull(String sql,
			String[] array) {   //要注意Salesforce 是不能插入Id的   三个参数分别为（sql语句，数据库数组模板）

		Connection conn = null;   
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<HashMap<Integer, String>> list = new ArrayList<HashMap<Integer, String>>();
		try {
			conn = connect(conn);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);// sql为sql语句例如"select * from 表名"，从方法中传进来，这里用的是ArrayList

			while (rs.next()) {
				HashMap<Integer, String> m = new HashMap<Integer, String>();
				for (int i = 0; i < array.length; i++) {
					if (rs.getString(array[i]) != null) {
						   m.put(i, rs.getString(array[i]).toString());
					}
				}
				list.add(m);
				// System.out.println("成功！"+rs.getString("name"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(stmt, conn);
		}
		return list;
	}
	

	/**
	 * 这个方法在salesforce 中值为空会插入空字符串 而SalesforceInsertMysql()方法插入值时会忽略控制
	 * 在查询时查询到的空值也不会保存
	 * 
	 * @param csvpath
	 */
	
	public boolean ifExistRecord(String Object,String salesforceid, String salesforceidvalue) {
		boolean flag = false;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		// getConnection();
		String sql = "select * from " + Object + " where " + salesforceid + " = '" + salesforceidvalue + "'";
		try {
			conn = connect(conn);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			closeAll(stmt, conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	public String SalesforceInsertMysql(String csvpath) { // 插入数据 返回日志文件名称
													
		Connection conn = null;
		Statement dbstate = null;
		int insertsum = 0;
		int updatesum = 0;
		String status = " ";

		String writelog = null;
		csvTrans ct = new csvTrans(csvpath);
		SalesforceConnection sfc = new SalesforceConnection();
		String values = "";
		String fields = "";
		int salesforceidRow = ct.getSalesforceRow(ct.csvTransArray(0), "id");

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String fileName = df.format(new Date()) + Target + "_"
				+ ct.salesforceSchema() + "_Sync" + ".txt";
		String logFilepath = Property.log_filepath + fileName; // log文件命名规则:
																// 路径+时间+Target+Object+CRUD.txt

		// 加条件处
		String searchsql = "SELECT " + ct.csvTransString(0) + " FROM " + ct.salesforceSchema() ;
		// String searchsql="SELECT " + ct.csvTransString(0)+ " FROM " +
		// ct.salesforceSchema()+" limit 1";

		ArrayList<HashMap<Integer, String>> al = sfc.queryIngoreNull(searchsql,
				ct.csvTransArray(0));

		conn = connect(conn);
		System.out.println("Salesforce 向 Mysql 插入/ 更新 数据开始执行！");
		lw.Writelog("Database：  " + Property.db_type + "   Host："
				+ Property.db_mysql_host + "   Schema:"
				+ Property.db_mysql_schema + "   OS:" + Property.os,
				logFilepath);
		for (int i = 0; i < al.size(); i++) {
			try {		
				HashMap<Integer, String> hm = al.get(i);
				if(ifExistRecord(ct.databaseSchema(),ct.getString(salesforceidRow + 1, 1), hm.get(salesforceidRow))){     //  更新			
					for (Integer key : hm.keySet()) {
						if (values.equals("")) {
							fields = ct.getString(key + 1, 1) + "=" + "'" + hm.get(key) + "'"; //fields用于拼写更新语句 
							values = "'" + hm.get(key) + "'";   // values用于写入logs
						} else {
							fields = fields + "," + ct.getString(key + 1, 1) + "=" + "'" + hm.get(key) + "'";
							values = values + "," + "'" + hm.get(key) + "'";

						}
					}
					
					String updatesql="update " + ct.databaseSchema() +" set " + fields +" where " + ct.getString(salesforceidRow + 1, 1) + "='" + hm.get(salesforceidRow) + "'";			
					dbstate = conn.createStatement();
					dbstate.executeUpdate(updatesql);
					updatesum++;
					status = "     Update Success";
					writelog = values + status;
					
				}else{  //插入
					for (Integer key : hm.keySet()) {
						if (values.equals("")) {
							fields = ct.getString(key + 1, 1);
							values = "'" + hm.get(key) + "'";
						} else {
							fields = fields + "," + ct.getString(key + 1, 1);
							values = values + "," + "'" + hm.get(key) + "'";
						}
						// System.out.println("key= "+ key + " and value= " + hm.get(key));
					}
					// System.out.println("fields:"+fields);
				    // System.out.println("values:"+values);

					dbstate = conn.createStatement();
					String insertsql = "insert into " + ct.databaseSchema() + " ("
							+ fields + ") values(" + values + ")";
					// System.out.println("insertsql:"+insertsql);
					dbstate.executeUpdate(insertsql);
					insertsum++;
					status = "     Insert Success";
					writelog = values + status;				
					
				}		
			} catch (MySQLIntegrityConstraintViolationException e) {
				writelog = values + "错误原因：ID 重复";
			    e.printStackTrace();
			} catch (SQLException e) {
				writelog = values + "错误原因：SQLException";
				e.printStackTrace();
			} finally {
				fields = "";
				values = "";
				lw.Writelog(writelog, logFilepath);
			}

		}
		writelog = "共 " + al.size() + " 条数据，插入成功 " + insertsum + " 条！" + " 更新成功 "+ updatesum +" 条！";
		lw.Writelog(writelog, logFilepath);
		System.out.println("共 " + al.size() + " 条数据，插入成功 " + insertsum
				+ " 条！" + " 更新成功 "+ updatesum +" 条！" + " 查看日志获得详细情况");
		try {
			dbstate.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("SQLException!");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			System.out.println("NullPointerException!");
		}
		
		closeAll(dbstate, conn);
		return logFilepath;
	}
}