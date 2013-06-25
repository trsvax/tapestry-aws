package com.trsvax.tapestry.aws.internal;

import org.apache.tapestry5.ValueEncoder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.trsvax.tapestry.aws.services.Key;

public class DynamoDBEntityValueEncoder<E> implements ValueEncoder<E> {
	
	private final Class<E> entityClass;
	private final Key key;
	private final DynamoDBMapper mapper;
	
	public DynamoDBEntityValueEncoder(Class<E> entityClass, Key key, AmazonDynamoDB client) {
		this.entityClass = entityClass;
		this.key = key;
		mapper = new DynamoDBMapper(client);
	}

	public String toClient(E value) {
		return key.string(value);
	}

	public E toValue(String clientValue) {
		KeyPair keyPair = key.fromString(clientValue);
		return mapper.load(entityClass,keyPair.getHashKey(),keyPair.getRangeKey());
	}

	

}
