package com.trsvax.tapestry.aws.services.dynamodb;

import java.util.List;
import java.util.Map;

import org.apache.tapestry5.services.ValueEncoderFactory;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;

public interface DynamoDBDAO<T> {
	
	public int count(DynamoDBQueryExpression queryExpression);
	public int count(DynamoDBScanExpression scanExpression);
	public void delete(T entity);
	public void delete(T entity, DynamoDBMapperConfig config);
	public T load(Object key);
	public T load(Object key, DynamoDBMapperConfig config);
	public T load(Object key, Object range);
	public T load(Object key, Object range, DynamoDBMapperConfig config);
	public T marshallIntoObject(Map<String, AttributeValue> itemAttributes);
	public List<T> query(DynamoDBQueryExpression queryExpression);
	public void save(T entity);
	public void save(T enitty, DynamoDBMapperConfig config);
	public List<T> scan();
	public List<T> scan(DynamoDBScanExpression scanExpression);
	
	public ValueEncoderFactory<T> encoderFactory();

}
