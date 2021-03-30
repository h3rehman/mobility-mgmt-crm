package services.calendar;

import org.springframework.stereotype.Service;

import config.MailConfig;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import javax.activation.DataHandler;
import javax.mail.Message;

import java.time.format.DateTimeFormatter;

@Service
public class CalendarService {
	
	private JavaMailSender mailSender;
	
	@Autowired
	MailConfig mailConfig;
	
	  @Autowired
	  public CalendarService(JavaMailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	  public void sendCalendarInvite(String fromEmail, CalendarRequest calendarRequest) throws Exception {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        mimeMessage.addHeaderLine("method=REQUEST");
	        mimeMessage.addHeaderLine("charset=UTF-8");
	        mimeMessage.addHeaderLine("component=VEVENT");
	        mimeMessage.setFrom(new InternetAddress(fromEmail));
	        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(calendarRequest.getToEmail()));
	        mimeMessage.setSubject(calendarRequest.getSubject());
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
	        StringBuilder builder = new StringBuilder();
	        builder.append("BEGIN:VCALENDAR\n" +
	                "METHOD:REQUEST\n" +
	                "PRODID:" + mailConfig.getExchangeServerVersion() +"\n" +
	                "VERSION:2.0\n" +
	                "BEGIN:VTIMEZONE\n" +
	                "TZID:America/Chicago\n" +
	                "END:VTIMEZONE\n" +
	                "BEGIN:VEVENT\n" +
	                "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:" + calendarRequest.getToEmail() + "\n" +
	                "ORGANIZER;CN=" + mailConfig.getFromEmailName() + ":MAILTO:" + fromEmail + "\n" +
	                "DESCRIPTION;LANGUAGE=en-US:" + calendarRequest.getBody() + "\n" +
	                "UID:"+calendarRequest.getUid()+"\n" +
	                "SUMMARY;LANGUAGE=en-US:" + calendarRequest.getSubject() + "\n" +
	                "DTSTART:" + formatter.format(calendarRequest.getMeetingStartTime()).replace(" ", "T") + "\n" +
	                "DTEND:" + formatter.format(calendarRequest.getMeetingEndTime()).replace(" ", "T") + "\n" +
	                "CLASS:PUBLIC\n" +
	                "PRIORITY:5\n" +
	                "DTSTAMP:"+ LocalDateTime.now().toString() + "\n" +
	                "TRANSP:OPAQUE\n" +
	                "STATUS:CONFIRMED\n" +
	                "SEQUENCE:$sequenceNumber\n" +
	                "LOCATION;LANGUAGE=en-US:" + calendarRequest.getLocation()  + "\n" +
	                "BEGIN:VALARM\n" +
	                "DESCRIPTION:REMINDER\n" +
	                "TRIGGER;RELATED=START:-PT15M\n" +
	                "ACTION:DISPLAY\n" +
	                "END:VALARM\n" +
	                "END:VEVENT\n" +
	                "END:VCALENDAR");
	 
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	 
	        messageBodyPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
	        messageBodyPart.setHeader("Content-ID", "calendar_message");
	        messageBodyPart.setDataHandler(new DataHandler(
	                new ByteArrayDataSource(builder.toString(), "text/calendar;method=REQUEST;name=\"invite.ics\"")));
	 
	        MimeMultipart multipart = new MimeMultipart();
	 
	        multipart.addBodyPart(messageBodyPart);
	 
	        mimeMessage.setContent(multipart);
	 
	        System.out.println(builder.toString());
	 
	        mailSender.send(mimeMessage);
	        System.out.println("#######--------Calendar invite sent--------######### on " + LocalDateTime.now().toString());
	 
	    }
	
}
