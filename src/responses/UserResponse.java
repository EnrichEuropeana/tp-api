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

import objects.User;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/users")
public class UserResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<User> userList = new ArrayList<User>();
		   ResultSet rs = null;
		   Connection conn = null;
		   Statement stmt = null;
		   TpGetPropertyValues prop = new TpGetPropertyValues();		   	       
		   try {
	            
			String[] propArray = prop.getPropValues();			   
			   
			final String DB_URL = propArray[0];
	        final String USER = propArray[1];
	        final String PASS = propArray[2];
	        final String DRIVER = propArray[4];
		   // Register JDBC driver
				Class.forName(DRIVER);
				
				   // Open a connection
				   conn = DriverManager.getConnection(DB_URL, USER, PASS);
				   // Execute SQL query
				   stmt = conn.createStatement();
		   try {
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   stmt.close();
				   conn.close();
				   return type +" succesful";
			   }
			   else {
				   stmt.close();
				   conn.close();
				   return type +" could not be executed";
			   }
		   }
		   rs = stmt.executeQuery(query);
		   
		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  User user = new User();
			  user.setUserId(rs.getInt("UserId"));
			  user.setWP_UserId(rs.getInt("WP_UserId"));
			  user.setRoleId(rs.getInt("RoleId"));
			  user.setRole(rs.getString("Role"));
			  user.setTimestamp(rs.getTimestamp("Timestamp"));
			  user.setToken(rs.getString("Token"));
			  userList.add(user);
		   }
		
		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   } finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		    }
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		    }
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(userList);
	    return result;
	}

	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT " + 
							" u.UserId as UserId, " + 
							" u.WP_UserId as WP_UserId, " + 
							" u.RoleId as RoleId, " + 
							" r.Name as Role, " + 
							" u.Timestamp as Timestamp, " + 
							" u.Token as Token " + 
						" FROM User u " + 
						" JOIN Role r ON u.RoleId = r.RoleId " +
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
        return rBuild.build();
	}
	

	//Add new entry
	
	@POST
	public Response add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    User user = gson.fromJson(body, User.class);
	    
		String query = "INSERT INTO User (WP_UserId, RoleId, WP_Role, Token) "
						+ "VALUES (" + user.WP_UserId
						+ ", IFNULL((SELECT RoleID FROM Role WHERE Name = '" + user.Role + "'), 1)"
						+ ", '" + user.WP_Role + "'"
						+ ", '" + user.Token +"')";
		String resource = executeQuery(query, "Insert");
		ResponseBuilder rBuild = Response.ok(resource);
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
			    query += entry.getKey() + " = '" + changes.get(entry.getKey()).getAsString() + "'";
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
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String query = "SELECT " + 
				" u.UserId as UserId, " + 
				" u.WP_UserId as WP_UserId, " + 
				" u.RoleId as RoleId, " + 
				" r.Name as Role, " + 
				" u.Timestamp as Timestamp, " + 
				" u.Token as Token " + 
			" FROM User u " + 
			" JOIN Role r ON u.RoleId = r.RoleId " +
			" WHERE WP_UserId = " + id;
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

}


