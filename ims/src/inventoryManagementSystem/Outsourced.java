package inventoryManagementSystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Outsourced extends Part {
	private StringProperty companyName = new SimpleStringProperty();

	public void setcompanyName(String companyName) {
		this.companyName.set(companyName);
	}
	
	public String getCompanyName() {
		return companyName.get();
	}
}
