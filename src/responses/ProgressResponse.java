package responses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.Progress;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

import eu.transcribathon.properties.PropertiesCache;

@Path("/progress")
public class ProgressResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<Progress> progresss = new ArrayList<Progress>();
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
			  Progress progress = new Progress();
			  progress.setYear(rs.getInt("Year"));
			  progress.setMonth(rs.getInt("Month"));
			  progress.setAmount(rs.getInt("Amount"));
			  progresss.add(progress);
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
	    String result = gsonBuilder.toJson(progresss);
	    return result;
	}

	//Get entries
	@Path("transcribedcharacters")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getTranscribedCharacters(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT \r\n" + 
				"YEAR(Timestamp) as Year,\r\n" + 
				"MONTH(Timestamp) as Month,\r\n" + 
				"SUM(Amount) as Amount\r\n" + 
				"FROM Score\r\n" + 
				"GROUP BY YEAR(Timestamp), MONTH(Timestamp)";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Get entries
	@Path("documentsstarted")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getDocumentsStarted(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT \r\n" + 
				"YEAR(Timestamp) as Year,\r\n" + 
				"MONTH(Timestamp) as Month,\r\n" + 
				"count(*) as Amount\r\n" + 
				"FROM Score s\r\n" +
				"JOIN (SELECT ItemId FROM Item i WHERE CompletionStatusId != 1) i ON s.ItemId = i.ItemId " +
				"GROUP BY YEAR(Timestamp), MONTH(Timestamp)";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Get entries
	@Path("documentscompleted")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getDocumentsCompleted(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT \r\n" + 
				"YEAR(Timestamp) as Year,\r\n" + 
				"MONTH(Timestamp) as Month,\r\n" + 
				"count(*) as Amount\r\n" + 
				"FROM Score s\r\n" +
				"JOIN (SELECT ItemId FROM Item i WHERE CompletionStatusId = 4) i ON s.ItemId = i.ItemId " +
				"GROUP BY YEAR(Timestamp), MONTH(Timestamp)";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	

	//Get entries
	@Path("documentsstartedcampaign")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getDocumentsStartedCampaign(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT \r\n" + 
				"YEAR(Timestamp) as Year,\r\n" + 
				"MONTH(Timestamp) as Month,\r\n" + 
				"count(*) as Amount\r\n" + 
				"FROM Score s\r\n" +
				"JOIN (SELECT ItemId FROM Item i WHERE CompletionStatusId != 1) i ON s.ItemId = i.ItemId " +
				"GROUP BY YEAR(Timestamp), MONTH(Timestamp)";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


