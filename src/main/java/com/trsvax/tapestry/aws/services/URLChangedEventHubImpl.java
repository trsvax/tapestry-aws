package com.trsvax.tapestry.aws.services;

import java.util.List;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;

import com.trsvax.tapestry.aws.URLChangedListener;

public class URLChangedEventHubImpl implements URLChangedEventHub {
    private final List<URLChangedListener> listeners = CollectionFactory.newThreadSafeList();


	public void addURLChangedtListener(URLChangedListener listener) {
		listeners.add(listener);
	}

	public void removeURLChangedListener(URLChangedListener listener) {
        listeners.remove(listener);
	}

	public void fire(String url) {
        for (URLChangedListener l : listeners)
        {
            l.URLDidChange(url);
        }
	}

}
