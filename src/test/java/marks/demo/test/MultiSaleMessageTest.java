package marks.demo.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import marks.demo.messaging.ActiveMQFactory;
import marks.demo.messaging.JMSFactoryInterface;
import marks.demo.model.MultiSaleMessage;

public class MultiSaleMessageTest implements Runnable {
	private static final Logger logger = Logger.getLogger(MultiSaleMessageTest.class.getCanonicalName());
	private ObjectMapper mapper = new ObjectMapper();

	JMSFactoryInterface factory = new ActiveMQFactory();
	MessageProducer producer;
	Random random = new Random();

	public MultiSaleMessageTest() throws JMSException, Exception {
		producer = factory.createTopicProducer("multiSaleMessage");
	}

	@Override
	public void run() {
		try {
			while (true) {
				List<MultiSaleMessage> messages = getDataToSend();

				for (MultiSaleMessage message : messages) {
					StringWriter sw = new StringWriter();
					mapper.writeValue(sw, message);
					TextMessage textMsg = factory.createTextMessage(sw.toString());
					producer.send(textMsg);
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			factory.shutdown();
		}

	}

	public List<MultiSaleMessage> getDataToSend() {
		List<MultiSaleMessage> messages = new ArrayList<MultiSaleMessage>();
		messages.add(new MultiSaleMessage("Apple", .20f, random.nextInt(11)));
		messages.add(new MultiSaleMessage("Old Peculier", 3.50f, random.nextInt(5)));
		messages.add(new MultiSaleMessage("Cheese", 2.32f, random.nextInt(6)));
		messages.add(new MultiSaleMessage("Guiness", 3.56f, random.nextInt(6)));
		messages.add(new MultiSaleMessage("Pizza", 4.75f, random.nextInt(6)));
		return messages;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	}

}
