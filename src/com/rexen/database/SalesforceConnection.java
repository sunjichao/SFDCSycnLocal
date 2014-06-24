package com.rexen.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.rexen.configs.LogWrite;
import com.rexen.configs.Property;
import com.rexen.configs.csvTrans;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.partner.Error;

/***
 * 
 * @author Sam Rexen Technology
 * 
 */
public class SalesforceConnection { // use Partner WSDL Application

	static PartnerConnection connection;
	LogWrite lw = new LogWrite();

	private PartnerConnection getConnection() {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(Property.salesforce_username);
		config.setPassword(Property.salesforce_password);
		// config.setTraceMessage(true);

		try {
			connection = Connector.newConnection(config);
			// display some current settings
			System.out.println("Auth EndPoint: " + config.getAuthEndpoint());
			System.out.println("Service EndPoint: "
					+ config.getServiceEndpoint());
			System.out.println("Username: " + config.getUsername());
			System.out.println("SessionId: " + config.getSessionId());

		} catch (ConnectionException e1) {
			System.out.println("账号密码不正确 / 未连接到网络 / 未知错误");
			// e1.printStackTrace();
		}
		return connection;
	}

	public ArrayList<HashMap<Integer, String>> queryIngoreNull(String sql,
			String[] array) {
		getConnection();
		System.out.println("Querying Starting...");
		ArrayList<HashMap<Integer, String>> list = new ArrayList<HashMap<Integer, String>>();
		try {
			QueryResult queryResults = connection.query(sql);
			if (queryResults.getSize() > 0) {
				for (SObject s : queryResults.getRecords()) {
					HashMap<Integer, String> m = new HashMap<Integer, String>();
					// ls.add(s.getId());
					for (int i = 0; i < array.length; i++) {
						if (s.getField(array[i]) != null) { // 在Salesforce里值不为空再保存
							m.put(i, s.getField(array[i]).toString());
						}
					}
					list.add(m);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean ifExistRecord(String Object, String value) {
		boolean flag = false;
		String sql = "select id from " + Object + " where id = '" + value + "'";
		QueryResult queryResults;
		try {
			queryResults = connection.query(sql);
			if (queryResults.getSize() > 0) {
				flag = true;
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	public void PrintArrayList(ArrayList<HashMap<Integer, String>> al) {
		if (al.size() > 0) {
			for (int i = 0; i < al.size(); i++) {
				HashMap<Integer, String> hm = al.get(i);
				for (Integer key : hm.keySet()) {
					System.out.println("key: " + key + "     value: "
							+ hm.get(key));
				}

			}
		} else {
			System.out.println("您要打印的链表为0");
		}

	}

	public int insert(ArrayList<HashMap<Integer, String>> al, String csvpath, String logFilepath) {
		String writelog = null;
		int sum = 0;
		csvTrans ct = new csvTrans(csvpath);
		SObject[] records = new SObject[al.size()];
		String[] logs = new String[al.size()];
		String log = "";
		if (al.size() > 0) {
			for (int i = 0; i < al.size(); i++) {
				HashMap<Integer, String> hm = al.get(i);
				SObject so = new SObject();
				so.setType(ct.salesforceSchema());
				for (Integer key : hm.keySet()) {
					so.setField(ct.getString(key + 1, 0), hm.get(key));
                    log = log+ hm.get(key) + "      ";
				}
				logs[i] = log;
				log = "";
				records[i] = so;
			}
			
			try {
				SaveResult[] saveResults = connection.create(records);
				for (int i1 = 0; i1 < saveResults.length; i1++) {
					if (saveResults[i1].isSuccess()) {
						writelog = logs[i1] + "    " + " Insert Success";
						sum++;
					} else {
						writelog = logs[i1] + "    " + "Insert Error";				
					}
					lw.Writelog(writelog, logFilepath);
				}
			} catch (ConnectionException e) {
				e.printStackTrace();
			}

		}
		return sum;

	}

	public int update(ArrayList<HashMap<Integer, String>> al, String csvpath, String logFilepath) {
		String writelog = null;
		int sum = 0;
		
		csvTrans ct = new csvTrans(csvpath);
		int salesforceidRow = ct.getSalesforceRow(ct.csvTransArray(0), "id");
		SObject[] records = new SObject[al.size()];
		String[] logs = new String[al.size()];
		String log = "";
		if (al.size() > 0) {
			for (int i = 0; i < al.size(); i++) {
				HashMap<Integer, String> hm = al.get(i);
				SObject so = new SObject();
				so.setType(ct.salesforceSchema());
				so.setId(hm.get(salesforceidRow));
				for (Integer key : hm.keySet()) {
					if (ct.getString(key + 1, 0).equalsIgnoreCase("isdeleted")
							|| ct.getString(key + 1, 0).equalsIgnoreCase(
									"CreatedById")) {

					} else {
						so.setField(ct.getString(key + 1, 0), hm.get(key));	
						log = log + hm.get(key) + "      ";
					}	
					records[i] = so;
				}
				logs[i] = log;
				log = "";
			}

			try {
				SaveResult[] saveResults = connection.update(records);
				for (int i1 = 0; i1 < saveResults.length; i1++) {
					if (saveResults[i1].isSuccess()) {
						writelog = logs[i1] + "    " + " Update Success";
						sum++;
					} else {
						Error[] errors = saveResults[i1].getErrors();
						for (int j = 0; j < errors.length; j++) {
						writelog = logs[i1] + "    " + "Insert Error";
							System.out.println("ERROR Update record: "
									+ errors[j].getMessage());
						}
					}
					lw.Writelog(writelog, logFilepath);
				}
			} catch (ConnectionException e) {
				e.printStackTrace();
			}

		}
		return sum;

	}

	public String LocalInsertSalesforce(String csvpath, String database) {
		String writelog = null;
		int insertsum = 0;
		int updatesum = 0;
		csvTrans ct = new csvTrans(csvpath);
		int salesforceidRow = ct.getSalesforceRow(ct.csvTransArray(0), "id");

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String fileName = df.format(new Date()) + "_MysqlToSalesforce" + "_"
				+ ct.databaseSchema() + "_Sync" + ".txt";
		String logFilepath = Property.log_filepath + fileName; // log文件命名规则
																// 路径+时间+Target+Object+CRUD.txt

		getConnection();
		String searchsql = "SELECT " + ct.csvTransString(1) + " FROM " + ct.databaseSchema() ;

		ArrayList<HashMap<Integer, String>> al = null;

		if (database.trim().equalsIgnoreCase("mysql")) {
			MysqlConnection mct = new MysqlConnection();
			al = mct.queryMysqlIngoreNull(searchsql, ct.csvTransArray(1));
			System.out.println("Mysql 向 Salesforce 插入/更新 数据开始执行！");
		} else if (database.trim().equalsIgnoreCase("oracle")) {
			OracleConnection oct = new OracleConnection();
			al = oct.queryMysqlIngoreNull(searchsql, ct.csvTransArray(1));
			System.out.println("Oracle 向 Salesforce 插入/更新 数据开始执行！");
		}

		lw.Writelog("Database：  " + Property.db_type + "   Host："
				+ Property.db_mysql_host + "   Schema:"
				+ Property.db_mysql_schema + "   OS:" + Property.os,
				logFilepath);
		ArrayList<HashMap<Integer, String>> al1 = new ArrayList<HashMap<Integer, String>>();
		ArrayList<HashMap<Integer, String>> al2 = new ArrayList<HashMap<Integer, String>>();
		for (int i = 0; i < al.size(); i++) {
			HashMap<Integer, String> hm = al.get(i);
			if (hm.get(salesforceidRow) == null) {
				al1.add(hm); // 插入
			} else {
				al2.add(hm); // 更新
			}
		}
		insertsum = insert(al1, csvpath, logFilepath);
		updatesum = update(al2, csvpath, logFilepath);

		writelog = "共 " + al.size() + " 条数据，成功 " + insertsum + " 条！" + " 更新成功 "
				+ updatesum + " 条！";
		lw.Writelog(writelog, logFilepath);
		System.out.println("共 " + al.size() + " 条数据，成功 " + insertsum + " 条！"
				+ " 更新成功 " + updatesum + " 条！" + " 查看日志获得详细情况");
		return logFilepath;
	}

	@SuppressWarnings("unused")
	private void delete(String searchsql) { // 注意 查询语句不要Select * from ****
		getConnection();
		System.out.println("Deleting Starting...");
		String[] ids = null;
		try {
			QueryResult queryResults = connection.query(searchsql);
			if (queryResults.getSize() > 0) {
				ids = new String[queryResults.getSize()];
				for (int i = 0; i < queryResults.getRecords().length; i++) {
					SObject so = (SObject) queryResults.getRecords()[i];
					ids[i] = so.getId();
				}
			}

			DeleteResult[] deleteResults = connection.delete(ids);

			for (int i = 0; i < deleteResults.length; i++) {
				if (deleteResults[i].isSuccess()) {
					// System.out.println(i+". Successfully deleted record - Id: "
					// + deleteResults[i].getId());
				} else {
					Error[] errors = deleteResults[i].getErrors();
					for (int j = 0; j < errors.length; j++) {
						System.out.println("ERROR deleting record: "
								+ errors[j].getMessage());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Deleting End");
	}

}
