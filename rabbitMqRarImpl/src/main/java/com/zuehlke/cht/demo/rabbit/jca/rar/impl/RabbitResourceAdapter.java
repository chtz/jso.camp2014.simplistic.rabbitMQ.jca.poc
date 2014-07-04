package com.zuehlke.cht.demo.rabbit.jca.rar.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

@Connector
public class RabbitResourceAdapter implements ResourceAdapter {
	private final Map<ActivationSpec,RabbitConsumer> consumers = new ConcurrentHashMap<>();
	private BootstrapContext bootstrapContext;
	
	@Override
	public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec activationSpec) throws ResourceException {
		System.out.println("endpointActivation(" + endpointFactory + "," + activationSpec + ")"); //DEBUG
		
		try {
			RabbitActivationSpec fsWatcherAS = (RabbitActivationSpec) activationSpec;
			
			consumers.put(fsWatcherAS, new RabbitConsumer(bootstrapContext.getWorkManager(), endpointFactory, fsWatcherAS.getQueueName()));
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void stop() {
		System.out.println("stop()"); //DEBUG
		
		for (ActivationSpec as : consumers.keySet()) {
			endpointDeactivation(null,  as);
		}
	}
	
	@Override
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec activationSpec) {
		System.out.println("endpointDeactivation(" + endpointFactory + "," + activationSpec  + ")"); //DEBUG
		
		RabbitConsumer c = consumers.remove(activationSpec);
		
		if (c != null) {
			c.stop();
		}
	}

	@Override
	public XAResource[] getXAResources(ActivationSpec[] arg0) throws ResourceException {
		System.out.println("getXAResources(" + arg0 + ")"); //DEBUG
		
		return null;
	}

	@Override
	public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
		System.out.println("start(" + bootstrapContext + ")"); //DEBUG
		
		this.bootstrapContext = bootstrapContext;
	}

	public BootstrapContext getBootstrapContext() {
		return bootstrapContext;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
