package objects;

import java.sql.Timestamp;

public class User {
	public Integer UserId; 
	public String Email; 
	public String Username; 
	public Timestamp Timestamp;
	public Integer RoleId; 
	public String Confirmed; 
	public String Newsletter; 
	
	public void setUserId (Integer UserId) {
		this.UserId = UserId;
	}
	
	public void setEmail (String Email) {
		this.Email = Email;
	}
	
	public void setUsername (String Username) {
		this.Username = Username;
	}
	
	public void setTimestamp (Timestamp Timestamp) {
		this.Timestamp = Timestamp;
	}
	
	public void setRoleId (Integer RoleId) {
		this.RoleId = RoleId;
	}
	
	public void setConfirmed (String Confirmed) {
		this.Confirmed = Confirmed;
	}
	
	public void setNewsletter (String Newsletter) {
		this.Newsletter = Newsletter;
	}
}
