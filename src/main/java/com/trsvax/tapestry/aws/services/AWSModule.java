package com.trsvax.tapestry.aws.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.ApplicationStatePersistenceStrategy;
import org.apache.tapestry5.services.AssetFactory;
import org.apache.tapestry5.services.LibraryMapping;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.trsvax.tapestry.aws.AWSConstants;
import com.trsvax.tapestry.aws.internal.DynamoDBApplicationStatePersistenceStrategy;
import com.trsvax.tapestry.aws.services.impl.EmailImpl;
import com.trsvax.tapestry.aws.services.impl.KeyImpl;

public class AWSModule {
    public static void bind(ServiceBinder binder) {
    	binder.bind(Email.class,EmailImpl.class);
		binder.bind(AssetFactory.class,S3AssetFactory.class).withId("S3AssetFactory");
		binder.bind(URLChangedEventHub.class,URLChangedEventHubImpl.class);
		binder.bind(Key.class,KeyImpl.class);
		
		binder.bind(DynamoDBMapperConfig.class, new ServiceBuilder<DynamoDBMapperConfig>() {

			public DynamoDBMapperConfig buildService(ServiceResources resources) {
				return DynamoDBMapperConfig.DEFAULT;
			}
		});

        binder.bind(DynamoDBMapper.class, new ServiceBuilder<DynamoDBMapper>() {

			public DynamoDBMapper buildService(ServiceResources resources) {
				return resources.autobuild("Mapper", DynamoDBMapper.class);

			}
		});
    }
    
    public void contributeAssetSource(MappedConfiguration<String, AssetFactory> configuration, 
			@Local AssetFactory s3AssetFactory) {
        configuration.add("s3", s3AssetFactory);
    }
    
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("aws", "com.trsvax.tapestry.aws"));
	}
	
	 public void contributeApplicationStatePersistenceStrategySource(MappedConfiguration<String, ApplicationStatePersistenceStrategy> configuration) {
			          configuration.addInstance(AWSConstants.DYNAMOENTITY, DynamoDBApplicationStatePersistenceStrategy.class);
	 }
	 
	 

}
