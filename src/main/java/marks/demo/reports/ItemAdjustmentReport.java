package marks.demo.reports;

import java.util.ArrayList;
import java.util.List;

public class ItemAdjustmentReport extends ItemReport {
	private List<String> adjustments = new ArrayList<String>();

	public ItemAdjustmentReport() {
	}
	
	public void addAdjustment(String adjustment) {
		adjustments.add(adjustment);
	}

	public List<String> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<String> adjustments) {
		this.adjustments = adjustments;
	}

}
