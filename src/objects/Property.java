package objects;

public class Property {
	public Integer PropertyId;
	public String Value;
	public String TypeName;
	public String Editable;
	

	public void setPropertyId(Integer propertyId) {
		PropertyId = propertyId;
	}
	public void setValue(String value) {
		Value = value;
	}
	public void setTypeName(String typeName) {
		TypeName = typeName;
	}
	public void setEditable(String editable) {
		Editable = editable;
	}
}
