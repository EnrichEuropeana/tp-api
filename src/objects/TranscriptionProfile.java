package objects;

import java.sql.Timestamp;

public class TranscriptionProfile {
	public Integer TranscriptionId;
	public String Text;
	public Timestamp Timestamp;
	public Integer UserId;
	public Integer WP_UserId;
	public Integer ItemId;
	public String ItemImageLink;
	public String ItemTitle;
	public String CompletionStatus;
	public String CurrentVersion;
	
	
	public void setTranscriptionId(Integer transcriptionId) {
		TranscriptionId = transcriptionId;
	}
	public void setText(String text) {
		Text = text;
	}
	public void setTimestamp(Timestamp timestamp) {
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
	public void setItemImageLink(String itemImageLink) {
		ItemImageLink = itemImageLink;
	}
	public void setItemTitle(String itemTitle) {
		ItemTitle = itemTitle;
	}
	public void setCompletionStatus(String completionStatus) {
		CompletionStatus = completionStatus;
	}
}
