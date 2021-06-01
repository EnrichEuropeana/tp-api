package responses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.StoryPropertyList;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/storyPropertyLists")
public class StoryPropertyListResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<StoryPropertyList> storyPropertyListList = new ArrayList<StoryPropertyList>();
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
			  StoryPropertyList storyPropertyList = new StoryPropertyList();
			  storyPropertyList.setName(rs.getString("PropertyName"));
			  storyPropertyListList.add(storyPropertyList);
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
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		   }
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(storyPropertyListList);
	    return result;
	}


	//Search using custom filters
	@Path("/{property}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@PathParam("property") String property) throws SQLException {
		String query = "SELECT distinct(" + property + ") as PropertyName FROM Story WHERE " + property + " is not null";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
		//ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}
}