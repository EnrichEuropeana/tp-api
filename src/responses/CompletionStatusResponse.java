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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.Response.ResponseBuilder;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import objects.CompletionStatus;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

@Path("/completionStatus")
public class CompletionStatusResponse {
    private static AccessTokenResponse getAccessToken(Keycloak keycloak) {
        try {
            return keycloak.tokenManager().getAccessToken();
        } catch (Exception anyException) {
            System.err.println("error getting access");
            return null;
        }
    }

	public String executeQuery(String query, String type) throws SQLException, ClientProtocolException, IOException{
			
		HttpClient httpclient = HttpClients.createDefault();
	        HttpPost httppost = new HttpPost("https://keycloak-server-test.eanadev.org/auth/realms/DataExchangeInfrastructure/protocol/openid-connect/token");
	
	        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
	        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
	        params.add(new BasicNameValuePair("client_secret", "8b81cee4-ef9a-49a0-a3ed-fd7435e2496c"));
	        params.add(new BasicNameValuePair("client_id", "tp-api-client"));
	
	        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        if (entity != null) {
	            try (InputStream instream = entity.getContent()) {
	                StringWriter writer = new StringWriter();
	                IOUtils.copy(instream, writer, StandardCharsets.UTF_8);
	                JsonObject data = new JsonParser().parse(writer.toString()).getAsJsonObject();
	                /*
	    	        HttpPost httppost2 = new HttpPost("https://fresenia.man.poznan.pl/dei-test/api/transcription?recordId=/08711/item_51775");
	    	    	
	    	
	    	        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	    	        HttpResponse response = httpclient.execute(httppost);
	    	        HttpEntity entity = response.getEntity();
	                return data.get("access_token").toString();*/
	            }
	        }
	        
		   List<CompletionStatus> completionStatusList = new ArrayList<CompletionStatus>();
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
				  CompletionStatus CompletionStatus = new CompletionStatus();
				  CompletionStatus.setCompletionStatusId(rs.getInt("CompletionStatusId"));
				  CompletionStatus.setName(rs.getString("Name"));
				  CompletionStatus.setColorCode(rs.getString("ColorCode"));
				  CompletionStatus.setColorCodeGradient(rs.getString("ColorCodeGradient"));
				  completionStatusList.add(CompletionStatus);
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
	    
	    String result = gsonBuilder.toJson(completionStatusList);
	    return result;
	}

	// Get entries
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException, ClientProtocolException, IOException {
		String query = "SELECT * FROM CompletionStatus WHERE 1";
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

	//Add new entry
	@Path("")
	@POST
	public String add(String body) throws SQLException, ClientProtocolException, IOException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    CompletionStatus completionStatus = gson.fromJson(body, CompletionStatus.class);
	    
	    //Check if all mandatory fields are included
	    if (completionStatus.CompletionStatusId != null && completionStatus.Name != null) {
			String query = "INSERT INTO CompletionStatus (CompletionStatusId, Name) "
							+ "VALUES (" + completionStatus.CompletionStatusId
							+ ", '" + completionStatus.Name + "')";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	}
	

	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException, ClientProtocolException, IOException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("CompletionStatusId") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if (changes.get("Name") == null || !changes.get("Name").isJsonNull()) {
		    String query = "UPDATE CompletionStatus SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE CompletionStatusId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited change to null";
	    }
	}
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException, ClientProtocolException, IOException {
		String resource = executeQuery("DELETE FROM CompletionStatus WHERE CompletionStatusId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException, ClientProtocolException, IOException {
		String resource = executeQuery("SELECT * FROM CompletionStatus WHERE CompletionStatusId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


