package objects;

import java.sql.Timestamp;

public class Transcription {
	public Integer TranscriptionId;
	public String Text;
	public Timestamp Timestamp;
	public Integer UserId;
	public Integer ItemId;
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
	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	public void setCurrentVersion(String currentVersion) {
		CurrentVersion = currentVersion;
	}
}
