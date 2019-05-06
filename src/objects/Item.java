package objects;

import java.sql.Timestamp;
import java.util.List;

public class Item {
	public Integer ItemId;
	public String Title;
	public String CompletionStatusName;
	public Integer CompletionStatusId;
	public Integer ProjectItemId;
	public Integer ProjectId;
	public String Description;
	public Timestamp DateStart;
	public Timestamp DateEnd;
	public Integer DatasetId;
	public String ImageLink;
	public Integer OrderIndex;
	public Timestamp Timestamp;
	public Integer StoryId;
	public String StorydcTitle;
	public String StorydcDescription;
	public String StoryProjectStoryUrl;
	public String StoryDateStartDisplay;
	public String StoryDateEndDisplay;;
	public String StoryPlaceName;
	public Float StoryPlaceLatitude;
	public Float StoryPlaceLongitute;
	public String StoryPlaceUserGenerated;
	public String StoryContributor;
	public String StoryRights;
	public String StorySummary;
	public Integer StoryParentStory;
	public String StoryManifest;
	public String StorySearchText;
	public Timestamp StoryDateStart;
	public Timestamp StoryDateEnd;
	public Integer StoryOrderIndex;
	public String[] PropertyIds;
	public List<Property> Properties;
	public List<Place> Places;
	public List<Transcription> Transcriptions;
	public List<Annotation> Annotations;
	public List<Comment> Comments;
	
	public void setStorydcTitle(String storydcTitle) {
		StorydcTitle = storydcTitle;
	}
	public void setStorydcDescription(String storydcDescription) {
		StorydcDescription = storydcDescription;
	}
	public void setStoryProjectStoryUrl(String storyProjectStoryUrl) {
		StoryProjectStoryUrl = storyProjectStoryUrl;
	}
	public void setStoryDateStartDisplay(String storyDateStartDisplay) {
		StoryDateStartDisplay = storyDateStartDisplay;
	}
	public void setStoryDateEndDisplay(String storyDateEndDisplay) {
		StoryDateEndDisplay = storyDateEndDisplay;
	}
	public void setStoryPlaceName(String storyPlaceName) {
		StoryPlaceName = storyPlaceName;
	}
	public void setStoryPlaceLatitude(Float storyPlaceLatitude) {
		StoryPlaceLatitude = storyPlaceLatitude;
	}
	public void setStoryPlaceLongitute(Float storyPlaceLongitute) {
		StoryPlaceLongitute = storyPlaceLongitute;
	}
	public void setStoryPlaceUserGenerated(String storyPlaceUserGenerated) {
		StoryPlaceUserGenerated = storyPlaceUserGenerated;
	}
	public void setStoryContributor(String storyContributor) {
		StoryContributor = storyContributor;
	}
	public void setStoryRights(String storyRights) {
		StoryRights = storyRights;
	}
	public void setStorySummary(String storySummary) {
		StorySummary = storySummary;
	}
	public void setStoryParentStory(Integer storyParentStory) {
		StoryParentStory = storyParentStory;
	}
	public void setStoryManifest(String storyManifest) {
		StoryManifest = storyManifest;
	}
	public void setStorySearchText(String storySearchText) {
		StorySearchText = storySearchText;
	}
	public void setStoryDateStart(Timestamp storyDateStart) {
		StoryDateStart = storyDateStart;
	}
	public void setStoryDateEnd(Timestamp storyDateEnd) {
		StoryDateEnd = storyDateEnd;
	}
	public void setStoryOrderIndex(Integer storyOrderIndex) {
		StoryOrderIndex = storyOrderIndex;
	}
	public void setStoryId(Integer storyId) {
		StoryId = storyId;
	}
	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public void setCompletionStatusName(String completionStatusName) {
		CompletionStatusName = completionStatusName;
	}
	public void setCompletionStatusId(Integer completionStatusId) {
		CompletionStatusId = completionStatusId;
	}
	public void setProjectItemId(Integer projectItemId) {
		ProjectItemId = projectItemId;
	}
	public void setProjectId(Integer projectId) {
		ProjectId = projectId;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public void setDateStart(Timestamp dateStart) {
		DateStart = dateStart;
	}
	public void setDateEnd(Timestamp dateEnd) {
		DateEnd = dateEnd;
	}
	public void setDatasetId(Integer datasetId) {
		DatasetId = datasetId;
	}
	public void setImageLink(String imageLink) {
		ImageLink = imageLink;
	}
	public void setOrderIndex(Integer orderIndex) {
		OrderIndex = orderIndex;
	}
	public void setTimestamp(Timestamp timestamp) {
		Timestamp = timestamp;
	}
	public void setPropertyIds(String[] propertyIds) {
		PropertyIds = propertyIds;
	}
	public void setProperties(List<Property> properties) {
		Properties = properties;
	}
	public void setPlaces(List<Place> places) {
		Places = places;
	}
	public void setTranscriptions(List<Transcription> transcriptions) {
		Transcriptions = transcriptions;
	}
	public void setAnnotations(List<Annotation> annotations) {
		Annotations = annotations;
	}
	public void setComments(List<Comment> comments) {
		Comments = comments;
	}
	

}
