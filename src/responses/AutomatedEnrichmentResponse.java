package responses;

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

import objects.AutomatedEnrichment;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/automatedEnrichments")
public class AutomatedEnrichmentResponse {


	public static String executeQuery(String query, String type) throws SQLException{
		   List<AutomatedEnrichment> automatedEnrichments = new ArrayList<AutomatedEnrichment>();
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
			  AutomatedEnrichment automatedEnrichment = new AutomatedEnrichment();
			  automatedEnrichment.setAutomatedEnrichmentId(rs.getInt("AutomatedEnrichmentId"));
			  automatedEnrichment.setName(rs.getString("Name"));
			  automatedEnrichment.setType(rs.getString("Type"));
			  automatedEnrichment.setWikiData(rs.getString("WikiData"));
			  automatedEnrichment.setItemId(rs.getInt("ItemId"));
			  automatedEnrichments.add(automatedEnrichment);
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
	    String result = gsonBuilder.toJson(automatedEnrichments);
	    return result;
	}

	//Get entries
	
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM AutomatedEnrichment WHERE 1";
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
	    AutomatedEnrichment automatedEnrichment = gson.fromJson(body, AutomatedEnrichment.class);
	    
		String query = "INSERT INTO AutomatedEnrichment (Name, Type, ExternalId, WikiData, ItemId) "
						+ "VALUES ('" + automatedEnrichment.Name + "'"
						+ ", '" + automatedEnrichment.Type + "'"
						+ ", '" + automatedEnrichment.ExternalId + "'";
		if (!(automatedEnrichment.WikiData.length() > 0)) {
			query += ", null";
		}
		else {
			query += ", '" + automatedEnrichment.WikiData + "'";
		}
		query += ", " + automatedEnrichment.ItemId + ")";
		
		String resource = executeQuery(query, "Insert");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	
	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM AutomatedEnrichment WHERE AutomatedEnrichmentId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Delete entry by id
	@Path("/delete")
	@POST
	public Response delete(String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    AutomatedEnrichment automatedEnrichment = gson.fromJson(body, AutomatedEnrichment.class);
	    
	    String query = "DELETE FROM AutomatedEnrichment WHERE ExternalId = '" + automatedEnrichment.ExternalId + "'";
		String resource = executeQuery(query, "Delete");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	
}


