package objects;

import java.sql.Timestamp;

public class Person {
	public Integer PersonId; 
	public String FirstName; 
	public String LastName; 
	public String BirthPlace; 
	public Timestamp BirthDate;
	public String DeathPlace; 
	public Timestamp DeathDate;
	public String Link; 
	public String Description; 
	
	public void setPersonId (Integer PersonId) {
		this.PersonId = PersonId;
	}
	
	
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}


	public void setLastName(String lastName) {
		LastName = lastName;
	}


	public void setDescription(String description) {
		Description = description;
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
