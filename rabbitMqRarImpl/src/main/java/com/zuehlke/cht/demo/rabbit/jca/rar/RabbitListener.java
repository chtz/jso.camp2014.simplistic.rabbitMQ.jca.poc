package com.zuehlke.cht.demo.rabbit.jca.rar;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public interface RabbitListener {
	public void handleDelivery(java.lang.String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body); 
}
