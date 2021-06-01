package responses;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.ProfileStatistics;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/profileStatistics")
public class ProfileStatisticsResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<ProfileStatistics> profileStatisticsList = new ArrayList<ProfileStatistics>();
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
		   try {
			Class.forName(DRIVER);
		
		   // Open a connection
		   conn = DriverManager.getConnection(DB_URL, USER, PASS);
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
			  ProfileStatistics profileStatistics = new ProfileStatistics();
			  profileStatistics.setWP_UserId(rs.getInt("WP_UserId"));
			  profileStatistics.setMiles(rs.getInt("Miles"));
			  profileStatistics.setTranscriptionCharacters(rs.getInt("TranscriptionCharacters"));
			  profileStatistics.setLocations(rs.getInt("Locations"));
			  profileStatistics.setEnrichments(rs.getInt("Enrichments"));
			  profileStatistics.setDocumentCount(rs.getInt("DocumentCount"));
			  profileStatisticsList.add(profileStatistics);
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
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}  finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		   }
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(profileStatisticsList);
	    return result;
	}
	

	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("SenderId") != null || changes.get("ReceiverId") != null 
	    		|| changes.get("Timestamp") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if (changes.get("Text") == null || !changes.get("Text").isJsonNull()){
		    String query = "UPDATE ProfileStatistics SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = '" + changes.get(entry.getKey()).getAsString() + "'";
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE ProfileStatisticsId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited changes to null";
	    }
	}

	//Get entry by id
	@Path("/{WP_UserId}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("WP_UserId") int WP_UserId) throws SQLException {
		String query =  "SELECT " + 
						"    u.WP_UserId as WP_UserId," + 
						"    SUM(CASE" + 
						"        WHEN st.Name = 'Location' THEN Amount" + 
						"        ELSE 0" + 
						"    END) AS Locations," + 
						"    SUM(CASE" + 
						"        WHEN st.Name = 'Transcription' THEN Amount" + 
						"        ELSE 0" + 
						"    END) AS TranscriptionCharacters," + 
						"    SUM(CASE" + 
						"        WHEN st.Name = 'Enrichment' OR st.Name = 'Description' THEN Amount" + 
						"        ELSE 0" + 
						"    END) AS Enrichments," + 
						"    FLOOR(SUM(s.Amount * st.Rate)) AS Miles," + 
						"    COUNT(DISTINCT s.ItemId) AS DocumentCount " + 
						"FROM" + 
						"    Score s" + 
						"        JOIN" + 
						"    ScoreType st ON s.ScoreTypeId = st.ScoreTypeId" + 
						"        JOIN" + 
						"    User u ON s.UserId = u.UserId " + 
						"WHERE u.WP_UserId = " + WP_UserId;
		
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


