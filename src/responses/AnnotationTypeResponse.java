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

import objects.AnnotationType;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import com.google.gson.*;

import Utilities.TpGetPropertyValues;

@Path("/AnnotationType")
public class AnnotationTypeResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<AnnotationType> annotationTypeList = new ArrayList<AnnotationType>();
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
			  AnnotationType annotationType = new AnnotationType();
			  annotationType.setAnnotationTypeId(rs.getInt("AnnotationTypeTypeId"));
			  annotationType.setName(rs.getString("Name"));
			  annotationType.setMotivationId(rs.getString("MotivationId"));
			  annotationTypeList.add(annotationType);
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
	    String result = gsonBuilder.toJson(annotationTypeList);
	    return result;
	}

	//Search using custom filters
	
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM AnnotationType WHERE 1";
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
	    AnnotationType annotationType = gson.fromJson(body, AnnotationType.class);
	    
	    //Check if all mandatory fields are included
	    if (annotationType.Name != null && annotationType.MotivationId != null) {
			String query = "INSERT INTO AnnotationType (Name, MotivationId) "
							+ "VALUES ('" + annotationType.Name + "'"
								+ ", " + annotationType.MotivationId + ")";
			String resource = executeQuery(query, "Insert");
			ResponseBuilder rBuild = Response.ok(resource);
	        return rBuild.build();
	    } else {
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
	        return rBuild.build();
	    }
	}
	

	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("AnnotationTypeId") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Name") == null || !changes.get("Name").isJsonNull())
	    		&& (changes.get("MotivationId") == null || !changes.get("MotivationId").isJsonNull())) {
		    String query = "UPDATE AnnotationType SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = '" + changes.get(entry.getKey()).getAsString() + "'";
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE AnnotationTypeId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited change to null";
	    }
	}
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM AnnotationType WHERE AnnotationTypeId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM AnnotationType WHERE AnnotationTypeId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

}