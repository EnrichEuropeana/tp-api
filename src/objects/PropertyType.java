package objects;

public class PropertyType {
	public Integer PropertyTypeId;
	public String Name;
	public String Motivation;
	public String Editable;
	public Integer ProjectId;
	
	public void setPropertyTypeId(Integer propertyTypeId) {
		PropertyTypeId = propertyTypeId;
	}
	public void setName(String name) {
		Name = name;
	}
	public void setMotivation(String motivation) {
		Motivation = motivation;
	}
	public void setEditable(String editable) {
		Editable = editable;
	}
	public void setProjectId(Integer projectId) {
		ProjectId = projectId;
	}
	
	
}
