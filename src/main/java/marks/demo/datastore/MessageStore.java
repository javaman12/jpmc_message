package marks.demo.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import marks.demo.model.Adjustment;
import marks.demo.model.MultiSaleMessage;
import marks.demo.model.SaleAdjustmentMessage;
import marks.demo.model.SaleMessage;
import marks.demo.reports.ItemAdjustmentReport;
import marks.demo.reports.ItemSalesReport;
import marks.demo.reports.SalesReportGenerator;

public class MessageStore implements DatastoreInterface {
	private static final Logger logger = Logger.getLogger(MessageStore.class.getCanonicalName());

	private int messageCount = 0;
	private boolean active = true;
	private Map<String, List<Float>> salesMap = new ConcurrentHashMap<String, List<Float>>();

	private Map<String, List<String>> adjustmentMap = new ConcurrentHashMap<String, List<String>>();

	private List<DatestoreActiveListener> databaseListeners = new ArrayList<DatestoreActiveListener>();

	private Object syncObj = new Object();

	public MessageStore() {
	}

	@Override
	public void processSaleMessage(SaleMessage message) throws Exception {
		if(!active) {
			throw new Exception("Datastore is not active");
		}
		synchronized (syncObj) {
			List<Float> values = salesMap.get(message.getItem());
			if (values == null) {
				values = new ArrayList<Float>();
			}
			values.add(message.getValue());
			salesMap.put(message.getItem(), values);
			messageCount++;
			checkForReport();
		}
	}

	@Override
	public void processMultiSaleMessage(MultiSaleMessage message) throws Exception {
		if(!active) {
			throw new Exception("Datastore is not active");
		}
		synchronized (syncObj) {
			List<Float> values = salesMap.get(message.getItem());
			if (values == null) {
				values = new ArrayList<Float>();
			}
			for (int i = 0; i < message.getCount(); i++) {
				values.add(message.getValue());
			}
			salesMap.put(message.getItem(), values);
			messageCount++;
			checkForReport();
		}
	}

	@Override
	public void processSaleAdjustmentMessage(SaleAdjustmentMessage message) throws Exception {
		if(!active) {
			throw new Exception("Datastore is not active");
		}
		synchronized (syncObj) {
			List<Float> values = salesMap.get(message.getItem());
			if (values != null) {
				Adjustment adj = message.getAdjustment();
				Float adjAmount = message.getValue();
				List<Float> adjValues = new ArrayList<Float>();
				String action = null;
				switch (adj) {
				case ADD:
					action = "added";
					for (Float value : values) {
						float adjusted = value + adjAmount;
						adjValues.add(adjusted);
					}
					break;
				case SUBTRACT:
					action = "subtracted";
					for (Float value : values) {
						float adjusted = value - adjAmount;
						adjValues.add(adjusted);
					}
					break;
				case MULTIPLY:
					action = "multiplied";
					for (Float value : values) {
						float adjusted = value * adjAmount;
						adjValues.add(adjusted);
					}
					break;
				}
				salesMap.put(message.getItem(), adjValues);
				List<String> adjustments = adjustmentMap.get(message.getItem());
				if (adjustments == null) {
					adjustments = new ArrayList<String>();
				}
				adjustments.add(String.format("Sale price was %s by %f.2", action, adjAmount));
				adjustmentMap.put(message.getItem(), adjustments);
				messageCount++;
				checkForReport();
			}
		}
	}

	private void checkForReport() {
		if (messageCount % 10 == 0) {
			printSumReport();
		}
		if (messageCount % 50 == 0) {
			printAdjustmentReport();
		}
	}

	private void printSumReport() {
		List<ItemSalesReport> reports = new ArrayList<ItemSalesReport>();
		for(String item : salesMap.keySet()) {
			reports.add(SalesReportGenerator.generateItemSalesReport(item, salesMap.get(item)));
		}
		StringBuilder sb = new StringBuilder("\n");
		reports.forEach(r -> {
			sb.append(String.format("Item: %s" +
												"\n\tTotal Sold: %d" +
												"\n\tTotal Amount: %.2f\n",
												r.getItem(),
												r.getNumberOfSales(),
												r.getTotalSales()));
		});
		logger.log(Level.INFO, sb.toString());
	}

	private void printAdjustmentReport() {
		active = false;
		logger.log(Level.INFO, "Datastore is paused for processing Adjustment Report");
		databaseListeners.forEach(d -> d.databasePaused());
		
		List<ItemAdjustmentReport> reports = new ArrayList<ItemAdjustmentReport>();
		for(String item : adjustmentMap.keySet()) {
			reports.add(SalesReportGenerator.generateItemAdjustmentReport(item, adjustmentMap.get(item)));
		}
		StringBuilder sb = new StringBuilder("\n");
		reports.forEach(r -> {
			sb.append(String.format("Item: %s\n", r.getItem()));
			r.getAdjustments().forEach(a -> {
				sb.append(String.format("\t%s\n", a));
			});
		});
		logger.log(Level.INFO, sb.toString());

		active = true;
		databaseListeners.forEach(d -> d.databaseActive());
	}

	@Override
	public void addDatastoreActiveListener(DatestoreActiveListener datestoreActiveListener) {
		databaseListeners.add(datestoreActiveListener);
	}

	@Override
	public void removeDatastoreActiveListener(DatestoreActiveListener datestoreActiveListener) {
		databaseListeners.remove(datestoreActiveListener);
	}

}
