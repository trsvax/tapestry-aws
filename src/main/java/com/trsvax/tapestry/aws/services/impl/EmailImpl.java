package com.trsvax.tapestry.aws.services.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.trsvax.tapestry.aws.services.Email;

public class EmailImpl implements Email {
	private final AmazonSimpleEmailService client;
	
	public EmailImpl(AmazonSimpleEmailService client) {
		this.client = client;
	}

	public void sendPage(String url, String subject, String plainText,
			Collection<String> toAddresses, Collection<String> ccAddresses, Collection<String> bccAddresses) {
		SendEmailRequest request = new SendEmailRequest().withSource("judy@judypaul.com");
		Body body = new Body().withHtml(new Content(getHTML(url))).withText( new Content(plainText));
		
		Message message = new Message(new Content(subject),body);
		request.setMessage(message);
		Destination destination = new Destination();
		destination.setToAddresses(toAddresses);
		destination.setCcAddresses(ccAddresses);
		destination.setBccAddresses(bccAddresses);
		request.setDestination(destination);
		client.sendEmail(request);
		
		
	}
	
	private String getHTML(String url) {
		try {
			InputStream input = new URL(url).openStream();
			return IOUtils.toString(input);
		} catch (Exception e) {
		}
		return "";

	}

	
}
