package marks.demo;

import javax.jms.JMSException;

import org.apache.activemq.broker.BrokerService;

import marks.demo.datastore.DatastoreInterface;
import marks.demo.datastore.MessageGateway;
import marks.demo.datastore.MessageStore;
import marks.demo.messaging.ActiveMQFactory;
import marks.demo.messaging.JMSFactoryInterface;
import marks.demo.messaging.MultiSaleMessageConsumer;
import marks.demo.messaging.SaleAdjustmentMessageConsumer;
import marks.demo.messaging.SaleMessageConsumer;

public class MessageDemo {
	JMSFactoryInterface factory;
	private MessageGateway messageGateway;
	
	public MessageDemo(DatastoreInterface datastore, JMSFactoryInterface factory) throws JMSException {
		this.factory = factory;
		messageGateway = new MessageGateway(datastore);
		
		SaleMessageConsumer smConsumer = new SaleMessageConsumer(messageGateway, factory);
		MultiSaleMessageConsumer msmConsumer = new MultiSaleMessageConsumer(messageGateway, factory);
		SaleAdjustmentMessageConsumer saConsumer = 
				new SaleAdjustmentMessageConsumer(messageGateway, factory);
		
		Thread smThread = new Thread(smConsumer);
		Thread msmThread = new Thread(msmConsumer);
		Thread saThread = new Thread(saConsumer);
		
		smThread.start();
		msmThread.start();
		saThread.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				factory.shutdown();
			}
		});
	}

	public static void main(String[] args) {
		BrokerService broker = new BrokerService();
		try {
			broker.addConnector("tcp://localhost:61616");
			broker.start();
			new MessageDemo(new MessageStore(), new ActiveMQFactory());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
