package com.trsvax.tapestry.aws.services;

import org.apache.tapestry5.ioc.ServiceBinder;

import com.trsvax.tapestry.aws.services.impl.EmailImpl;

public class AWSModule {
    public static void bind(ServiceBinder binder) {
    	binder.bind(Email.class,EmailImpl.class);
    }

}
