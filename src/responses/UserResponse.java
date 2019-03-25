package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import objects.User;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

@Path("/User")
public class UserResponse {


	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<User> userList = new ArrayList<User>();
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
			  User user = new User();
			  user.setUserId(rs.getInt("UserId"));
			  user.setEmail(rs.getString("Email"));
			  user.setUsername(rs.getString("Username"));
			  user.setTimestamp(rs.getTimestamp("Timestamp"));
			  user.setRoleId(rs.getInt("RoleId"));
			  user.setConfirmed(rs.getString("Confirmed"));
			  user.setNewsletter(rs.getString("Newsletter"));
			  userList.add(user);
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
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(userList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@GET
	public String getAll() throws SQLException {
		String query = "SELECT * FROM User WHERE 1";
		String resource = executeQuery(query, "Select");
		return resource;
	}
	

	//Add new entry
	@Path("/add")
	@POST
	public String add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    User user = gson.fromJson(body, User.class);
	    
	    //Check if all mandatory fields are included
	    if (user.UserId != null && user.Email != null 
	    		&& user.Username != null && user.RoleId != null 
	    		&& user.Confirmed != null && user.Newsletter != null) {
			String query = "INSERT INTO User (UserId, Email, Username, RoleId, Confirmed, Newsletter) "
							+ "VALUES (" + user.UserId
							+ ", '" + user.Email + "'"
							+ ", '" + user.Username + "'"
							+ ", " + user.RoleId 
							+ ", " + user.Confirmed 
							+ ", " + user.Newsletter + ")";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	}
	

	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("UserId") != null || changes.get("Timestamp") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Email") == null || !changes.get("Email").isJsonNull())
	    		&& (changes.get("Username") == null || !changes.get("Username").isJsonNull())
	    		&& (changes.get("RoleId") == null || !changes.get("RoleId").isJsonNull())
	    		&& (changes.get("Confirmed") == null || !changes.get("Confirmed").isJsonNull())
	    		&& (changes.get("Newsletter") == null || !changes.get("Newsletter").isJsonNull())){
		    String query = "UPDATE User SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE UserId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited changes to null";
	    }
	}
	
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM User WHERE UserId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@GET
	public String getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM User WHERE UserId = " + id, "Select");
		return resource;
	}

	//Search using custom filters
	@Path("/search")
	@GET
	public String search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM Campaign WHERE 1";
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
		return resource;
	}
}


