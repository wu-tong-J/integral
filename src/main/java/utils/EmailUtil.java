package utils;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @author
 * @description 发送邮件工具类
 * @date 2017-11-13 17:16
 **/
public class EmailUtil {
	private static String host;
	private static String port;
	private static String username;
	private static String password;
	private static String nickname;
	private static String auth;
	private static String timeout;

	/**
	 * @Description:发送纯文本邮件
	 **/
	public static void sendSimpleEmail(String sendName, String sendPwd, String receiveName, String title, String content) throws Exception {
		Properties properties = authSSL();
		//1、创建session
		Session session = Session.getInstance(properties);
		//开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		//2、通过session得到transport对象
		Transport ts = session.getTransport();
		MimeMessage message = createSimpleMessage(sendName, sendPwd, receiveName, title, content, session);
		//5、发送邮件
		if (sendName == null || "".equals(sendName) || sendPwd == null || "".equals(sendPwd)){
			ts.connect(host, username, password);
		}else {
			ts.connect(host, sendName, sendPwd);
		}
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}

	/**
	 * @Description:发送包含内嵌图片的文本邮件
	 **/
	public static void sendImageMail(String sendName, String sendPwd, String receiveName, String title, String content, String picPath) throws Exception {
		Properties properties = authSSL();
		//1、创建session
		Session session = Session.getInstance(properties);
		//开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		//2、通过session得到transport对象
		Transport ts = session.getTransport();
		MimeMessage message = createImageMessage(sendName, sendPwd, receiveName, title, content, picPath, session);
		//5、发送邮件
		if (sendName == null || "".equals(sendName) || sendPwd == null || "".equals(sendPwd)){
			ts.connect(host, username, password);
		}else {
			ts.connect(host, sendName, sendPwd);
		}
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}

	private static MimeMessage createImageMessage(String sendName, String sendPwd, String receiveName, String title, String content, String picPath, Session session) throws Exception{
		//创建邮件
		MimeMessage message = new MimeMessage(session);
		//设置邮件的基本信息
		if (sendName == null || "".equals(sendName) || sendPwd == null || "".equals(sendPwd)){
			//指明邮件的发件人
			message.setFrom(new InternetAddress(username));
		}else {
			//指明邮件的发件人
			message.setFrom(new InternetAddress(sendName));
		}
		//收件人
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveName));
		//邮件标题
		message.setSubject(title);
		// 准备邮件正文数据
		MimeBodyPart text = new MimeBodyPart();
		//"这是一封邮件正文带图片<img src='cid:xxx.jpg'>的邮件", "text/html;charset=UTF-8"
		text.setContent(content,"text/html;charset=UTF-8");
		// 准备图片数据
		MimeBodyPart image = new MimeBodyPart();
		DataHandler dh = new DataHandler(new FileDataSource(picPath));
		image.setDataHandler(dh);
		String picName = picPath.substring(picPath.lastIndexOf(File.separator) + 1, picPath.length());
		image.setContentID(picName);
		// 描述数据关系
		MimeMultipart mm = new MimeMultipart();
		mm.addBodyPart(text);
		mm.addBodyPart(image);
		mm.setSubType("related");

		message.setContent(mm);
		message.saveChanges();
		//将创建好的邮件写入到E盘以文件的形式进行保存
		//String localFilePath = request.getServletContext().getRealPath("WEB-INF" + File.separator + "temp"/*+File.separator+ftpFileName*/);
		message.writeTo(new FileOutputStream("E:\\ImageMail.eml"));
		//返回创建好的邮件
		return message;
	}

	private static MimeMessage createSimpleMessage(String sendName, String sendPwd, String receiveName, String title, String content, Session session) throws MessagingException {
		//4、创建邮件
		MimeMessage message = null;
		//3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		if (sendName == null || "".equals(sendName) || sendPwd == null || "".equals(sendPwd)){
			//创建邮件对象
			message = new MimeMessage(session);
			//指明邮件的发件人
			message.setFrom(new InternetAddress(username));
		}else {
			//创建邮件对象
			message = new MimeMessage(session);
			//指明邮件的发件人
			message.setFrom(new InternetAddress(sendName));
		}
		//指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveName));
		//邮件的标题
		message.setSubject(title);
		//邮件的文本内容
		message.setContent(content, "text/html;charset=UTF-8");
		return message;
	}

	private static Properties authSSL(){
		Properties prop = new Properties();
		prop.setProperty("mail.host", host);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.port", port);
		prop.setProperty("mail.smtp.auth", "true".equals(auth)?"true":"false");
		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		sf.setTrustAllHosts(true);
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.socketFactory", sf);
		prop.put("mail.smtp.socketFactory.fallback", "false");
		prop.put("mail.smtp.socketFactory.port",  port);
		return prop;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		EmailUtil.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		EmailUtil.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		EmailUtil.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		EmailUtil.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		EmailUtil.nickname = nickname;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		EmailUtil.auth = auth;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		EmailUtil.timeout = timeout;
	}
}
