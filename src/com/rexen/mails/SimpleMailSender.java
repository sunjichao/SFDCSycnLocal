package com.rexen.mails;


/**
 * @author Sam Rexen Technology
 *
 */


import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SimpleMailSender {
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo,ArrayList<String> al) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 添加附件
			

			
//			mailMessage.setContent(addAttachment(filepath,filename));
//			System.out.println("filepath: "+filepath );
//			System.out.println("filename: "+filename);
//			
//			mailMessage.setContent(addAttachment("/Users/Sam/Desktop/","logo.gif"));

			
	
			mailMessage.setContent(addAttachments(al));
			
			
			
			
			
			
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (SendFailedException e) {
			System.out.println("邮件发送失败！" + "可能是邮件地址不正确！");
		} catch (MessagingException ex) {
			ex.printStackTrace();
			System.err.println("邮件发送失败！" + "未知原因");

		}
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
			System.out.println("邮件发送失败！");
		}
		return false;
	}

	public Multipart addAttachment(String filePath, String fileName) {
		String AttachmentName = fileName;
		Multipart multipart = new MimeMultipart();
		BodyPart contentPart = new MimeBodyPart();
		try {
			multipart.addBodyPart(contentPart);
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath + fileName);
			// 添加附件的内容
			messageBodyPart.setDataHandler(new DataHandler(source));

			messageBodyPart.setFileName(AttachmentName);

			multipart.addBodyPart(messageBodyPart);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return multipart;

	}
	
	public Multipart addAttachments(ArrayList<String> al) {
		Multipart multipart = new MimeMultipart();
		try {
			
			 for (int index = 0; index < al.size() ; index++) 
		        { 
		         MimeBodyPart mailArchieve = new MimeBodyPart(); 
		         FileDataSource fds = new FileDataSource(al.get(index)); 
		         
		         mailArchieve.setDataHandler(new DataHandler(fds)); 
		         mailArchieve.setFileName(fds.getName());
		         
		        // mailArchieve.setFileName(MimeUtility.encodeText(fds.getName(),"GBK","B"));      设置编码
		         multipart.addBodyPart(mailArchieve); 
		        }   
			 
			
		} catch (Exception e) {
			System.out.println("添加附件失败！");
			System.err.println(e);
		}
		return multipart;
	}
}
    
