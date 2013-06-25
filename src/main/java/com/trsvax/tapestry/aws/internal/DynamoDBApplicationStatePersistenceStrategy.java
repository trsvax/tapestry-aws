package com.trsvax.tapestry.aws.internal;

import org.apache.tapestry5.internal.services.SessionApplicationStatePersistenceStrategy;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.trsvax.tapestry.aws.services.Key;



public class DynamoDBApplicationStatePersistenceStrategy extends SessionApplicationStatePersistenceStrategy {
	
	private final DynamoDBMapper mapper;
	private final Key key;
	private final Logger logger;

	public DynamoDBApplicationStatePersistenceStrategy(Request request, AmazonDynamoDB client, Key key, Logger logger) {
		super(request);
		mapper = new DynamoDBMapper(client);
		this.key = key;
		this.logger = logger;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> ssoClass, ApplicationStateCreator<T> creator) {
		final Object persistedValue =  getOrCreate(ssoClass, creator);
		logger.info("persisted {}",persistedValue);
		if(persistedValue instanceof DynamoDBPersistedEntity) {
			final DynamoDBPersistedEntity persisted = (DynamoDBPersistedEntity) persistedValue;
			Object restored = persisted.restore(this.mapper);
			logger.info("loaded {}",restored);
			if(restored == null) {
				set(ssoClass, null);
				return (T) getOrCreate(ssoClass, creator);
			}
			
			return (T) restored;
		}
		
		return (T) persistedValue;
	}

	@Override
	public <T> void set(Class<T> ssoClass, T sso) {
		final String sessionkey = buildKey(ssoClass);
		logger.info("set {}",sessionkey);
		Object entity = null;
		
		if ( sso != null ) {
			KeyPair keyPair = key.pair(sso);
			
			//Best guess that it's persisted
			if ( keyPair.getHashKey() != null ) {				
				entity = new DynamoDBPersistedEntity(ssoClass, keyPair);
			} else {
				entity = sso;
			}
		}	
		getSession().setAttribute(sessionkey, entity);
	}
	
	
}
