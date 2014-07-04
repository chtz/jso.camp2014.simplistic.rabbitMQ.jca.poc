package com.zuehlke.cht.demo.rabbit.jca.rar.impl;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

final class RabbitConsumer implements Consumer {
	private final WorkManager workManager;
	private final MessageEndpointFactory messageEndpointFactory;
	private final Connection connection;
	private final Channel channel;

	public RabbitConsumer(WorkManager workManager, MessageEndpointFactory messageEndpointFactory, String queueName) throws IOException {
		System.out.println("RabbitConsumer(" + workManager + "," + messageEndpointFactory  + "," + queueName + ")"); //DEBUG
		
		this.workManager = workManager;
		this.messageEndpointFactory = messageEndpointFactory;
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    
	    connection = factory.newConnection();
	    
	    channel = connection.createChannel();
	    channel.queueDeclare(queueName, false, false, false, null);
	    channel.basicConsume(queueName, true, this);
	}

	public void stop() {
		try {
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e); //FIXME
		}
		finally {
			try {
				connection.close();
			} catch (IOException e) {
				throw new RuntimeException(e); //FIXME
			}
		}
	}
	
	@Override
	public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) {
		System.out.println("handleDelivery(" + consumerTag + "," + envelope + "," + properties + "," + body); //DEBUG
		
		try {
			final MessageEndpoint endpoint = messageEndpointFactory.createEndpoint(null);
			
			workManager.scheduleWork(new Work() {
				@Override
				public void run() {
					try {
						Method endpointMethod = endpoint.getClass().getMethod("handleDelivery", String.class, Envelope.class,
								AMQP.BasicProperties.class, byte[].class);
						endpoint.beforeDelivery(endpointMethod);
						endpointMethod.invoke(endpoint, consumerTag, envelope, properties, body);
						endpoint.afterDelivery();
					} catch (Exception e) {
						throw new RuntimeException(e); //FIXME error handling
					}
				}
				
				@Override
				public void release() {}
			});
		} catch (WorkException e) {
			throw new RuntimeException(e); //FIXME
		} catch (UnavailableException e) {
			throw new RuntimeException(e); //FIXME
		}
	}
	
	@Override
	public void handleConsumeOk(String consumerTag) {
		//FIXME
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		//FIXME
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		//FIXME
	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		//FIXME
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		//FIXME
	}
}
