package inventoryManagementSystem;

import inventoryManagementSystem.Model.StockErrors;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

abstract class Part {
	private IntegerProperty partID = new SimpleIntegerProperty();
	private StringProperty name = new SimpleStringProperty();
	private DoubleProperty price = new SimpleDoubleProperty();
	private IntegerProperty inStock = new SimpleIntegerProperty();
	private IntegerProperty min = new SimpleIntegerProperty();
	private IntegerProperty max = new SimpleIntegerProperty();
	
	public void setName(String name) {
		this.name.set(name);
	};
	
	public String getName() {
		return name.get();
	}
	
	public void setPrice(double price) {
		this.price.set(price);
	}
	
	public double getPrice() {
		return price.get();
	}
	
	public void setInStock(int inStock) throws Exception {
		if (inStock >= min.get() && inStock <= max.get() || inStock == 0) {
			this.inStock.set(inStock);
		}
		else {
			
			throw new Exception(StockErrors.INVALID_STOCK.toString());
		}
	}
	
	public int getInStock() {
		return inStock.get();
	}
	
	public void setMin(int min) throws Exception {
		if (min < 0) {
			throw new Exception(StockErrors.MIN_TOO_LOW.toString());
		}
		else if (min <= getMax()) {
			this.min.set(min);
		}
		else {
			throw new Exception(StockErrors.MIN_TOO_HIGH.toString());
		}
		
	}
	
	public int getMin() {
		return min.get();
	}
	
	public void setMax(int max) throws Exception {
		if (max >= getMin()) {
			this.max.set(max);
		}
		else {
			throw new Exception(StockErrors.MAX_TOO_LOW.toString());
		}
	}
	
	public int getMax() {
		return max.get();
	}
	
	public void setPartID(int partID) {
		this.partID.set(partID);
	}
	
	public int getPartID() {
		return partID.get();
	}
}
