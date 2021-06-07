package responses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.Score;
import objects.TranscriptionProfile;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.*;

import eu.transcribathon.properties.PropertiesCache;

@Path("/transcriptionProfile")
public class TranscriptionProfileResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<TranscriptionProfile> transcriptionProfileList = new ArrayList<TranscriptionProfile>();
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
			  TranscriptionProfile transcriptionProfile = new TranscriptionProfile();
			  // String to Timestamp conversion
			  try {
		            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            Date date = formatter.parse(rs.getString("Timestamp"));
		            Timestamp timeStampDate = new Timestamp(date.getTime());
		            transcriptionProfile.setTimestamp(timeStampDate);
	
		        } catch (ParseException e) {
		            System.out.println("Exception :" + e);
		            return null;
		        }
			  transcriptionProfile.setItemId(rs.getInt("ItemId"));
			  transcriptionProfile.setItemTitle(rs.getString("ItemTitle"));
			  transcriptionProfile.setItemImageLink(rs.getString("ItemImageLink"));
			  transcriptionProfile.setCompletionStatus(rs.getString("CompletionStatus"));
			  transcriptionProfile.setCompletionColorCode(rs.getString("CompletionColorCode"));
			  transcriptionProfile.setProjectUrl(rs.getString("ProjectUrl"));
			  
			  //Add Scores
			  List<Score> ScoreList = new ArrayList<Score>();
			  if (rs.getString("Amount") != null) {
				  String[] ScoreAmounts = rs.getString("Amount").split(",", -1);
				  String[] ScoreTypes = rs.getString("ScoreType").split(",", -1);
				  for (int i = 0; i < ScoreAmounts.length; i++) {
					  Score score = new Score();
					  score.setAmount(Integer.parseInt(ScoreAmounts[i]));
					  score.setScoreType(ScoreTypes[i]);
					  ScoreList.add(score);
				  }
			  }
			  
			  transcriptionProfile.setScores(ScoreList);
			  
			  transcriptionProfileList.add(transcriptionProfile);
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
	    String result = gsonBuilder.toJson(transcriptionProfileList);
	    return result;
	}

	
	//Search using custom filters
	@Path("/{wp_userId}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@PathParam("wp_userId") int wp_userId, @Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT    \r\n" + 
				"				    *   \r\n" + 
				"				FROM   \r\n" + 
				"				    (SELECT      \r\n" + 
				"							i.ItemId AS ItemId,\r\n" + 
				"				            i.ImageLink AS ItemImageLink,   \r\n" + 
				"				            i.Title AS ItemTitle,   \r\n" + 
				"				            c.Name AS CompletionStatus,   \r\n" + 
				"				            c.ColorCode AS CompletionColorCode,   \r\n" + 
				"				            p.Url AS ProjectUrl   \r\n" + 
				"				    FROM   Item i  \r\n" + 
				"				    JOIN CompletionStatus c ON i.CompletionStatusId = c.CompletionStatusId   \r\n" + 
				"				    JOIN Story story ON i.StoryId = story.StoryId   \r\n" + 
				"				    JOIN Project p ON story.ProjectId = p.ProjectId) i   \r\n" + 
				"                    INNER JOIN \r\n" + 
				"                    (\r\n" + 
				"						SELECT \r\n" + 
				"							s.ItemId,\r\n" + 
				"                            GROUP_CONCAT(Amount) AS Amount,\r\n" + 
				"                            GROUP_CONCAT(ScoreType) AS ScoreType,\r\n" + 
				"							MAX(Timestamp) AS Timestamp\r\n" + 
				"						FROM\r\n" + 
				"							(\r\n" + 
				"								SELECT \r\n" + 
				"									s.ItemId,\r\n" + 
				"									SUM(s.Amount) as Amount,\r\n" + 
				"									st.Name as ScoreType,\r\n" + 
				"                                    MAX(Timestamp) AS Timestamp\r\n" + 
				"								FROM Score s \r\n" + 
				"								JOIN ScoreType st ON s.ScoreTypeId = st.ScoreTypeId \r\n" + 
				"								WHERE s.UserId = (SELECT UserId FROM User WHERE WP_UserId = " + wp_userId + ")\r\n" + 
				"								GROUP BY s.ItemId, st.Name\r\n" + 
				"							) s\r\n" + 
				"						GROUP BY s.ItemId\r\n" + 
				"						ORDER BY Timestamp DESC\r\n" + 
				"					) s ON i.ItemId = s.ItemId\r\n";

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