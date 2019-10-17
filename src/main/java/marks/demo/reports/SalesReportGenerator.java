package marks.demo.reports;

import java.util.List;

public class SalesReportGenerator {

	public SalesReportGenerator() {
	}
	
	public static ItemSalesReport generateItemSalesReport(String item, List<Float> values) {
		ItemSalesReport report = new ItemSalesReport();
		report.setItem(item);
		report.setNumberOfSales(values.size());
		float total = 0;
		for(float v : values) {
			total += v;
		}
		report.setTotalSales(total);
		return report;
	}
	
	public static ItemAdjustmentReport generateItemAdjustmentReport(String item, List<String> adjustments) {
		ItemAdjustmentReport report = new ItemAdjustmentReport();
		report.setItem(item);
		report.setAdjustments(adjustments);
		return report;
	}

}
