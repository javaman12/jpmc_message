package marks.demo.test;

import javax.jms.JMSException;

public class TestRunner {

	public TestRunner() throws JMSException, Exception {
		Thread t1 = new Thread(new SaleMessageTest());
		Thread t2 = new Thread(new MultiSaleMessageTest());
		Thread t3 = new Thread(new SaleAdjustmentMessageTest());
		t1.start();
		t2.start();
		t3.start();
	}

	public static void main(String[] args) throws JMSException, Exception {
		new TestRunner();
	}

}
