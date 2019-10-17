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
import marks.demo.model.Adjustment;
import marks.demo.model.SaleAdjustmentMessage;

public class SaleAdjustmentMessageTest implements Runnable {
	private static final Logger logger = Logger.getLogger(SaleAdjustmentMessageTest.class.getCanonicalName());
	private ObjectMapper mapper = new ObjectMapper();

	JMSFactoryInterface factory = new ActiveMQFactory();
	MessageProducer producer;
	boolean set = true;

	public SaleAdjustmentMessageTest() throws JMSException, Exception {
		producer = factory.createTopicProducer("saleAdjustmentMessage");
	}

	@Override
	public void run() {
		try {
			while (true) {
				List<SaleAdjustmentMessage> messages = new ArrayList<SaleAdjustmentMessage>();
				if (set) {
					messages.addAll(getDataToSend1());
				} else {
					messages.addAll(getDataToSend2());
				}
				set = !set;

				for (SaleAdjustmentMessage message : messages) {
					StringWriter sw = new StringWriter();
					mapper.writeValue(sw, message);
					TextMessage textMsg = factory.createTextMessage(sw.toString());
					producer.send(textMsg);
					Thread.sleep(2000);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			factory.shutdown();
		}

	}

	public List<SaleAdjustmentMessage> getDataToSend1() {
		List<SaleAdjustmentMessage> messages = new ArrayList<SaleAdjustmentMessage>();
		messages.add(new SaleAdjustmentMessage("Apple", .03f, Adjustment.ADD));
		messages.add(new SaleAdjustmentMessage("Old Peculier", .15f, Adjustment.SUBTRACT));
		messages.add(new SaleAdjustmentMessage("Cheese", .05f, Adjustment.ADD));
		messages.add(new SaleAdjustmentMessage("Guiness", 1.03f, Adjustment.MULTIPLY));
		messages.add(new SaleAdjustmentMessage("Pizza", .02f, Adjustment.SUBTRACT));
		return messages;
	}

	public List<SaleAdjustmentMessage> getDataToSend2() {
		List<SaleAdjustmentMessage> messages = new ArrayList<SaleAdjustmentMessage>();
		messages.add(new SaleAdjustmentMessage("Apple", .03f, Adjustment.SUBTRACT));
		messages.add(new SaleAdjustmentMessage("Old Peculier", .15f, Adjustment.ADD));
		messages.add(new SaleAdjustmentMessage("Cheese", .05f, Adjustment.SUBTRACT));
		messages.add(new SaleAdjustmentMessage("Guiness", .15f, Adjustment.SUBTRACT));
		messages.add(new SaleAdjustmentMessage("Pizza", .02f, Adjustment.ADD));
		return messages;
	}
}
