package com.trsvax.tapestry.aws.services.dynamodb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.slf4j.Logger;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;

public abstract class AbstractDynamoDBDAO<T> implements DynamoDBDAO<T> {
	private final DynamoDBMapper mapper;
	private final Method hashKeyMethod;
	private final Method rangeKeyMethod;
	private final Class<T> clazz;
	
	@Inject
	private Logger logger;

	
	public AbstractDynamoDBDAO(AmazonDynamoDB client, Class<T> clazz)  {
		this.mapper = new DynamoDBMapper(client);
		this.clazz = clazz;
		hashKeyMethod = findMethod(clazz,DynamoDBHashKey.class);
		if ( hashKeyMethod == null ) {
			throw new RuntimeException("No DynamoDBHashKey for " + clazz.getName());
		}
		rangeKeyMethod = findMethod(clazz, DynamoDBRangeKey.class);
	}
	
	public int count(DynamoDBQueryExpression queryExpression) {
		try {
			return mapper.count(clazz, queryExpression);
		} catch (Exception e) {
			throw runtime(e);
		}
	}
	
	public int count(DynamoDBScanExpression scanExpression) {
		try {
			return mapper.count(clazz, scanExpression);
		} catch (Exception e) {
			throw runtime(e);
		}
	}
	
	public void delete(T object) {
		mapper.delete(object);
	}	

	public void delete(T object, DynamoDBMapperConfig config) {
		mapper.delete(object, config);
	}
	
	public T load(Object hashKey) {
		return mapper.load(clazz, hashKey);
	}

	public T load(Object hashKey,DynamoDBMapperConfig config) {
		return mapper.load(clazz, hashKey, config);
	}

	public T load(Object hashKey, Object rangeKey, DynamoDBMapperConfig config) {
		return mapper.load(clazz, hashKey, rangeKey, config);
	}

	public T load(Object hashKey, Object rangeKey) {
		return mapper.load(clazz, hashKey, rangeKey);
	}


	public T marshallIntoObject(Map<String, AttributeValue> itemAttributes) {
		return mapper.marshallIntoObject(clazz, itemAttributes);
	}

	public List<T> query(DynamoDBQueryExpression queryExpression) {
		try {
			return mapper.query(clazz, queryExpression);
		} catch (Exception e) {
			throw runtime(e);
		}
	}
	
	public void save(T object) {
		mapper.save(object);
	}
	
	public void save(T object,DynamoDBMapperConfig config) {
		mapper.save(object,config);
	}
	
	public List<T> scan() {
		return scan(new DynamoDBScanExpression());
	}
	
	public List<T> scan(DynamoDBScanExpression scanExpression) {
		try {
			return mapper.scan(clazz, scanExpression);
		} catch (Exception e) {
			throw runtime(e);
		}
	}
	
	public List<T> keys(Set<String> keys) {
		if ( keys == null ) {
			return Collections.emptyList();
		}
		List<T> entities = new ArrayList<T>();
		for ( String key : keys) {
			T entity = load(key);
			entities.add(entity);
			
		}		
		return entities;
	}
	
	public List<T> sortedKeys(String sortOrder, Set<String> keys) {
		if ( keys == null ) {
			return Collections.emptyList();
		}
		List<String> sorted = Collections.emptyList();
		List<T> entities = new ArrayList<T>();
		
		if ( sortOrder == null ) {
			sorted = new ArrayList<String>(keys);
		} else {
			sorted = new ArrayList<String>(Arrays.asList(sortOrder.split(",")));
			keys.removeAll(sorted);
			sorted.addAll(keys);
		}
		for ( String key : sorted ) {
			T entity = load(key);
			if ( entity != null) {
				entities.add(entity);
			}
		}		
		return entities;
	}

	
	
	public ValueEncoderFactory<T> encoderFactory() {
		return new ValueEncoderFactory<T>() {
			public ValueEncoder<T> create(Class<T> type) {
				return new ValueEncoder<T>() {
					public String toClient(T entity) {
						if ( rangeKeyMethod != null ) {
							return getHashKey(entity) + "-" + getRangeKey(entity);
						}
						return getHashKey(entity);
					}
					public T toValue(String key) {
						if ( rangeKeyMethod != null ) {
							String[] keys = key.split("-",2);
							logger.info("keys {} {}",keys[0],keys[1]);
							if ( Integer.class.isAssignableFrom(rangeKeyMethod.getReturnType())) {
								return load(keys[0], new Integer(keys[1]));
							}
							return load(keys[0],keys[1]);
						}
						logger.info("key {}",key);

						return load(key);
					}
				};
			}
		};
	}
	
	String getHashKey(Object entity) {
		try {
			return hashKeyMethod.invoke(entity).toString();
		} catch (Exception e) {
			logger.error("getKey " + entity  + " method "+ hashKeyMethod + " message " + e.getMessage());
			throw new RuntimeException("getKey " + entity  + " method "+ hashKeyMethod + " message " + e.getMessage());
		}				
	}
	
	String getRangeKey(Object entity) {
		try {
			return rangeKeyMethod.invoke(entity).toString();
		} catch (Exception e) {
			logger.error("getKey " + entity  + " method "+ hashKeyMethod + " message " + e.getMessage());
			throw new RuntimeException("getKey " + entity  + " method "+ hashKeyMethod + " message " + e.getMessage());
		}
	}
	
	Method findMethod(Class<T> clazz, Class<? extends Annotation> annotation)  {
		for (Method m : clazz.getMethods()) {
		     if (m.isAnnotationPresent(annotation)) {
		           return m;	            
		     }
		}
		return null;
	}
	
	private RuntimeException runtime(Exception e) {
		return new RuntimeException(e.getMessage());		
	}

}
