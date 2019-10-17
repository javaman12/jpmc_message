package marks.demo.model;

public class SaleAdjustmentMessage extends SaleMessage {

	private Adjustment adjustment;
	
	public SaleAdjustmentMessage() {
	}

	public SaleAdjustmentMessage(String item, float value, Adjustment adjustment) {
		super(item, value);
		this.adjustment = adjustment;
	}

	public Adjustment getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Adjustment adjustment) {
		this.adjustment = adjustment;
	}

}
