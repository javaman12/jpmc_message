package marks.demo.model;

public class MultiSaleMessage extends SaleMessage {

	private int count;
	
	public MultiSaleMessage() {
	}

	public MultiSaleMessage(String item, float value, int count) {
		super(item, value);
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
