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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import com.google.gson.*;

@Path("/persons")
public class PersonResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<Person> personList = new ArrayList<Person>();
	       try (InputStream input = new FileInputStream("/home/enrich/tomcat/apache-tomcat-9.0.13/webapps/tp-api/WEB-INF/config.properties")) {

	            Properties prop = new Properties();

	            // load a properties file
	            prop.load(input);

	            // get the property value and print it out
	            final String DB_URL = prop.getProperty("DB_URL");
	            final String USER = prop.getProperty("USER");
	            final String PASS = prop.getProperty("PASS");
		   // Register JDBC driver
		   try {
			Class.forName("com.mysql.jdbc.Driver");
		
		   // Open a connection
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   // Execute SQL query
		   Statement stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   return type +" succesful";
			   }
			   else {
				   return type +" could not be executed";
			   }
		   }
		   ResultSet rs = stmt.executeQuery(query);
		   
		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  Person Person = new Person();
			  Person.setPersonId(rs.getInt("PersonId"));
			  Person.setFirstName(rs.getString("FirstName"));
			  Person.setLastName(rs.getString("LastName"));
			  Person.setBirthPlace(rs.getString("BirthPlace"));
			  Person.setBirthDate(rs.getString("BirthDate"));
			  Person.setDeathPlace(rs.getString("DeathPlace"));
			  Person.setDeathDate(rs.getString("DeathDate"));
			  Person.setLink(rs.getString("Link"));
			  Person.setDescription(rs.getString("Description"));
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
		   } catch (ClassNotFoundException e) {
			   e.printStackTrace();
		}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(personList);
	    return result;
	}

	//Search using custom filters
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM Person WHERE 1";
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		
		for(String key : queryParams.keySet()){
			String[] values = queryParams.getFirst(key).split(",");
			query += " AND (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += key + " = " + value;
			    if (i < valueCount) {
			    	query += " OR ";
			    }
			    i++;
		    }
		    query += ")";
		}
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}


	//Add new entry
	@Path("")
	@POST
	public Response add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Person person = gson.fromJson(body, Person.class);
	    
	    //Check if all mandatory fields are included
	    String query = "";
		query += "INSERT INTO Person (FirstName, LastName, BirthPlace, BirthDate, DeathPlace, DeathDate, Link, Description, ItemId) "
						+ "VALUES (";
		if(!person.FirstName.equals("")) {
			query += "'" + person.FirstName + "',";
		}
		else {
			query += "null,";
		}
		if(!person.LastName.equals("")) {
			query += "'" + person.LastName + "',";
		}
		else {
			query += "null,";
		}
		if(!person.BirthPlace.equals("")) {
			query += "'" + person.BirthPlace + "',";
		}
		else {
			query += "null,";
		}
		if(person.BirthDate.equals("") || person.BirthDate.equals(null)) {
			query += "'" + person.BirthDate + "',";
		}
		else {
			query += "null,";
		}
		if(person.DeathPlace.equals("") || person.DeathPlace.equals(null)) {
			query += "'" + person.DeathPlace + "',";
		}
		else {
			query += "null,";
		}
		if(person.DeathDate.equals("") || person.DeathDate.equals(null)) {
			query += "'" + person.DeathDate + "',";
		}
		else {
			query += "null,";
		}
		if(person.Link.equals("") || person.Link.equals(null)) {
			query += "'" + person.Link + "',";
		}
		else {
			query += "null,";
		}
		if(person.Description.equals("") || person.Description.equals(null)) {
			query += "'" + person.Description + "',";
		}
		else {
			query += "null,";
		}
		query += person.ItemId;
		query += ")";
		String resource = executeQuery(query, "Insert");
		//ResponseBuilder rBuild = Response.ok(resource);
		ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}


	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("PersonId") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Name") == null || !changes.get("Name").isJsonNull())){
		    String query = "UPDATE Person SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE PersonId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited change to null";
	    }
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
		String resource = executeQuery("SELECT * FROM Person WHERE PersonId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


