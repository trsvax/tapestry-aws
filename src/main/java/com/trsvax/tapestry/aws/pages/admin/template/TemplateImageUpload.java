package com.trsvax.tapestry.aws.pages.admin.template;

import java.io.File;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class TemplateImageUpload {
	@PageActivationContext
	private String templateKey;
	
	@Property
    private UploadedFile file;
	
	@Inject
	private AmazonS3 client;
	
	@Inject
	private Logger logger;
	
	@Inject
	private PageRenderLinkSource source;
	
	private final String prefix = "http://assets.judypaul.com.s3.amazonaws.com/";
	private final String bucket = "assets.judypaul.com";

    public Object onSuccess()
    {
    	String path = templateKey.substring(0, templateKey.lastIndexOf("/"));
    	logger.info("path {} {}",templateKey,path);
    	
    	ObjectMetadata metadata = new ObjectMetadata();
    	metadata.setContentType(file.getContentType());
    	metadata.setContentLength(file.getSize());
    	String key = path + "/" + file.getFileName();
		client.putObject(bucket,key,file.getStream(), metadata);
		client.setObjectAcl(bucket, key, CannedAccessControlList.PublicRead);
		return source.createPageRenderLinkWithContext(TemplateEdit.class, templateKey);

    }

}
