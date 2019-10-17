package marks.demo.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import marks.demo.messaging.ActiveMQFactory;
import marks.demo.messaging.JMSFactoryInterface;
import marks.demo.model.SaleMessage;

public class SaleMessageTest implements Runnable {
	private static final Logger logger = Logger.getLogger(SaleMessageTest.class.getCanonicalName());
	private ObjectMapper mapper = new ObjectMapper();

	JMSFactoryInterface factory = new ActiveMQFactory();
	MessageProducer producer;

	public SaleMessageTest() throws JMSException, Exception {
		producer = factory.createTopicProducer("saleMessage");
	}

	@Override
	public void run() {
		try {
			while (true) {
				List<SaleMessage> messages = getDataToSend();

				for (SaleMessage message : messages) {
					StringWriter sw = new StringWriter();
					mapper.writeValue(sw, message);
					TextMessage textMsg = factory.createTextMessage(sw.toString());
					producer.send(textMsg);
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			factory.shutdown();
		}
	}

	public List<SaleMessage> getDataToSend() {
		List<SaleMessage> messages = new ArrayList<SaleMessage>();
		messages.add(new SaleMessage("Apple", .20f));
		messages.add(new SaleMessage("Old Peculier", 3.50f));
		messages.add(new SaleMessage("Cheese", 2.32f));
		messages.add(new SaleMessage("Guiness", 3.56f));
		messages.add(new SaleMessage("Pizza", 4.75f));
		return messages;
	}

	public static void main(String[] args) throws JMSException, Exception {
		new Thread(new SaleMessageTest()).start();

	}
}
