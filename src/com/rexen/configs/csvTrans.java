package com.rexen.configs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 获取Mapping的csv文件 包含一些操作方法
 * @author Sam Rexen Technology
 * 
 */
public class csvTrans {
	private BufferedReader br = null;
	private List<String> list = new ArrayList<String>();

	public csvTrans(String fileName) {
		try {
			br = new BufferedReader(new FileReader(fileName));
			String stemp;
			while ((stemp = br.readLine()) != null) {
				list.add(stemp);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("没找到文件！config.properties 的SalesforceToLocal 和 LocalToSalesforce 对应 /csv 下的文件，检查您的对应关系！");
		} catch (IOException ex) {
			System.out.println("读文件出错！");
		} 
		try {
			br.close();
		} catch (Exception e) {
			System.out.println("关闭读写流失败！");
		}
		
	}

	/**
	 * 获取行数
	 */
	public int getRowNum() {
		return list.size();
	}

	/**
	 * 获取列数
	 */
	public int getColNum() {
		if (!list.toString().equals("[]")) {
			if (list.get(0).toString().contains(",")) {// csv为逗号分隔文件
				return list.get(0).toString().split(",").length;
			} else if (list.get(0).toString().trim().length() != 0) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 获取制定行
	 * @param index
	 * @return
	 */
	public String getRow(int index) {
		if (this.list.size() != 0) {
			return (String) list.get(index);
		} else {
			return null;
		}
	}

	/**
	 * 获取指定列
	 * @param index
	 * @return
	 */
	public String getCol(int index) {
		if (this.getColNum() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		String tmp = null;
		int colnum = this.getColNum();
		if (colnum > 1) {
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				tmp = it.next().toString();
				sb = sb.append(tmp.split(",")[index] + ",");
			}
		} else {
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				tmp = it.next().toString();
				sb = sb.append(tmp + ",");
			}
		}
		String str = new String(sb.toString());
		str = str.substring(0, str.length() - 1);
		return str;
	}

	/**
	 * 获取某个单元格
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public String getString(int row, int col) {
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1) {
			temp = list.get(row).toString().split(",")[col];
		} else if (colnum == 1) {
			temp = list.get(row).toString();
		} else {
			temp = null;
		}
		return temp;
	}
	
     /**
     * 获取Salesforce Schema
     * @return
     */
	public String salesforceSchema() {
		return this.getString(0, 0);
	}
	
	 /**
     * 获取local database Schema
     * @return
     */
	public String databaseSchema() {
		return this.getString(0, 1);
	}
	
	public String[] csvTransArray(int index) {  // 得到field 数组
		int rowNum = this.getRowNum();
		String[] str=new String[rowNum-1];
		for (int i = 1; i < rowNum; i++) {			
		      str[i-1]=this.getString(i,index);	
		}
		return str;
	}

	public String csvTransString(int index) { // 得到field 字符串
		String strdd = this.getCol(index);
		return strdd.substring(strdd.indexOf(",") + 1, strdd.length());
	}
	
	public int getSalesforceRow(String[] str, String s) {   //根据数组和字符串得到索引 没有则返回-1
		int x = -1;
		for (int i = 0; i < str.length; i++) {
			if (str[i].trim().equalsIgnoreCase(s)) {
				x = i;
			}
		}
		return x;
	}

}
