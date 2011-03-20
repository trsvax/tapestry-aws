
//Copyright [2011] [Barry Books]

//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at

//       http://www.apache.org/licenses/LICENSE-2.0

//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.trsvax.tapestry.aws.services.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.trsvax.tapestry.aws.services.ServiceAppender;

public class AWSAppenderImpl implements ServiceAppender {
	private final AmazonSNS client;
	private final Map<Level, String> configuration;
	private String format = "[%s] unknown %s";
	
	public AWSAppenderImpl(Map<Level,String> configuration, AmazonSNS client)  {
		this.client = client;
		this.configuration = configuration;
		try {
			format = "[%s] " + InetAddress.getLocalHost().getHostName() + " %s";
		} catch (UnknownHostException e) {
			//don't care
		}
	}

	public void append(Layout layout, LoggingEvent event) {
		String arn = configuration.get(event.getLevel());

		if ( arn != null )  {
			String msg = String.format(format, event.getLevel(), event.getLoggerName());
			client.publish(new PublishRequest(arn,layout.format(event),msg));
		}		
	}

	public void close() {
		// Nothing to close
	}

}
