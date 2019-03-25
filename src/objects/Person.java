package objects;

import java.sql.Timestamp;

public class Person {
	public Integer PersonId; 
	public String Name; 
	public String BirthPlace; 
	public Timestamp BirthDate;
	public String DeathPlace; 
	public Timestamp DeathDate;
	public String Link; 
	
	public void setPersonId (Integer PersonId) {
		this.PersonId = PersonId;
	}
	
	public void setName (String Name) {
		this.Name = Name;
	}
	
	public void setBirthPlace (String BirthPlace) {
		this.BirthPlace = BirthPlace;
	}
	
	public void setBirthDate (Timestamp BirthDate) {
		this.BirthDate = BirthDate;
	}
	
	public void setDeathPlace (String DeathPlace) {
		this.DeathPlace = DeathPlace;
	}
	
	public void setDeathDate (Timestamp DeathDate) {
		this.DeathDate = DeathDate;
	}
	
	public void setLink (String Link) {
		this.Link = Link;
	}
	

}
