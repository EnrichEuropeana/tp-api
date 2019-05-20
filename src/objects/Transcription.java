package objects;

public class Transcription {
	public Integer TranscriptionId;
	public String Text;
	public String Timestamp;
	public Integer UserId;
	public Integer WP_UserId;
	public Integer ItemId;
	public String CurrentVersion;
	public Integer EuropeanaAnnotationId;
	
	
	public void setTranscriptionId(Integer transcriptionId) {
		TranscriptionId = transcriptionId;
	}
	public void setText(String text) {
		Text = text;
	}
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}
	public void setUserId(Integer userId) {
		UserId = userId;
	}
	public void setWP_UserId(Integer wP_UserId) {
		WP_UserId = wP_UserId;
	}
	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	public void setCurrentVersion(String currentVersion) {
		CurrentVersion = currentVersion;
	}
	public void setEuropeanaAnnotationId(Integer europeanaAnnotationId) {
		EuropeanaAnnotationId = europeanaAnnotationId;
	}
}
