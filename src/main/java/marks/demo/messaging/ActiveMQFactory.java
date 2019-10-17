package marks.demo.messaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQFactory implements JMSFactoryInterface {
	private static final Logger logger = Logger.getLogger(ActiveMQFactory.class.getCanonicalName());
	private ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	private Connection connection;
	private Session session;

	public ActiveMQFactory() throws JMSException {
		connection = factory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	@Override
	public MessageConsumer createTopicConsumer(String topicName) throws JMSException {
		Topic topic = session.createTopic(topicName);
		MessageConsumer consumer = session.createConsumer(topic);
		return consumer;
	}
	
	@Override
	public MessageProducer createTopicProducer(String topicName) throws JMSException {
		Topic topic = session.createTopic(topicName);
		MessageProducer producer = session.createProducer(topic);
		return producer;		
	}
	
	@Override
	public TextMessage createTextMessage(String text) throws JMSException {
		return session.createTextMessage(text);
	}
	
	@Override
	public void shutdown() {
		try {
			session.close();
			connection.close();
		} catch (JMSException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
