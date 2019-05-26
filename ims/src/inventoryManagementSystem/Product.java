package inventoryManagementSystem;

import inventoryManagementSystem.Model.StockErrors;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class Product {
	private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
	private IntegerProperty productID = new SimpleIntegerProperty();
	private StringProperty name = new SimpleStringProperty();
	private DoubleProperty price = new SimpleDoubleProperty();
	private IntegerProperty inStock = new SimpleIntegerProperty();
	private IntegerProperty min = new SimpleIntegerProperty();
	private IntegerProperty max = new SimpleIntegerProperty();
	
	
	public void addAssociatedPart(Part part) {
		associatedParts.add(part);
	}
	
	public void clearAssociatedParts() {
		associatedParts.clear();
	}
	
	public boolean removeAssociatedPart(int partID) {
		if (lookupAssociatedPart(partID) != null) {
			return associatedParts.remove(lookupAssociatedPart(partID));
		}
		
		return false;
	}
	
	public Part lookupAssociatedPart(int partID) {
		for (Part part : associatedParts) {
			if (part.getPartID() == partID) {
				return part;
			}
			else {
				return null;
			}
		}
		
		return null;
	}
	
	public ObservableList<Part> getAllAssociatedParts() {
			return associatedParts;
	}
	
	public void setName(String name) {
		this.name.set(name);
	};
	
	public String getName() {
		return name.get();
	}
	
	public void setPrice(double price) throws Exception {
		double totalPrice = 0.00;
		
		for (Part part : associatedParts) {
			totalPrice += part.getPrice();
		}
		if (price >= totalPrice) {
			this.price.set(price);
		}
		else {
			throw new Exception(StockErrors.PRICE_TOO_LOW.toString(), new Throwable(Double.toString(totalPrice)));
		}
	}
	
	public double getPrice() {
		return price.get();
	}
	
	public void setInStock(int inStock) throws Exception {
		if (inStock >= min.get() && inStock <= max.get()  || inStock == 0) {
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
		
	public void setProductID(int productID) {
		this.productID.set(productID);
	}
	
	public int getProductID() {
		return productID.get();
	}
}
