package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.Person;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

import eu.transcribathon.properties.PropertiesCache;

@Path("/persons")
public class PersonResponse {


	public static String executeQuery(String query, String type) throws SQLException{
		   List<Person> personList = new ArrayList<Person>();
		   ResultSet rs = null;
		   Connection conn = null;
		   Statement stmt = null;
		   try {

		   // Register JDBC driver
				Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));

				   // Open a connection
				   conn = DriverManager.getConnection(
						   PropertiesCache.getInstance().getProperty("DB_URL"),
						   PropertiesCache.getInstance().getProperty("USER"),
						   PropertiesCache.getInstance().getProperty("PASS")
						   );
				   // Execute SQL query
				   stmt = conn.createStatement();
		   try {
		   if (type != "Select") {
			   if (type == "PersonId") {
				   rs = stmt.executeQuery(query);
				   if(rs.next() == false){
					   return "";
				   } else {
					   String personId = rs.getString("PersonId");
					   rs.close();
					   stmt.close();
					   conn.close();
					   return personId;
				   }
			   }
				 if (type == "lastInsertId") {
					 int insertPerson = stmt.executeUpdate(query);
					 int lastId = 0;
					 String queryOut = "Person could not inserted.";
					 ResultSet lastInsertId = stmt.executeQuery("SELECT LAST_INSERT_ID()");
					 lastInsertId.next();
					 lastId = Integer.valueOf(lastInsertId.getInt(1));
					 if (lastId > 0) {
						 queryOut = Integer.toString(lastId);
					 }
					 lastInsertId.close();
					 stmt.close();
					 conn.close();
					 return queryOut;
				 }
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   stmt.close();
				   conn.close();
				   return type +" succesful";
			   } else {
				   stmt.close();
				   conn.close();
				   return type +" could not be executed";
			   }
		   }
		   rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  Person Person = new Person();
			  Person.setPersonId(rs.getInt("PersonId"));
			  Person.setFirstName(rs.getString("FirstName"));
			  Person.setLastName(rs.getString("LastName"));
			  Person.setBirthPlace(rs.getString("BirthPlace"));
			  Person.setBirthDate(rs.getString("BirthDate"));
			  Person.setBirthDateDisplay(rs.getString("BirthDateDisplay"));
			  Person.setDeathPlace(rs.getString("DeathPlace"));
			  Person.setDeathDate(rs.getString("DeathDate"));
			  Person.setDeathDateDisplay(rs.getString("DeathDateDisplay"));
			  Person.setLink(rs.getString("Link"));
			  Person.setDescription(rs.getString("Description"));
			  Person.setPersonRole(rs.getString("PersonRole"));
			  Person.setItemId(rs.getInt("ItemId"));
			  personList.add(Person);
		   }

		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   }  finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		   }
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		   }
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(personList);
	    return result;
	}

	//Search using custom filters

	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM ("
				+ "SELECT " +
				"   ip.PersonId as PersonId,\r\n" +
				"	FirstName,\r\n" +
				"    LastName,\r\n" +
				"    BirthPLace,\r\n" +
				"    BirthDate,\r\n" +
				"    BirthDateDisplay,\r\n" +
				"    DeathPlace,\r\n" +
				"    DeathDate,\r\n" +
				"    DeathDateDisplay,\r\n" +
				"    Link,\r\n" +
				"    Description,\r\n" +
				"    PersonRole,\r\n" +
				"    ip.ItemId as ItemId\r\n" +
				"FROM Person p\r\n" +
				"JOIN ItemPerson ip 	ON p.PersonId = ip.PersonId\r\n) a " +
				" WHERE 1";
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

		for(String key : queryParams.keySet()){
			String[] values = queryParams.getFirst(key).split(",");
			query += " AND (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += key + " = '" + value + "'";
			    if (i < valueCount) {
			    	query += " OR ";
			    }
			    i++;
		    }
		    query += ")";
		}
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
		//ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}

	//Add new entry
	@POST
	public Response add(String body) throws SQLException {
	  GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	  Gson gson = gsonBuilder.create();
	  Person person = gson.fromJson(body, Person.class);

	  String rBuildString = "";

		String FirstNameCheckQuery = !person.FirstName.equals("")
			? "FirstName = '" + person.FirstName + "'"
			: "FirstName is null";
		String LastNameCheckQuery = !person.LastName.equals("")
			? "LastName = '" + person.LastName + "'"
			: "LastName is null";

    String checkQuery = "SELECT PersonId FROM Person WHERE "
    	+ FirstNameCheckQuery
    	+ " AND "
    	+ LastNameCheckQuery
    	+ " AND "
    	+ "ItemId = " + person.ItemId
			+ " LIMIT 1";

		String PersonId = executeQuery(checkQuery, "PersonId");

		if (PersonId == "") {

			String FirstName = !person.FirstName.equals("") ? "'" + person.FirstName + "'," : "null,";
			String LastName = !person.LastName.equals("") ? "'" + person.LastName + "'," : "null,";
			String BirthPlace = !person.BirthPlace.equals("") ? "'" + person.BirthPlace + "'," : "null,";
			String BirthDate = !person.BirthDate.equals("") ? "'" + person.BirthDate + "'," : "null,";
			String BirthDateDisplay = !person.BirthDateDisplay.equals("") ? "'" + person.BirthDateDisplay + "'," : "null,";
			String DeathPlace = !person.DeathPlace.equals("") ? "'" + person.DeathPlace + "'," : "null,";
			String DeathDate = !person.DeathDate.equals("") ? "'" + person.DeathDate + "'," : "null,";
			String DeathDateDisplay = !person.DeathDateDisplay.equals("") ? "'" + person.DeathDateDisplay + "'," : "null,";
			String Link = !person.Link.equals("") ? "'" + person.Link	+ "'," : "null,";
			String Description = !person.Description.equals("") ? "'" + person.Description	+ "'," : "null,";
			String PersonRole = !person.PersonRole.equals("") ? "'" + person.PersonRole	+ "'," : "null,";

			String query = "INSERT INTO Person ("
				+ "FirstName"
				+ ", LastName"
				+ ", BirthPlace"
				+ ", BirthDate"
				+ ", BirthDateDisplay"
				+ ", DeathPlace"
				+ ", DeathDate"
				+ ", DeathDateDisplay"
				+ ", Link"
				+ ", Description"
				+ ", PersonRole"
				+ ", ItemId"
			+ ") VALUES ("
				+ FirstName
				+ LastName
				+ BirthPlace
				+ BirthDate
				+ BirthDateDisplay
				+ DeathPlace
				+ DeathDate
				+ DeathDateDisplay
				+ Link
				+ Description
				+ PersonRole
				+ person.ItemId
			+ ")";

			PersonId = executeQuery(query, "lastInsertId");

			String itemPersonInsert = "INSERT INTO ItemPerson ("
				+ "ItemId"
				+ ", PersonId"
			+ ") VALUES ("
				+ person.ItemId
				+ ", " + PersonId
				+ ")";

			String itemPersonInsertResponse = executeQuery(itemPersonInsert, "Insert");

			rBuildString = itemPersonInsert;
			// rBuildString = PersonId;
		} else {
			rBuildString = "Nothing added, person already exists for this item.";
		}

		ResponseBuilder rBuild = Response.ok(rBuildString);

	  return rBuild.build();
	}

	//Edit entry by id
	@Path("/{id}")
	@POST
	public Response update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Person changes = gson.fromJson(body, Person.class);

	    String query = "UPDATE Person ";
	    		query += "SET FirstName = ";
	    		if(!changes.FirstName.equals("")) {
	    			query += "'" + changes.FirstName + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "LastName = ";
	    		if(!changes.LastName.equals("")) {
	    			query += "'" + changes.LastName + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "BirthPlace = ";
	    		if(!changes.BirthPlace.equals("")) {
	    			query += "'" + changes.BirthPlace + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}
	    		query += "BirthDate = ";
	    		if(!changes.BirthDate.equals("")) {
	    			query += "'" + changes.BirthDate + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}
	    		query += "BirthDateDisplay = ";
	    		if(!changes.BirthDateDisplay.equals("")) {
	    			query += "'" + changes.BirthDateDisplay + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "DeathPlace = ";
	    		if(!changes.DeathPlace.equals("")) {
	    			query += "'" + changes.DeathPlace + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "DeathDate = ";
	    		if(!changes.DeathDate.equals("")) {
	    			query += "'" + changes.DeathDate + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "DeathDateDisplay = ";
	    		if(!changes.DeathDateDisplay.equals("")) {
	    			query += "'" + changes.DeathDateDisplay + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "Link = ";
	    		if(!changes.Link.equals("")) {
	    			query += "'" + changes.Link + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

	    		query += "Description = ";
	    		if(!changes.Description.equals("")) {
	    			query += "'" + changes.Description + "',";
	    		}
	    		else {
	    			query += "null,";
	    		}

				query += "PersonRole = ";
	    		if(!changes.PersonRole.equals("")) {
	    			query += "'" + changes.PersonRole + "'";
	    		}
	    		else {
	    			query += "null ";
	    		}

		query += " WHERE PersonId = " + id;

		String resource = executeQuery(query, "Update");
		//ResponseBuilder rBuild = Response.ok(resource);
		ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}


	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM Person WHERE PersonId = " + id, "Delete");
		return resource;
	}


	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM ("
				+ "SELECT " +
				"   ip.PersonId as PersonId,\r\n" +
				"	FirstName,\r\n" +
				"    LastName,\r\n" +
				"    BirthPLace,\r\n" +
				"    BirthDate,\r\n" +
				"    BirthDateDisplay,\r\n" +
				"    DeathPlace,\r\n" +
				"    DeathDate,\r\n" +
				"    DeathDateDisplay,\r\n" +
				"    Link,\r\n" +
				"    Description,\r\n" +
				"    PersonRole,\r\n" +
				"    ip.ItemId as ItemId\r\n" +
				"FROM Person p\r\n" +
				"JOIN ItemPerson ip 	ON p.PersonId = ip.PersonId\r\n) a " +
				" WHERE PersonId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


