package marks.demo.datastore;

import marks.demo.model.MultiSaleMessage;
import marks.demo.model.SaleAdjustmentMessage;
import marks.demo.model.SaleMessage;

public interface MessageProcessInterface {

	void processSaleMessage(SaleMessage message) throws Exception;
	void processMultiSaleMessage(MultiSaleMessage message) throws Exception;
	void processSaleAdjustmentMessage(SaleAdjustmentMessage message) throws Exception;
}
