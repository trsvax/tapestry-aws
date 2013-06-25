package com.trsvax.tapestry.aws.pages.admin.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.DataType;
import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.slf4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.trsvax.tapestry.aws.services.URLChangedEventHub;

public class TemplateEdit {
	@PageActivationContext
	@Property
	@NonVisual
	private String key;
	
	@Property
	@DataType("html")
	private String template;
	
	@Property
	private List<String> images;
	@NonVisual
	@Property
	private String image;
	 
	@Inject
	private AmazonS3 client;
	
	@Inject
	private Logger logger;
	
	@Inject
	private URLChangedEventHub hub;
	
	@Inject
	private AssetSource source;
	
	private final String prefix = "http://assets.judypaul.com.s3.amazonaws.com/";
	private final String bucket = "assets.judypaul.com";

	
	@BeginRender
	void beginRender() throws IOException {
		Resource resource = source.resourceForPath("s3:" + key);
		if ( resource != null ) {
			template = IOUtils.toString(resource.openStream());
		}
    	String path = key.substring(0, key.lastIndexOf("/"));
    	List<S3ObjectSummary> summaries = client.listObjects(bucket, path).getObjectSummaries();
    	images = new ArrayList<String>();
    	for ( S3ObjectSummary summary : summaries ) {
    		if ( summary.getKey().endsWith("jpg")) {
    			images.add(summary.getKey());
    		}
    	}

	}
	
	void onSuccess() {
		String url = prefix + key;
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(template.getBytes().length);
		client.putObject(bucket, key, new ByteArrayInputStream(template.getBytes()), metadata);
		hub.fire(url);
	}

}
