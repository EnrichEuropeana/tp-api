package objects;

public class Person {
	public Integer PersonId;
	public String FirstName;
	public String LastName;
	public String BirthPlace;
	public String BirthDate;
	public String BirthDateDisplay;
	public String DeathPlace;
	public String DeathDate;
	public String DeathDateDisplay;
	public String Link;
	public String Description;
	public String PersonRole;
	public Integer ItemId;

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

    public void setPersonRole(String personRole) {
		PersonRole = personRole;
	}

	public void setBirthPlace (String BirthPlace) {
		this.BirthPlace = BirthPlace;
	}

	public void setBirthDate (String BirthDate) {
		this.BirthDate = BirthDate;
	}

	public void setBirthDateDisplay (String BirthDateDisplay) {
		this.BirthDateDisplay = BirthDateDisplay;
	}

	public void setDeathPlace (String DeathPlace) {
		this.DeathPlace = DeathPlace;
	}

	public void setDeathDate (String DeathDate) {
		this.DeathDate = DeathDate;
	}

	public void setDeathDateDisplay (String DeathDateDisplay) {
		this.DeathDateDisplay = DeathDateDisplay;
	}

	public void setLink (String Link) {
		this.Link = Link;
	}

	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}


}
