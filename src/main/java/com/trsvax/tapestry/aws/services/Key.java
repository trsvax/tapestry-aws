package com.trsvax.tapestry.aws.services;

import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;

public interface Key {

	public KeyPair pair(Object entity);
	public String string(Object entity);
	public KeyPair fromString(String clientValue);
}
