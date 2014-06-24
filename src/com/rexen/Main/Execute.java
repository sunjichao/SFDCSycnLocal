package com.rexen.Main;

import java.util.ArrayList;

import com.rexen.configs.Property;
import com.rexen.database.MysqlConnection;
import com.rexen.database.OracleConnection;
import com.rexen.database.SalesforceConnection;
import com.rexen.mails.SendMail;

/***
 * 
 * @author Sam Rexen Technology
 * 
 */
public class Execute {

	private static String[] task1 = Property.SalesforceToLocal;
	private static String[] task2 = Property.LocalToSalesforce;
	private static ArrayList<String> al = new ArrayList<String>();
	SalesforceConnection sc = new SalesforceConnection();
	String csvpath;
	String logspath;

	public void mysql() { // 当数据库是Mysql时应该执行此方法
		MysqlConnection mc = new MysqlConnection();
		if (!(task1.length == 1 && task1[0].equals(""))) { // 如果数组长度唯一且值为0，就不执行操作
			for (int i = 0; i < task1.length; i++) {
				csvpath = Property.csvPath + "/" + task1[i] + ".csv";
				logspath = mc.SalesforceInsertMysql(csvpath);
				al.add(logspath);
			}
		}
		if (!(task2.length == 1 && task2[0].equals(""))) {
			for (int j = 0; j < task2.length; j++) {
				csvpath = Property.csvPath + "/" + task2[j] + ".csv";
				logspath = sc.LocalInsertSalesforce(csvpath, "mysql");
				al.add(logspath);
			}
		}
	}

	public void oracle() { // 当数据库是oracle时应该执行此方法
		OracleConnection oc = new OracleConnection();
		if (!(task1.length == 1 && task1[0].equals(""))) {
			for (int i = 0; i < task1.length; i++) {
				csvpath = Property.csvPath + "/" + task1[i] + ".csv";
				logspath = oc.SalesforceInsertOracle(csvpath);
				al.add(logspath);
			}
		}

		if (!(task2.length == 1 && task2[0].equals(""))) {
			for (int j = 0; j < task2.length; j++) {
				csvpath = Property.csvPath + "/" + task2[j] + ".csv";
				logspath = sc.LocalInsertSalesforce(csvpath, "oracle");
				al.add(logspath);
			}
		}
	}

	public void sqlserver() { // 当数据库是sqlserver时应该执行此方法

	}
	
	public static void main(String[] args) {

		Execute ect = new Execute();

		switch (Property.db_type.toLowerCase()) {
		case "oracle":
			System.out.println("您的数据库是Oracle!");
			ect.oracle();
			break;
		case "mysql":
			System.out.println("您的数据库是Mysql!");
			ect.mysql();
			break;
		case "sqlserver":
			System.out.println("您的数据库是Sqlserver!");
			ect.sqlserver();
			break;
		default:
			System.out.println("您的数据库类型不正确或不支持！");
		}
		
		
		if(al.size()>0){
			if (Property.mail_sendYesOrNot.equalsIgnoreCase("y")) {
				SendMail sm = new SendMail();
				sm.Send(al);
			}
		}

	}
}
