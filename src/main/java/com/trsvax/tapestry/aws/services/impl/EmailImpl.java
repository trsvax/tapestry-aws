package com.trsvax.tapestry.aws.services.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.trsvax.tapestry.aws.AWSConstants;
import com.trsvax.tapestry.aws.services.Email;

public class EmailImpl implements Email {
	private final AmazonSimpleEmailService client;
	private final String source;
	private final Logger logger;
	private Request request;
	
	public EmailImpl(AmazonSimpleEmailService client, @Symbol(AWSConstants.EMAIL_SOURCE) String source, Logger logger, Request request) {
		this.client = client;
		this.source = source;
		this.logger = logger;
		this.request = request;
	}

	public void sendPage(String url, String subject, String plainText,
			Collection<String> toAddresses, Collection<String> ccAddresses, Collection<String> bccAddresses) {
		SendEmailRequest request = new SendEmailRequest().withSource(source);
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
			return IOUtils.toString(new URL(url).openStream());
		} catch (Exception e) {
			logger.error("Email get HTML {}",e.getMessage());
		}
		return "";
	}	
	
	public boolean isThisMyIpAddress() {
	    try {
	    	String addr = request.getRemoteHost();
			InetAddress inetAddress = InetAddress.getByName(addr);
			logger.info("my ip {} {}",addr,inetAddress.isAnyLocalAddress());
		    // Check if the address is a valid special local or loop back
		    if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress())
		        return true;
	
		    // Check if the address is defined on any interface
	        return NetworkInterface.getByInetAddress(inetAddress) != null;
	    } catch (Exception e) {
	        return false;
	    }
	}
}
