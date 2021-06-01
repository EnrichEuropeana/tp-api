package responses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import objects.Item;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/itemMinimal")
public class ItemMinimalResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<Item> itemList = new ArrayList<Item>();
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
			  Item Item = new Item();
			  Item.setItemId(rs.getInt("ItemId"));
			  Item.setStoryId(rs.getInt("StoryId"));
			  Item.setImageLink(rs.getString("ImageLink"));
			  Item.setTitle(rs.getString("Title"));
			  Item.setDatasetId(rs.getInt("DatasetId"));
			  Item.setCompletionStatusId(rs.getInt("CompletionStatusId"));
			  Item.setTranscriptionStatusId(rs.getInt("TranscriptionStatusId"));
			  Item.setDescriptionStatusId(rs.getInt("DescriptionStatusId"));
			  Item.setLocationStatusId(rs.getInt("LocationStatusId"));
			  Item.setTaggingStatusId(rs.getInt("TaggingStatusId"));
			  Item.setAutomaticEnrichmentStatusId(rs.getInt("AutomaticEnrichmentStatusId"));
			  itemList.add(Item);
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
	    String result = gsonBuilder.toJson(itemList);
	    return result;
	}
	
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo, String body, @Context HttpHeaders headers) throws SQLException {	
		String query = "SELECT * FROM "
				+ "("
				+ "SELECT "
				+ " i.ItemId as ItemId,\r\n"
				+ " i.StoryId as StoryId,\r\n"
				+ " i.ImageLink as ImageLink,\r\n"
				+ " i.Title as Title,\r\n"
				+ " sto.DatasetId as DatasetId,\r\n"
				+ " i.CompletionStatusId as CompletionStatusId,\r\n"
				+ " i.TranscriptionStatusId as TranscriptionStatusId,\r\n"
				+ " i.DescriptionStatusId as DescriptionStatusId,\r\n"
				+ " i.LocationStatusId as LocationStatusId,\r\n"
				+ " i.TaggingStatusId as TaggingStatusId,\r\n"
				+ " i.AutomaticEnrichmentStatusId as AutomaticEnrichmentStatusId\r\n"
				+ " FROM Item i\r\n"
				+ " JOIN Story sto ON i.StoryId = sto.StoryId\r\n"
				+ " ) a "
				+ " WHERE 1";
		
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
	
