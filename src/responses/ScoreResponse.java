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

import objects.Score;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/scores")
public class ScoreResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<Score> scoreList = new ArrayList<Score>();
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
					    if (type == "ScoreId") {
					    	// Return ScoreId as simple String
						    rs = stmt.executeQuery(query);
						    if(rs.next() == false){
							    rs.close();
				 			    stmt.close();
				 			    conn.close();
							    return "";
						    }
						    else {
						    	String result = rs.getString("ScoreId");
							    rs.close();
				 			    stmt.close();
				 			    conn.close();
							    return result;
						    }
					    }
					    else {
					    	// Execute Insert, Update or Delete queries
						    int success = stmt.executeUpdate(query);
				 		    if (success > 0) {
 							    stmt.close();
 							    conn.close();
 							    return type +" succesful";
 						    }
 						    else {
 							    stmt.close();
 							    conn.close();
							    return "";
						    }
					   }
				    }
				    // Save query results as Result set
				    rs = stmt.executeQuery(query);
				   
				    // Extract data from result set
				    while(rs.next()){
				    	//Retrieve by column name
				    	Score score = new Score();
				    	score.setScoreId(rs.getInt("ScoreId"));
				    	score.setAmount(rs.getInt("Amount"));
				    	score.setUserId(rs.getInt("UserId"));
				    	score.setScoreTypeId(rs.getInt("ScoreTypeId"));
				    	score.setScoreType(rs.getString("ScoreType"));
				    	score.setRate(rs.getFloat("Rate"));
				    	score.setItemId(rs.getInt("ItemId"));
				    	score.setTimestamp(rs.getString("Timestamp"));
				    	scoreList.add(score);
				    }
				
				    // Close connections etc
				    rs.close();
	 			    stmt.close();
	 			    conn.close();
				} catch(SQLException se) {
				    se.printStackTrace();
				    return "";
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
				e1.printStackTrace();
			}  finally {
			    try { rs.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		   }
	    // Build Json from query results
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(scoreList);
	    return result;
	}

	//Search using custom filters
	
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		// Build base query
		String query = "SELECT * FROM " +
						"(" +
							"SELECT " + 
							"	s.ScoreId as ScoreId," + 
							"	s.Amount as Amount," + 
							"   s.ItemId as ItemId," + 
							"   u.WP_UserId as UserId," + 
							"	st.ScoreTypeId as ScoreTypeId," + 
							"    st.Name as ScoreType," + 
							"    st.Rate as Rate," + 
							"    s.Timestamp as Timestamp " + 
							"FROM Score s " + 
							"JOIN ScoreType st " + 
							"ON s.ScoreTypeId = st.ScoreTypeId " +
							"JOIN User u " + 
							"ON u.UserId = s.UserId " +
						") a " + 
						"WHERE 1";
		// Get url parameters
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		
		// Add query conditions form parameters
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
		String result = executeQuery(query, "Select");

		ResponseBuilder rBuild = Response.ok(result);
        return rBuild.build();
	}
	

	//Add new entry
	
	@POST
	public Response add(String body, @Context UriInfo uriInfo) throws SQLException {	
		// Build new object
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Score score = gson.fromJson(body, Score.class);

		String query = "INSERT INTO Score (ItemId, UserId, ScoreTypeId, Amount) "
						+ "VALUES (" 
							+ score.ItemId + ""
							+ ", (SELECT UserId FROM User WHERE WP_UserId = " + score.UserId + ")"
							+ ", ("
							+	"SELECT ScoreTypeId FROM ScoreType WHERE Name = '" + score.ScoreType + "'"
							+ ")"
							+ ", " + score.Amount 
						+ ")";
		
		String result = executeQuery(query, "Insert");
		// Check if insert was successful
		if (result != "") {
			ResponseBuilder rBuild = Response.ok(result);
	        return rBuild.build();
		}
		else {
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
	        return rBuild.build();
		}
	}
	

	//Edit entry by id
	@Path("/{id}")
	@POST
	public Response update(@PathParam("id") int id, String body) throws SQLException {
		// Build new object
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    String query = "UPDATE Score SET ";
	    int keyCount = changes.entrySet().size();
	    int i = 1;
		for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
		    query += entry.getKey() + " = '" + changes.get(entry.getKey()).getAsString() + "'";
		    if (i < keyCount) {
		    	query += ", ";
		    }
		    i++;
		}
		query += " WHERE ScoreId = " + id;
		String result = executeQuery(query, "Update");
		if (result != "") {
			ResponseBuilder rBuild = Response.ok(result);
	        return rBuild.build();
		}
		else {
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
	        return rBuild.build();
		}
	}
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") int id) throws SQLException {
		String result = executeQuery("DELETE FROM Score WHERE ScoreId = " + id, "Delete");
		ResponseBuilder rBuild = Response.ok(result);
        return rBuild.build();
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String query = "SELECT * FROM " +
				"(" +
					"SELECT " + 
					"	s.ScoreId as ScoreId," + 
					"	s.Amount as Amount," + 
					"   s.ItemId as ItemId," + 
					"   u.WP_UserId as UserId," + 
					"	st.ScoreTypeId as ScoreTypeId," + 
					"    st.Name as ScoreType," + 
					"    st.Rate as Rate," + 
					"    s.Timestamp as Timestamp " + 
					"FROM Score s " + 
					"JOIN ScoreType st " + 
					"ON s.ScoreTypeId = st.ScoreTypeId " +
					"JOIN User u " + 
					"ON u.UserId = s.UserId " +
				") a " + 
				"WHERE ScoreId = " + id;
		String result = executeQuery(query, "Select");
		
		ResponseBuilder rBuild = Response.ok(result);
        return rBuild.build();
	}

}