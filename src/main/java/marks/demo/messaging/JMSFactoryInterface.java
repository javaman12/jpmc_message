package marks.demo.messaging;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

public interface JMSFactoryInterface {

	MessageConsumer createTopicConsumer(String topicName) throws JMSException;

	MessageProducer createTopicProducer(String topicName) throws JMSException;

	TextMessage createTextMessage(String text) throws JMSException;

	void shutdown();

}