package marks.demo.model;

public class SaleMessage {
	
	protected String item;
	protected float value;

	public SaleMessage() {
	}
	public SaleMessage(String item, float value) {
		this.item = item;
		this.value = value;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	

}
