package com.trsvax.tapestry.aws.marshaller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;


public class BeanMarshaller implements DynamoDBMarshaller<Object> {
	
	public String marshall(Object getterReturnResult) {
		if ( getterReturnResult == null ) {
			return "";
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(os);
		encoder.writeObject(getterReturnResult);
		encoder.close();
		return new String(os.toByteArray());		
	}

	public Object unmarshall(Class<Object> clazz, String obj) {
		if ( obj == null || obj.length() == 0 ) {
			return null;
		}
		XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(obj.getBytes()));
		try {
			return  decoder.readObject();
		} finally {
			decoder.close();
		}
	}

}
