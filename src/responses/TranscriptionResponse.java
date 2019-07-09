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

import objects.Transcription;

import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;

import com.google.gson.*;

@Path("/transcriptions")
public class TranscriptionResponse {


	public String executeQuery(String query, String type) throws SQLException, ParseException{
		   List<Transcription> transcriptionList = new ArrayList<Transcription>();
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

			   	/*

					HttpClient httpclient = HttpClients.createDefault();
				
				    HttpPost httppost = new HttpPost("https://keycloak-server-test.eanadev.org/auth/realms/DataExchangeInfrastructure/protocol/openid-connect/token");
			
			        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
			        params.add(new BasicNameValuePair("client_secret", "prop.getProperty("SECRET_KEY")"));
			        params.add(new BasicNameValuePair("client_id", "tp-api-client"));
			        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			        HttpResponse response = httpclient.execute(httppost);
			        HttpEntity entity = response.getEntity();
			
			        if (entity != null) {
			            try (InputStream instream = entity.getContent()) {
			                StringWriter writer = new StringWriter();
			                IOUtils.copy(instream, writer, StandardCharsets.UTF_8);
			                JsonObject data = new JsonParser().parse(writer.toString()).getAsJsonObject();

			    	        HttpPost httppost2 = new HttpPost("https://fresenia.man.poznan.pl/dei-test/api/transcription?recordId=/9200579/nn4a4jwf");
			    	        List<NameValuePair> params2 = new ArrayList<NameValuePair>(2);
			    	        httppost2.setEntity(new UrlEncodedFormEntity(params2, "UTF-8"));
			    	        String authHeader = "bearer " + data.get("access_token").toString().replace("\"", "");
			    	        httppost2.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
			    	        
			    	        HttpResponse response2 = httpclient.execute(httppost2);
			    	        HttpEntity entity2 = response2.getEntity();
			    	        if (entity2 != null) {
			    	            try (InputStream instream2 = entity2.getContent()) {
			    	                StringWriter writer2 = new StringWriter();
			    	                IOUtils.copy(instream2, writer2, StandardCharsets.UTF_8);
			    	                return writer2.toString();
			    	            }
			    	        }
			            }
			        }
		        */
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
			  Transcription transcription = new Transcription();
			  transcription.setTranscriptionId(rs.getInt("TranscriptionId"));
			  transcription.setText(rs.getString("Text"));
			  transcription.setTimestamp(rs.getString("Timestamp"));
			  transcription.setUserId(rs.getInt("UserId"));
			  transcription.setWP_UserId(rs.getInt("WP_UserId"));
			  transcription.setItemId(rs.getInt("ItemId"));
			  transcription.setCurrentVersion(rs.getString("CurrentVersion"));
			  transcription.setEuropeanaAnnotationId(rs.getInt("EuropeanaAnnotationId"));
			  transcriptionList.add(transcription);
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
	    String result = gsonBuilder.toJson(transcriptionList);
	    return result;
	}

	//Get entries
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException, ParseException {		
		String query = "SELECT * FROM ("
				+ "SELECT "
				+ "t.TranscriptionId, "
				+ "t.Text, "
				+ "t.Timestamp, "
				+ "t.UserId, "
				+ "t.ItemId, "
				+ "t.CurrentVersion, "
				+ "t.EuropeanaAnnotationId, "
				+ "u.WP_UserId "
				+ "FROM Transcription t "
				+ "JOIN User u ON t.UserId = u.UserId) a "
				+ "WHERE 1";

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
	public String update(@PathParam("id") int id, String body) throws SQLException, ParseException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("TranscriptionId") != null || changes.get("Timestamp") != null ) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Text") == null || !changes.get("Text").isJsonNull())
	    		&& (changes.get("UserId") == null || !changes.get("UserId").isJsonNull())
	    		&& (changes.get("ItemId") == null || !changes.get("ItemId").isJsonNull())) {
		    String query = "UPDATE Transcription SET ";
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE TranscriptionId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited change to null";
	    }
	}
	
	//Add new entry
	@Path("")
	@POST
	public String add(String body) throws SQLException, ParseException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Transcription transcription = gson.fromJson(body, Transcription.class);
	    
	    //Check if all mandatory fields are included
	    if (transcription.Text != null && transcription.UserId != null 
	    		&& transcription.ItemId != null && transcription.CurrentVersion != null) {
	    	if (transcription.CurrentVersion.contains("1")) {
	    		String updateQuery = "UPDATE Transcription "
	    							+ "SET CurrentVersion = b'0' "
	    							+ "WHERE ItemId = " + transcription.ItemId;
				String updateResponse = executeQuery(updateQuery, "Update");
	    	}
			String query = "INSERT INTO Transcription (Text, UserId, ItemId, CurrentVersion) "
							+ "VALUES ('" + transcription.Text + "'"
								+ ", " + transcription.UserId
								+ ", " + transcription.ItemId
								+ ", " + transcription.CurrentVersion + ")";
			String resource = executeQuery(query, "Insert");
			//return resource;
			return transcription.CurrentVersion;
	    } else {
	    	return "Fields missing";
	    }
	}
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException, ParseException {
		String resource = executeQuery("DELETE FROM Transcription WHERE TranscriptionId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException, ParseException {
		String query = "SELECT * FROM ("
				+ "SELECT "
				+ "t.TranscriptionId, "
				+ "t.Text, "
				+ "t.Timestamp, "
				+ "t.UserId, "
				+ "t.ItemId, "
				+ "t.CurrentVersion, "
				+ "t.EuropeanaAnnotationId, "
				+ "u.WP_UserId "
				+ "FROM Transcription t "
				+ "JOIN User u ON t.UserId = u.UserId) a "
				+ "WHERE TranscriptionId = " + id;
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

}