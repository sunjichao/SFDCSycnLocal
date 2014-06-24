package com.rexen.mails;

import java.util.ArrayList;

import com.rexen.configs.Property;
import com.rexen.mails.MailSenderInfo;
import com.rexen.mails.SimpleMailSender;

/**
 * @author Sam Rexen Technology
 *
 */
public class SendMail {
	public void Send(ArrayList<String> al){
		 MailSenderInfo mailInfo = new MailSenderInfo();    
	     mailInfo.setMailServerHost(Property.mail_setMailServerHost);    
	     mailInfo.setMailServerPort("25");    
	     mailInfo.setValidate(true);    
	     mailInfo.setUserName(Property.mail_setUserName);    
	     mailInfo.setPassword(Property.mail_setPassword);//您的邮箱密码    
	     mailInfo.setFromAddress(Property.mail_setUserName);
	     
	     mailInfo.setToAddress(Property.mail_setToAddress);    
	     mailInfo.setSubject("Integration log ");    
	     mailInfo.setContent("请查看日志内容 ");    
	        //这个类主要来发送邮件   
	     SimpleMailSender sms = new SimpleMailSender(); 
	     sms.sendTextMail(mailInfo, al);   
	        System.out.println("邮件发送成功！");
		
	}

}

