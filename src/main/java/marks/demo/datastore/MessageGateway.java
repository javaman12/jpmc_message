package marks.demo.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import marks.demo.model.MultiSaleMessage;
import marks.demo.model.SaleAdjustmentMessage;
import marks.demo.model.SaleMessage;

public class MessageGateway implements MessageProcessInterface, DatestoreActiveListener {
	private static final Logger logger = Logger.getLogger(MessageGateway.class.getCanonicalName());
	private DatastoreInterface datastore;
	private boolean datastoreActive = true;

	private List<SaleMessage> saleMessageBacklog = new ArrayList<SaleMessage>();
	private List<MultiSaleMessage> multiMessageBacklog = new ArrayList<MultiSaleMessage>();
	private List<SaleAdjustmentMessage> adjMessageBacklog = new ArrayList<SaleAdjustmentMessage>();

	private Object syncObj = new Object();

	public MessageGateway(DatastoreInterface datastore) {
		this.datastore = datastore;
		this.datastore.addDatastoreActiveListener(this);
	}

	@Override
	public void processSaleMessage(SaleMessage message) throws Exception {
		synchronized (syncObj) {
			if (datastoreActive) {
				datastore.processSaleMessage(message);
			} else {
				saleMessageBacklog.add(message);
			}
		}
	}

	@Override
	public void processMultiSaleMessage(MultiSaleMessage message) throws Exception {
		synchronized (syncObj) {
			if (datastoreActive) {
				datastore.processMultiSaleMessage(message);
			} else {
				multiMessageBacklog.add(message);
			}
		}
	}

	@Override
	public void processSaleAdjustmentMessage(SaleAdjustmentMessage message) throws Exception {
		synchronized (syncObj) {
			if (datastoreActive) {
				datastore.processSaleAdjustmentMessage(message);
			} else {
				adjMessageBacklog.add(message);
			}
		}
	}

	@Override
	public void databaseActive() {
		datastoreActive = true;
		synchronized (syncObj) {
			try {
				int backlogCount = 0;
				int count = saleMessageBacklog.size() + multiMessageBacklog.size() + adjMessageBacklog.size();
				if(count > 0) {
					logger.log(Level.INFO, String.format(" %d backlog messages", count));
					while (datastoreActive && saleMessageBacklog.size() > 0) {
						SaleMessage message = saleMessageBacklog.remove(0);
						datastore.processSaleMessage(message);
						backlogCount++;
					}
					while (datastoreActive && multiMessageBacklog.size() > 0) {
						MultiSaleMessage message = multiMessageBacklog.remove(0);
						datastore.processMultiSaleMessage(message);
						backlogCount++;
					}
					while (datastoreActive && adjMessageBacklog.size() > 0) {
						SaleAdjustmentMessage message = adjMessageBacklog.remove(0);
						datastore.processSaleAdjustmentMessage(message);
						backlogCount++;
					}
					logger.log(Level.INFO, String.format("Worked of %d backlog messages", backlogCount));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void databasePaused() {
		datastoreActive = false;
	}

}
