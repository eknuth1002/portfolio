package inventoryManagementSystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class Inventory {
	private ObservableList<Part> allParts = FXCollections.observableArrayList();
	private ObservableList<Product> allProducts = FXCollections.observableArrayList();
		
	public ObservableList<Part> getAllParts() {
		return allParts;
	}
	
	public ObservableList<Product> getAllProducts() {
		return allProducts;
	}
	
	public void addProduct(Product product) {
		allProducts.add(product);
	}
	
	public boolean removeProduct(int productID) {
		Product productToRemove = lookupProduct(productID);
		
		if (productToRemove != null) {
			return allProducts.remove(productToRemove);
		}
			
		return false;
	}
	
	public void updateProduct(Product modifiedProduct) {
		for (Product productToBeReplaced : allProducts) {
			if (modifiedProduct.getProductID() == productToBeReplaced.getProductID()) {
				allProducts.set(allProducts.indexOf(productToBeReplaced), modifiedProduct);
			}
		}
	}
	
	public Product lookupProduct(int productID) {
		for (Product product : allProducts) {
			if (product.getProductID() == productID) {
				return product;
			}
		}
		
		return null;
	}
	
	public void addPart(Part part) {
		allParts.add(part);
	}
	
	public boolean removePart(int partID) {
		Part partToRemove = lookupPart(partID);
		
		if (partToRemove != null) {
			return allParts.remove(partToRemove);
		}
		
		return false;
	}
	
	public void updatePart(Part modifiedPart) {
		for (Part partToBeReplaced : allParts) {
			if (modifiedPart.getPartID() == partToBeReplaced.getPartID()) {
				try {
					partToBeReplaced.setName(modifiedPart.getName());
					partToBeReplaced.setMax(modifiedPart.getMax());
					partToBeReplaced.setMin(modifiedPart.getMin());
					partToBeReplaced.setInStock(modifiedPart.getInStock());
					partToBeReplaced.setPrice(modifiedPart.getPrice());
				}
				catch (Exception e) {
					
				}
				
			}
		}
	}
	
	public Part lookupPart(int partID) {
		for (Part part : allParts) {
			if (part.getPartID() == partID) {
				return part;
			}
		}
		
		return null;
	}
	
}
