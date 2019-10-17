package marks.demo.messaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import marks.demo.datastore.MessageProcessInterface;
import marks.demo.model.SaleAdjustmentMessage;

public class SaleAdjustmentMessageConsumer implements Runnable {
	private static final Logger logger = Logger.getLogger(SaleAdjustmentMessageConsumer.class.getCanonicalName());
	public static final String TOPIC_NAME = "saleAdjustmentMessage";

	private JMSFactoryInterface factory;
	private MessageProcessInterface messageInterface;
	private MessageConsumer consumer;
	private ObjectMapper mapper = new ObjectMapper();

	public SaleAdjustmentMessageConsumer(MessageProcessInterface messageInterface, JMSFactoryInterface factory)
			throws JMSException {
		this.factory = factory;
		this.messageInterface = messageInterface;
		this.consumer = factory.createTopicConsumer(TOPIC_NAME);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Message message = consumer.receive();
				if (message instanceof TextMessage) {
					TextMessage textMsg = (TextMessage) message;
					try {
						SaleAdjustmentMessage saleMessage = mapper.readValue(textMsg.getText(),
								SaleAdjustmentMessage.class);
						messageInterface.processSaleAdjustmentMessage(saleMessage);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Invalid message payload", e);
					}
				} else {
					logger.log(Level.WARNING,
							"Invalid message type. Expected TextMessage but received " + message.getClass().getName());
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			factory.shutdown();
		}

	}

}
