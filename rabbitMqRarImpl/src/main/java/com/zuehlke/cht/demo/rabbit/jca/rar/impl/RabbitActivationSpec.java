package com.zuehlke.cht.demo.rabbit.jca.rar.impl;

import javax.resource.ResourceException;
import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

import com.zuehlke.cht.demo.rabbit.jca.rar.RabbitListener;

@Activation(messageListeners = RabbitListener.class)
public class RabbitActivationSpec implements ActivationSpec {
	private ResourceAdapter resourceAdapter;
	private String queueName;

	@Override
	public void validate() throws InvalidPropertyException {
		System.out.println("validate(" + queueName + ")"); // DEBUG
	}

	@Override
	public ResourceAdapter getResourceAdapter() {
		return resourceAdapter;
	}

	@Override
	public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
		this.resourceAdapter = resourceAdapter;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}
