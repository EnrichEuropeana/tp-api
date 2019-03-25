package objects;

public class Place {
	public Integer PlaceId; 
	public String Name;
	public Float Latitude;
	public Float Longitude;
	public Integer ItemId; 
	public String Link;
	public Integer Zoom;
	public String Comment;
	public Integer Accuracy;
	public String Editable;
	
	public void setPlaceId (Integer PlaceId) {
		this.PlaceId = PlaceId;
	}

	public void setName (String Name) {
		this.Name = Name;
	}

	public void setLatitude (Float Latitude) {
		this.Latitude = Latitude;
	}

	public void setLongitude (Float Longitude) {
		this.Longitude = Longitude;
	}

	public void setItemId (Integer ItemId) {
		this.ItemId = ItemId;
	}

	public void setLink (String Link) {
		this.Link = Link;
	}

	public void setZoom (Integer Zoom) {
		this.Zoom = Zoom;
	}

	public void setComment (String Comment) {
		this.Comment = Comment;
	}

	public void setAccuracy (Integer Accuracy) {
		this.Accuracy = Accuracy;
	}
	
	public void setEditable (String Editable) {
		this.Editable = Editable;
	}
}
