package com.trsvax.tapestry.aws.pages.admin.template;

import java.io.ByteArrayInputStream;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.amazonaws.services.s3.AmazonS3;

public class TemplateNew {
	@Property
	private String key;
	
	@Inject
	private AmazonS3 client;
	
	String bucket = "assets.judypaul.com";

	
	Object onSuccess() {
		client.putObject(bucket, "template/" + key, new ByteArrayInputStream("<div class='container'>\n</div>".getBytes()), null);
		return TemplateIndex.class;
	}

}
