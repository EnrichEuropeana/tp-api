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

import objects.TeamUser;

import java.util.*;
import java.sql.*;

import com.google.gson.*;
import eu.transcribathon.properties.PropertiesCache;

@Path("/teamUsers")
public class TeamUserResponse {
	
	
	public String executeQuery(String query, String type) throws SQLException{
		   List<TeamUser> teamUserList = new ArrayList<TeamUser>();
		   ResultSet rs = null;
		   Connection conn = null;
		   Statement stmt = null;		   	       
		               
		   // Register JDBC driver
		   try {
			Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));
		
		   // Open a connection
		   conn = DriverManager.getConnection(
				   PropertiesCache.getInstance().getProperty("DB_URL"), 
				   PropertiesCache.getInstance().getProperty("USER"), 
				   PropertiesCache.getInstance().getProperty("PASS")
				   );
		   // Execute SQL query
		   stmt = conn.createStatement();
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
			  TeamUser teamUser = new TeamUser();
			  teamUser.setTeamId(rs.getInt("TeamId"));
			  teamUser.setUserId(rs.getInt("UserId"));
			  teamUserList.add(teamUser);
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
		}  finally {
		    try { rs.close(); } catch (Exception e) { /* ignored */ }
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
	   }
		   
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(teamUserList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getAll() throws SQLException {
		String query = "SELECT * FROM TeamUser WHERE 1";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	

	//Add new entry
	
	@POST
	public Response add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    TeamUser teamUser = gson.fromJson(body, TeamUser.class);
	    
		String query = "INSERT INTO TeamUser (TeamId, UserId) "
						+ "VALUES (" + teamUser.TeamId
								+ ", (SELECT UserId FROM User WHERE WP_UserId = " + teamUser.UserId + "))";
		String resource = executeQuery(query, "Insert");
		ResponseBuilder rBuild = Response.ok(resource);
		//ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}

	

	//Delete entry by id
	@Path("/{teamId}/{userId}")
	@DELETE
	public String delete(@PathParam("teamId") int teamId, @PathParam("userId") int userId) throws SQLException {
		String resource = executeQuery("DELETE FROM TeamUser WHERE UserId = (SELECT UserId FROM User WHERE WP_UserId = " + userId + ") AND TeamId = " + teamId, "Delete");
		return resource;
	}
	
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM TeamUser WHERE TeamUserId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM TeamUser WHERE 1";
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
}


