package marks.demo.reports;

public class ItemSalesReport extends ItemReport {
	private int numberOfSales;
	private float totalSales;
	
	public ItemSalesReport() {
		
	}

	public int getNumberOfSales() {
		return numberOfSales;
	}

	public void setNumberOfSales(int numberOfSales) {
		this.numberOfSales = numberOfSales;
	}

	public float getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(float totalSales) {
		this.totalSales = totalSales;
	}
	
}
