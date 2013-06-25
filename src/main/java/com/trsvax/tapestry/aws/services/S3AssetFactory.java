package com.trsvax.tapestry.aws.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.Asset2;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.AssetFactory;
import org.slf4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.trsvax.tapestry.aws.URLChangedListener;

public class S3AssetFactory  implements AssetFactory {
	private final AmazonS3 client;
	private final Logger logger;
	private final URLChangedEventHub hub;
	
	public static Resource root = null;
	
	public S3AssetFactory(Logger logger,AmazonS3 client, URLChangedEventHub hub) {
		this.client = client;
		this.logger = logger;
		this.hub = hub;
		root = new S3Resource("");
	}

	public Resource getRootResource() {
		return root;
	}

	public Asset createAsset(Resource resource) {
		S3Asset asset = new S3Asset(resource);
		hub.addURLChangedtListener(asset);
		return asset;
	}
	
	public Asset createAsset(String path) {
		return createAsset( new S3Resource(path));
	}
	
	
	
	public class S3Asset implements Asset2, URLChangedListener {
		private Resource resource;
		
		public S3Asset(Resource resource) {
			this.resource = resource;
		}

		public String toClientURL() {
			return resource.toURL().toString();
		}

		public Resource getResource() {
			logger.info("getResource {}",resource.getPath());
			return resource;
		}

		public boolean isInvariant() {
			return true;
		}

		public void URLDidChange(String url) {
			if ( toClientURL().equals(url)) {
				logger.info("changed {}",url);
				resource = new S3Resource(resource.getPath());	
			}
		}
		
	}
		

	public class S3Resource implements Resource {
		private final String path;
		private final URL url;
		private final byte[] data;
		private final Boolean exists;
		private final String etag;
		private final String prefix = "http://assets.judypaul.com.s3.amazonaws.com/";
		private final String bucket = "assets.judypaul.com";
		
		public S3Resource(String path) {
			this.path = path;
			try {
				url = new URL(prefix + path);
				if ( path.endsWith(".html") || path.endsWith(".tml")) {
					logger.info("fetch {}",path);
					S3Object o = client.getObject(bucket, path);
					if ( o != null ) {
						etag = o.getObjectMetadata().getETag();
						InputStream is = o.getObjectContent();
						data = IOUtils.toByteArray(is);
						is.close();	
						exists = true;
					} else {
						data = null;
						exists = false;
						etag = path;
					}
				} else {
					exists = path.length() != 0 && ! path.endsWith("/");
					data = null;
					etag = path;
				}
			} catch (Exception e) {
				throw new RuntimeException(path + " " + e.getMessage());
			} 
		}
		

		public boolean exists() {
			return  exists;
		}

		public InputStream openStream() throws IOException {
			if ( data != null ) {
				return new ByteArrayInputStream(data);
			}
			return client.getObject("assets.judypaul.com", path).getObjectContent();
		}

		public URL toURL() {
			return url;
		}

		public Resource forLocale(Locale locale) {
			return this;
		}

		public Resource forFile(String relativePath) {
			 StringBuilder builder = new StringBuilder(getFolder());

		        for (String term : relativePath.split("/"))
		        {
		            // This will occur if the relative path contains sequential slashes

		            if (term.equals(""))
		                continue;

		            if (term.equals("."))
		                continue;

		            if (term.equals(".."))
		            {
		                int slashx = builder.lastIndexOf("/");

		                // TODO: slashx < 0 (i.e., no slash)

		                // Trim path to content before the slash

		                builder.setLength(slashx);
		                continue;
		            }

		            // TODO: term blank or otherwise invalid?
		            // TODO: final term should not be "." or "..", or for that matter, the
		            // name of a folder, since a Resource should be a file within
		            // a folder.

		            if (builder.length() > 0)
		                builder.append("/");

		            builder.append(term);
		        }

		        return createResource(builder.toString());
		}

		public Resource withExtension(String extension) {
			int dotx = path.lastIndexOf('.');

	        if (dotx < 0)
	            return createResource(path + "." + extension);

	        return createResource(path.substring(0, dotx + 1) + extension);
		}

		public String getFolder() {
	        int slashx = path.lastIndexOf('/');
	        return (slashx < 0) ? "" : path.substring(0, slashx);
		}

		public String getFile() {
	        int slashx = path.lastIndexOf('/');
	        return path.substring(slashx + 1);
		}

		public String getPath() {
			return path;
		}
		
		
		
		
		 private Resource createResource(String path)
		    {
		        if (this.path.equals(path))
		            return this;

		        return new S3Resource(path);
		    }


		@Override
		public boolean equals(Object other) {
			if ( this == other ) return true;
			if ( S3AssetFactory.S3Resource.class.isAssignableFrom(other.getClass())) {
				if ( etag.equals( ((S3AssetFactory.S3Resource)other).etag )) {
					return true;
				}
			}
			return false;
		}


		@Override
		public int hashCode() {
			return etag.hashCode();
		}


		@Override
		public String toString() {
			return String.format("S3Resource %s",path);
		}
		
		
	}
}
