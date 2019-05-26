package inventoryManagementSystem;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Inhouse extends Part {
	private IntegerProperty machineID = new SimpleIntegerProperty();
	
	public void setMachineID(int machineID) {
		this.machineID.set(machineID);
	}
	
	public int getMachineID() {
		return machineID.get();
	}
}
