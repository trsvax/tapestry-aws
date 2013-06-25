package com.trsvax.tapestry.aws.pages.admin.template;

import java.util.List;

import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class TemplateIndex {
	
	@Property
	List<S3ObjectSummary> templates;
	
	
	@Inject
	private AmazonS3 client;
	
	String prefix = "http://assets.judypaul.com.s3.amazonaws.com/";
	String bucket = "assets.judypaul.com";
	
	@BeginRender
	void beginRender() {
		ObjectListing listing = client.listObjects(bucket, "template/");
		templates = listing.getObjectSummaries();
	}

}
