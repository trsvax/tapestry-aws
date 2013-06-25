package com.trsvax.tapestry.aws.services.impl;

import java.lang.reflect.Method;

import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.trsvax.tapestry.aws.services.Key;

public class KeyImpl implements Key {
	private final TypeCoercer coercer;
	
	public KeyImpl(TypeCoercer coercer ) {
		this.coercer = coercer;
	}
	

	public KeyPair pair(Object enitity) {
		return new KeyPair().withHashKey(hashKey(enitity)).withRangeKey(rangeKey(enitity));
	}
	
	private Object hashKey(Object sso) {
		for ( Method method : sso.getClass().getMethods() ) {
			if ( method.getAnnotation(DynamoDBHashKey.class) != null ) {
				try {
					return method.invoke(sso);
				} catch (Exception e) {
					break;
				}
			}			
		}
		return null;
	}

	private  Object rangeKey(Object sso) {
		for ( Method method : sso.getClass().getMethods() ) {
			if ( method.getAnnotation(DynamoDBRangeKey.class) != null ) {
				try {
					return method.invoke(sso);
				} catch (Exception e) {
					break;
				}
			}			
		}
		return null;
	}

	public String string(Object entity) {
		
		KeyPair keyPair = pair(entity);
		String string = convert(keyPair.getHashKey());
		String range = convert(keyPair.getRangeKey());
		if ( range != null ) {
			string += ":" + range;
		}
		return string;
	}

	private String convert(Object key) {
		if ( key == null ) {
			return null;
		}
		if ( String.class.isAssignableFrom(key.getClass()) ) {
			String v = (String) key;
			return "'" + v.replaceAll("'","''") + "'";
		}
		if ( Integer.class.isAssignableFrom(key.getClass()) ) {
			Integer v = (Integer) key;
			return "I" + coercer.coerce(v, String.class);
		}
		if ( Long.class.isAssignableFrom(key.getClass()) ) {
			Long v = (Long) key;
			return "L" + coercer.coerce(v, String.class);
		}
		return null;
	}

	public KeyPair fromString(String clientValue) {
		KeyPair keyPair = new KeyPair();
		return keyPair;
	}

}
