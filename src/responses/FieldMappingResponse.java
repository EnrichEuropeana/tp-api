package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import objects.ApiKey;
import objects.FieldMapping;

import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import com.google.gson.*;

@Path("/fieldMappings")
public class FieldMappingResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<FieldMapping> fieldMappingList = new ArrayList<FieldMapping>();
	       try (InputStream input = new FileInputStream("/home/enrich/tomcat/apache-tomcat-9.0.13/webapps/tp-api/WEB-INF/config.properties")) {

	            Properties prop = new Properties();

	            // load a properties file
	            prop.load(input);

	            // get the property value and print it out
	            final String DB_URL = prop.getProperty("DB_URL");
	            final String USER = prop.getProperty("USER");
	            final String PASS = prop.getProperty("PASS");
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
			  FieldMapping FieldMapping = new FieldMapping();
			  FieldMapping.setFieldMappingId(rs.getInt("FieldMappingId"));
			  FieldMapping.setName(rs.getString("Name"));
			  FieldMapping.setDisplayName(rs.getString("DisplayName"));
			  fieldMappingList.add(FieldMapping);
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
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(fieldMappingList);
	    return result;
	}

	public String getApiKeys() throws SQLException{
			String query = "SELECT * FROM ApiKey";
		   List<ApiKey> apiKeys = new ArrayList<ApiKey>();
	       try (InputStream input = new FileInputStream("/home/enrich/tomcat/apache-tomcat-9.0.13/webapps/tp-api/WEB-INF/config.properties")) {

	            Properties prop = new Properties();

	            // load a properties file
	            prop.load(input);

	            // get the property value and print it out
	            final String DB_URL = prop.getProperty("DB_URL");
	            final String USER = prop.getProperty("USER");
	            final String PASS = prop.getProperty("PASS");
		   // Register JDBC driver
		   try {
			Class.forName("com.mysql.jdbc.Driver");
		
		   // Open a connection
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   // Execute SQL query
		   Statement stmt = conn.createStatement();
		   ResultSet rs = stmt.executeQuery(query);
		   
		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  ApiKey apiKey = new ApiKey();
			  apiKey.setApiKeyId(rs.getInt("ApiKeyId"));
			  apiKey.setKeyString(rs.getString("KeyString"));
			  apiKey.setProjectId(rs.getInt("ProjectId"));
			  apiKey.setRoleId(rs.getInt("RoleId"));
			  apiKeys.add(apiKey);
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
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(apiKeys);
	    return result;
	}

	//Get entries
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo, String body, @Context HttpHeaders headers) throws SQLException {	
		/*
		boolean auth = false;
		String authorizationToken = "";
		if (headers.getRequestHeader(HttpHeaders.AUTHORIZATION) != null) {
			List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			authorizationToken = authHeaders.get(0);
			String tokens = getApiKeys();
			JsonArray data = new JsonParser().parse(tokens).getAsJsonArray();
			
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getAsJsonObject().get("KeyString").toString().replace("\"", "").equals(authorizationToken)) {
					auth = true;
					break;
				}
			}
		}
		
		if (auth != true) {
			ResponseBuilder authResponse = Response.status(Response.Status.UNAUTHORIZED);
			return authResponse.build();
		}
		*/
		String query = "SELECT * FROM FieldMapping WHERE 1";
		
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		
		for(String key : queryParams.keySet()){
			String[] values = queryParams.getFirst(key).split(",");
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
	

	

	//Edit entry by id
	@Path("/{id}")
	@POST
	public Response update(@PathParam("id") int id, String body, @Context HttpHeaders headers) throws SQLException {	
		boolean auth = false;
		String authorizationToken = "";
		if (headers.getRequestHeader(HttpHeaders.AUTHORIZATION) != null) {
			List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			authorizationToken = authHeaders.get(0);
			String tokens = getApiKeys();
			JsonArray data = new JsonParser().parse(tokens).getAsJsonArray();
			
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getAsJsonObject().get("KeyString").toString().replace("\"", "").equals(authorizationToken)) {
					auth = true;
					break;
				}
			}
		}
		
		if (auth != true) {
			ResponseBuilder authResponse = Response.status(Response.Status.UNAUTHORIZED);
			return authResponse.build();
		}
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("FieldMappingId") != null) {
			ResponseBuilder authResponse = Response.status(Response.Status.BAD_REQUEST);
			return authResponse.build();
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if (changes.get("Name") == null || !changes.get("Name").isJsonNull()){
		    String query = "UPDATE FieldMapping SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE FieldMappingId = " + id;
			String resource = executeQuery(query, "Update");
			ResponseBuilder rBuild = Response.ok(resource);
	        return rBuild.build();
	    } else {
			ResponseBuilder authResponse = Response.status(Response.Status.BAD_REQUEST);
			return authResponse.build();
	    }
	}
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") int id, @Context HttpHeaders headers) throws SQLException {
		boolean auth = false;
		String authorizationToken = "";
		if (headers.getRequestHeader(HttpHeaders.AUTHORIZATION) != null) {
			List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			authorizationToken = authHeaders.get(0);
			String tokens = getApiKeys();
			JsonArray data = new JsonParser().parse(tokens).getAsJsonArray();
			
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getAsJsonObject().get("KeyString").toString().replace("\"", "").equals(authorizationToken)) {
					auth = true;
					break;
				}
			}
		}
		
		if (auth != true) {
			ResponseBuilder authResponse = Response.status(Response.Status.UNAUTHORIZED);
			return authResponse.build();
		}
		String resource = executeQuery("DELETE FROM FieldMapping WHERE FieldMappingId = " + id, "Delete");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id, String body, @Context HttpHeaders headers) throws SQLException {	
		/*
		boolean auth = false;
		String authorizationToken = "";
		if (headers.getRequestHeader(HttpHeaders.AUTHORIZATION) != null) {
			List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			authorizationToken = authHeaders.get(0);
			String tokens = getApiKeys();
			JsonArray data = new JsonParser().parse(tokens).getAsJsonArray();
			
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getAsJsonObject().get("KeyString").toString().replace("\"", "").equals(authorizationToken)) {
					auth = true;
					break;
				}
			}
		}
		
		if (auth != true) {
			ResponseBuilder authResponse = Response.status(Response.Status.UNAUTHORIZED);
			return authResponse.build();
		}
		*/
		String resource = executeQuery("SELECT * FROM FieldMapping WHERE FieldMappingId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


