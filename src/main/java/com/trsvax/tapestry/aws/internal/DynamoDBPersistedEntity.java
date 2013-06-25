package com.trsvax.tapestry.aws.internal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;

public class DynamoDBPersistedEntity {
	private final Class<?> clazz;
	private final KeyPair keyPair;
	
	public DynamoDBPersistedEntity(Class<?> clazz, KeyPair keyPair ) {
		this.clazz = clazz;
		this.keyPair = keyPair;
	}

	public Object restore(DynamoDBMapper mapper) {
		return mapper.load(clazz, keyPair.getHashKey(),keyPair.getRangeKey());
	}
	
	public String toString() {
		return String.format("<PersistedEntity: %s(%s,%s)>",clazz.getName(),keyPair.getHashKey(),keyPair.getRangeKey());
	}
	  
}
