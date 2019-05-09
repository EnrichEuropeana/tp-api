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

import objects.TranscriptionProfile;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

@Path("/TranscriptionProfile")
public class TranscriptionProfileResponse {


	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<TranscriptionProfile> transcriptionProfileList = new ArrayList<TranscriptionProfile>();
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
			  TranscriptionProfile transcriptionProfile = new TranscriptionProfile();
			  transcriptionProfile.setTimestamp(rs.getTimestamp("Timestamp"));
			  transcriptionProfile.setUserId(rs.getInt("UserId"));
			  transcriptionProfile.setWP_UserId(rs.getInt("WP_UserId"));
			  transcriptionProfile.setItemId(rs.getInt("ItemId"));
			  transcriptionProfile.setAmount(rs.getInt("Amount"));
			  transcriptionProfile.setItemImageLink(rs.getString("ItemImageLink"));
			  transcriptionProfile.setItemTitle(rs.getString("ItemTitle"));
			  transcriptionProfile.setCompletionStatus(rs.getString("CompletionStatus"));
			  transcriptionProfile.setScoreType(rs.getString("ScoreType"));
			  transcriptionProfileList.add(transcriptionProfile);
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
	    String result = gsonBuilder.toJson(transcriptionProfileList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@POST
	public Response getAll(String body) throws SQLException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonTree = jsonParser.parse(body);
		JsonObject bodyObject = jsonTree.getAsJsonObject();
		String query = "SELECT  " + 
						"    * " + 
						"FROM " + 
						"    (SELECT  " + 
						"			st.Name as ScoreType, " + 
						"            s.Amount, " + 
						"            s.Timestamp, " + 
						"            s.UserId, " + 
						"            s.ItemId, " + 
						"            u.WP_UserId, " + 
						"            i.ImageLink AS ItemImageLink, " + 
						"            i.Title AS ItemTitle, " + 
						"            c.Name AS CompletionStatus " + 
						"    FROM " + 
						"        Score s " + 
						"    JOIN ScoreType st ON s.ScoreTypeId = st.ScoreTypeId " + 
						"    JOIN User u ON s.UserId = u.UserId " + 
						"    JOIN Item i ON s.ItemId = i.ItemId " + 
						"    JOIN CompletionStatus c ON i.CompletionStatusId = c.CompletionStatusId) a " + 
						"WHERE 1";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	
	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@POST
	public Response search(@Context UriInfo uriInfo, String body) throws SQLException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonTree = jsonParser.parse(body);
		JsonObject bodyObject = jsonTree.getAsJsonObject();

		String query = "SELECT  " + 
						"    * " + 
						"FROM " + 
						"    (SELECT  " + 
						"			st.Name as ScoreType, " + 
						"            s.Amount, " + 
						"            s.Timestamp, " + 
						"            s.UserId, " + 
						"            s.ItemId, " + 
						"            u.WP_UserId, " + 
						"            i.ImageLink AS ItemImageLink, " + 
						"            i.Title AS ItemTitle, " + 
						"            c.Name AS CompletionStatus " + 
						"    FROM " + 
						"        Score s " + 
						"    JOIN ScoreType st ON s.ScoreTypeId = st.ScoreTypeId " + 
						"    JOIN User u ON s.UserId = u.UserId " + 
						"    JOIN Item i ON s.ItemId = i.ItemId " + 
						"    JOIN CompletionStatus c ON i.CompletionStatusId = c.CompletionStatusId) a " + 
						"WHERE 1";

		for(String key : bodyObject.keySet()){
			String[] values = bodyObject.get(key).toString().split(",");
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
}