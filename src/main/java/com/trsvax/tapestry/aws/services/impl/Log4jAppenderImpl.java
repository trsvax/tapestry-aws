
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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.tapestry5.ioc.ServiceResources;

import com.trsvax.tapestry.aws.services.ServiceAppender;
import com.trsvax.tapestry.aws.services.ThresholdAppender;


public class Log4jAppenderImpl extends AppenderSkeleton implements  ThresholdAppender {	
	private ServiceResources resources;
	
	public ThresholdAppender withResources(ServiceResources resources) {
		this.resources = resources;
		return this;
	}

	@Override
	protected void append(LoggingEvent event) {
		if ( this.layout == null || resources == null ) {
			return;
		}		
		resources.getService(ServiceAppender.class).append(this.getLayout(),event);		
	}

	@Override
	public void close() {		
		resources.getService(ServiceAppender.class).close();		

	}

	@Override
	public boolean requiresLayout() {
		return true;
	}
}
