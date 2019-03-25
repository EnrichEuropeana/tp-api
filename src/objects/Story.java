package objects;

import java.sql.Timestamp;
import java.util.List;

public class Story {
	public Integer StoryId;
	public String dcTitle;
	public String dcDescription;
	public String ProjectItemUrl;
	public String DateStartDisplay;
	public String DateEndDisplay;;
	public String PlaceName;
	public Float PlaceLatitude;
	public Float PlaceLongitute;
	public String PlaceEditable;
	public String Contributor;
	public String Rights;
	public String Summary;
	public Integer ParentStory;
	public String Manifest;
	public String SearchText;
	public Timestamp DateStart;
	public Timestamp DateEnd;
	public Integer OrderIndex;
	public List<Item> Items;
	
	
	public void setItems(List<Item> items) {
		Items = items;
	}
	public void setStoryId(Integer storyId) {
		StoryId = storyId;
	}
	public void setdcTitle(String dcTitle) {
		this.dcTitle = dcTitle;
	}
	public void setdcDescription(String dcDescription) {
		this.dcDescription = dcDescription;
	}
	public void setProjectItemUrl(String projectItemUrl) {
		ProjectItemUrl = projectItemUrl;
	}
	public void setDateStartDisplay(String dateStartDisplay) {
		DateStartDisplay = dateStartDisplay;
	}
	public void setDateEndDisplay(String dateEndDisplay) {
		DateEndDisplay = dateEndDisplay;
	}
	public void setPlaceName(String placeName) {
		PlaceName = placeName;
	}
	public void setPlaceLatitude(Float placeLatitude) {
		PlaceLatitude = placeLatitude;
	}
	public void setPlaceLongitute(Float placeLongitute) {
		PlaceLongitute = placeLongitute;
	}
	public void setPlaceEditable(String placeEditable) {
		PlaceEditable = placeEditable;
	}
	public void setContributor(String contributor) {
		Contributor = contributor;
	}
	public void setRights(String rights) {
		Rights = rights;
	}
	public void setSummary(String summary) {
		Summary = summary;
	}
	public void setParentStory(Integer parentStory) {
		ParentStory = parentStory;
	}
	public void setManifest(String manifest) {
		Manifest = manifest;
	}
	public void setSearchText(String searchText) {
		SearchText = searchText;
	}
	public void setDateStart(Timestamp dateStart) {
		DateStart = dateStart;
	}
	public void setDateEnd(Timestamp dateEnd) {
		DateEnd = dateEnd;
	}
	public void setOrderIndex(Integer orderIndex) {
		OrderIndex = orderIndex;
	}

}
