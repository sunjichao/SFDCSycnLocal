package com.rexen.configs;

/**
 * 获取config.properties基本信息
 * @author Sam Rexen Technology
 *
 */
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

public class Property {
 
 //根据key读取value
 final public static String configfilePath=System.getProperty("user.dir") +"/config.properties";
 final public static String csvPath = System.getProperty("user.dir")+"/csv";

 final public static String os=readValue(configfilePath,"os").trim();
 final public static String log_filepath=readValue(configfilePath,"log.filepath").trim();

 final public static String db_type=readValue(configfilePath,"db.type").trim();
 final public static String db_mysql_username=readValue(configfilePath,"db.username").trim();
 final public static String db_mysql_password=readValue(configfilePath,"db.password").trim();
 final public static String db_mysql_host=readValue(configfilePath,"db.host").trim();
 final public static String db_mysql_schema=readValue(configfilePath,"db.schema").trim();
 final public static String db_mysql_port=readValue(configfilePath,"db.port").trim();
 
 final public static String mail_sendYesOrNot=readValue(configfilePath,"mail.sendYesOrNot").trim();
 final public static String mail_setMailServerHost=readValue(configfilePath,"mail.setMailServerHost").trim();
 final public static String mail_setUserName=readValue(configfilePath,"mail.setUserName").trim();
 final public static String mail_setPassword=readValue(configfilePath,"mail.setPassword").trim();
 final public static String mail_setToAddress=readValue(configfilePath,"mail.setToAddress").trim();
 
 final public static String salesforce_username=readValue(configfilePath,"salesforce.username").trim();
 final public static String salesforce_password=readValue(configfilePath,"salesforce.password").trim();
 
 final public static String[] SalesforceToLocal=readValue(configfilePath,"SalesforceToLocal").trim().split(";");
 final public static String[] LocalToSalesforce=readValue(configfilePath,"LocalToSalesforce").trim().split(";");

 
 public static String readValue(String filePath,String key) {
  Properties props = new Properties();
        try {
         InputStream in = new BufferedInputStream (new FileInputStream(filePath));
         props.load(in);
         String value = props.getProperty (key); 
            return value;
        } catch (Exception e) {
         e.printStackTrace();
         return null;
        }
 }
 
 //读取properties的全部信息
    public void readProperties(String filePath) {
     Properties props = new Properties();
        try {
         InputStream in = new BufferedInputStream (new FileInputStream(filePath));
         props.load(in);
            Enumeration<?> en = props.propertyNames();
             while (en.hasMoreElements()) {
              String key = (String) en.nextElement();
                    String Property = props.getProperty (key);
                    if(Property.isEmpty()||Property.equals(" ")){
                    	Property="null";
                    	System.out.println();
                    	System.out.println("Danger！,Property Value is null！");
                    }
                    System.out.println(key+"  "+Property);
                }
        } catch (Exception e) {
         e.printStackTrace();
        }
    }
    
    public void writeProperties(String filePath,String parameterName,String parameterValue) {
        Properties prop = new Properties();
        try {
         InputStream fis = new FileInputStream(filePath);
               //从输入流中读取属性列表（键和元素对）
               prop.load(fis);
               //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
               //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
               OutputStream fos = new FileOutputStream(filePath);
               prop.setProperty(parameterName, parameterValue);
               //以适合使用 load 方法加载到 Properties 表中的格式，
               //将此 Properties 表中的属性列表（键和元素对）写入输出流
               prop.store(fos, "Update '" + parameterName + "' value");
           } catch (IOException e) {
            System.err.println("Visit "+filePath+" for updating "+parameterName+" value error");
           }
       }

}

