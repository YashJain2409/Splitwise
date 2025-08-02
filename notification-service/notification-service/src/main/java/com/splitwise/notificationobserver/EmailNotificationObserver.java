package com.splitwise.notificationobserver;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.splitwise.enums.NotificationChannel;
import com.splitwise.events.ExpenseCreatedEvent;
import com.splitwise.events.NotificationEvent;
import com.splitwise.intfc.NotificationObserver;
import com.splitwise.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailNotificationObserver implements NotificationObserver {

	final JavaMailSender mailSender;
	
	@Value("${notification.email.from}")
	String from;


	@Override
	public void notify(NotificationEvent notificationEvent)  {
		String subject = "";
		String body = "";

		switch (notificationEvent.getEventType()) {
		case EXPENSE_CREATED -> {
			if (notificationEvent instanceof ExpenseCreatedEvent expenseEvent) {
				subject = "New Expense Added";
				body = buildExpenseCreatedEmail(expenseEvent);
			}
		}
		}

		sendEmail(notificationEvent.getRecipient().getEmail(), subject, body);
	}

	private void sendEmail(String to, String subject, String body) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from); 
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body,true);
			mailSender.send(message);
		} catch (MessagingException e) {
			System.out.println("error occured in sending mail");
		}
		
	}

	private String buildExpenseCreatedEmail(ExpenseCreatedEvent notificationEvent)  {
		String html = "";
		try {
			ClassPathResource resource = new ClassPathResource("templates/expense-notification.html");
			html = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		html = html.replace("{{userName}}", notificationEvent.getRecipient().getName());
		html = html.replace("{{actorName}}", notificationEvent.getCreatedUserName());
		html = html.replace("{{expenseTitle}}", notificationEvent.getExpenseName());
		html = html.replace("{{total}}", notificationEvent.getExpenseAmount().toString());
		BigDecimal amount = notificationEvent.getOwedAmount();
		String owedLabel = "";
		String owedClass = "";
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			owedLabel = "You owe: ₹ " + amount.abs().toPlainString();
			owedClass = "negative";
		} else {
			owedLabel = "You get: ₹ " + amount.abs().toPlainString();
			owedClass = "positive";
		}
		html = html.replace("{{owed}}", owedLabel);
		html = html.replace("owedClass", owedClass);
		return html;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.EMAIL;
	}

}
