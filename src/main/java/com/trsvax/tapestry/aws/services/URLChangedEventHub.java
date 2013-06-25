package com.trsvax.tapestry.aws.services;

import com.trsvax.tapestry.aws.URLChangedListener;

public interface URLChangedEventHub {
	
	void addURLChangedtListener(URLChangedListener listener);

    void removeURLChangedListener(URLChangedListener listener);

    void fire(String url);

}
