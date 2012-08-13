package com.trsvax.tapestry.aws.services;

import java.util.Collection;

public interface Email {

	public void sendPage(String url, String subject, String plainText,
			Collection<String> toAddresses, Collection<String> ccAddresses, Collection<String> bccAddresses);
}
